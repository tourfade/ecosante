package com.kamitsoft.ecosante.client.patient.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.patient.Encounter;
import com.kamitsoft.ecosante.constant.StatusConstant;
import com.kamitsoft.ecosante.model.EncounterHeaderInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.json.ExtraData;
import com.kamitsoft.ecosante.model.json.Status;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class EncounterHistoryDialog extends DialogFragment {


    private final EncounterHeaderInfo encounter;
    private AlertDialog.Builder historyDialogBuilder;
    private LinearLayout historyLines;


    public EncounterHistoryDialog(@NonNull EncounterHeaderInfo encounter) {
        this.encounter = encounter;

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        historyDialogBuilder = new AlertDialog.Builder(getActivity());
        historyDialogBuilder.setTitle(getString(R.string.e_history));
        historyDialogBuilder.setCancelable(true);
        historyDialogBuilder.setView(R.layout.history_dialog);
        historyDialogBuilder.setIcon(R.drawable.setting);

        historyDialogBuilder.setNegativeButton("Fermer", (dialog, which) -> {
            dialog.cancel();
        });

        AlertDialog alertDialog = historyDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            historyLines = ((AlertDialog)dialog).findViewById(R.id.history_container);
            initValues();
        });
        alertDialog.show();
        return alertDialog;
    }




    void initValues() {
        historyLines.removeAllViews();
        for (Status s :encounter.getStatus()) {
            TextView line = new TextView(getContext());
            line.setBackgroundResource(R.drawable.border_bottom);
            line.setText(Utils.format(s.date)+ "-> "+ getString(StatusConstant.ofStatus(s.status).name) +" "+Utils.niceFormat(s.author));
            historyLines.addView(line);
        }



    }


}


