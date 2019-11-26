package com.kamitsoft.ecosante.client.patient.dialogs;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.model.json.ExtraData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.DialogFragment;

public class FallsEditorDialog extends DialogFragment implements View.OnLongClickListener {
    private final boolean editable;
    private final AppCompatCheckBox source;

    private ExtraData rootData;
    private EditText notes, date;

    private final Calendar calendar = Calendar.getInstance();
    private AlertDialog.Builder alertDialogBuilder;
     private LinearLayout falls;


    public FallsEditorDialog(@NonNull ExtraData root, AppCompatCheckBox buttonView, boolean editable) {
        this.rootData = root;
        if (rootData.items == null) {
            rootData.items = new ArrayList<>();
        }
        this.editable = editable;
        this.source = buttonView;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.falls));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(R.layout.falls_dialog);
        alertDialogBuilder.setIcon(R.drawable.fall);

        alertDialogBuilder.setNegativeButton("Fermer", (dialog, which) -> {

            dialog.cancel();

        });
        if(editable) {
            alertDialogBuilder.setPositiveButton("Ajouter", (dialog, which) -> {});
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            initVars((AlertDialog) dialog);
            initListeners((AlertDialog) dialog);
            initValues();
        });
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
        return alertDialog;
    }

    void initVars(AlertDialog d) {
        date = d.findViewById(R.id.fall_date);
        date.setEnabled(editable);
        notes = d.findViewById(R.id.notes);
        notes.setEnabled(editable);
        falls = d.findViewById(R.id.falls);
    }

    void initListeners(AlertDialog d) {
        if (editable) {
            date.setOnClickListener(v -> Utils.manageDataPicker(getContext(), date, calendar));

            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                if(Utils.isNullOrEmpty(notes.getText().toString())) {
                    notes.setError("Décrire brièvement");
                    return;
                }
                if(calendar.getTime().after(new Date())){
                    date.setError("Mauvaise date");
                    return;
                }
                ExtraData ed = new ExtraData();
                rootData.items.add(ed);
                ed.date = calendar.getTimeInMillis();
                ed.name = notes.getText().toString();
                rootData.items.sort((ex1, ex2) -> ex1.date > ex2.date ? 1 : -1);
                initValues();

            });
        }
        d.findViewById(R.id.editor).setVisibility(editable?View.VISIBLE:View.GONE);

    }

    void initValues() {
        falls.removeAllViews();
        for (ExtraData ex : rootData.items) {
            View view = getLayoutInflater().inflate(R.layout.summary_item_view, null);
            ((TextView) view.findViewById(R.id.date)).setText(Utils.format(new Date(ex.date)));
            ((TextView) view.findViewById(R.id.name)).setText(Utils.niceFormat(ex.name));
            view.setTag(ex);
            view.setOnLongClickListener(this);
            falls.addView(view);
        }
        notes.setText("");
        calendar.setTimeInMillis(System.currentTimeMillis());
        date.setText(Utils.format(calendar.getTime()));


        if (rootData.items.size() > 0) {
            source.setChecked(true);
            ExtraData last = rootData.items.get(rootData.items.size() -1);
            source.setText(rootData.items.size() +" chutes. dernier -> le " + Utils.format(new Date(last.date)));
        } else {
            source.setChecked(false);
            source.setText("");
        }


    }

    @Override
    public boolean onLongClick(View v) {
        View verso = v.findViewById(R.id.item_delete);
        int width = verso.getRight() - verso.getLeft();
        AnimatorSet anset = new AnimatorSet();
        anset.play(ObjectAnimator
                .ofInt(v.findViewById(R.id.insider), "scrollX", 0)
                .setDuration(200))
                .after(3000)
                .after(ObjectAnimator
                        .ofInt(v.findViewById(R.id.insider), "scrollX", width)
                        .setDuration(200));
        anset.start();
        verso.setOnClickListener(vv -> {
            rootData.items.remove(v.getTag());
            initValues();
        });
        return false;
    }
}


