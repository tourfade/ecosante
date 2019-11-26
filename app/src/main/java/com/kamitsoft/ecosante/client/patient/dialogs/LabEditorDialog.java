package com.kamitsoft.ecosante.client.patient.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.adapters.LabsAdapter;
import com.kamitsoft.ecosante.client.patient.oracles.AnalysisOracleAdapter;
import com.kamitsoft.ecosante.constant.LabType;
import com.kamitsoft.ecosante.model.Analysis;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.viewmodels.LabsViewModel;

import java.sql.Timestamp;
import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class LabEditorDialog extends DialogFragment {
    private  boolean readyForNew;
    private LabsAdapter labsAdapter;
    private  LabInfo curentLab;
    private  EditText notes, labValue;
    private  CheckBox done;
    private  AppCompatSpinner labType;
    private  ImageView icon;

    private AnalysisOracleAdapter analysisOracle;
    private  final Calendar calendar = Calendar.getInstance();
    private AutoCompleteTextView labName;
    private Analysis analys;
    private LabsViewModel labsModel;

    public LabEditorDialog(LabInfo labs, boolean isNew){
        this.curentLab = labs;
        this.readyForNew = isNew;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        analysisOracle = new AnalysisOracleAdapter(getActivity());

        labsModel = ViewModelProviders.of(this).get(LabsViewModel.class);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(readyForNew? R.string.newLabs:LabType.ofType(curentLab.getType()).title);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(LabType.OTHERLAB.pickerLayout);
        alertDialogBuilder.setIcon(R.drawable.labs);
        alertDialogBuilder.setPositiveButton(readyForNew?"Ajouter":"Modifier",(dialog, which)->{
            curentLab.setNotes(notes.getText().toString());
            curentLab.setDone(done.isChecked());
            if(analys != null) {
                curentLab.setLabName(analys.getLabName());
                curentLab.setLabNumber(analys.getLabNumber());

            }else{
                curentLab.setLabName(labName.getText().toString());
                curentLab.setLabNumber(0);
            }

            curentLab.setDoneDate(done.isChecked()?new Timestamp(calendar.getTimeInMillis()):null);
            curentLab.setLabValue(Utils.floatFromEditText(labValue));
            curentLab.setType(LabType.atIndex(labType.getSelectedItemPosition()).type);
            curentLab.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            if(readyForNew) {
                labsModel.insert(curentLab);
            }else {
                labsModel.update(curentLab);
            }
        });
        alertDialogBuilder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            initVars((AlertDialog)dialog);
            initListeners();
            initValues();
        });
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return alertDialog;
    }

    void initVars(AlertDialog d) {

        notes = d.findViewById(R.id.notes);
        done = d.findViewById(R.id.lab_done);
        labName = d.findViewById(R.id.labName);
        labValue = d.findViewById(R.id.labValue);
        labType = d.findViewById(R.id.labType);
        icon = d.findViewById(R.id.icon);
        labName.setAdapter(analysisOracle);
    }
    void initListeners() {
        done.setOnCheckedChangeListener((buttonView, isChecked) -> {
            labValue.setEnabled(isChecked);
            if(isChecked) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (vie, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    done.setText("Le "+Utils.niceFormat(Utils.format(calendar.getTime())));

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }else {
                done.setText("");
            }

        });


        labName.setOnItemClickListener((parent, view, position, id) -> {
            analys = analysisOracle.getItem(position);
            labName.setText(analys.getLabName());
            labType.setSelection(LabType.labNumberOf(analys.getLabNumber()).index);
        });

        labType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                icon.setImageResource(LabType.atIndex(position).icon);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                icon.setImageResource(LabType.OTHERLAB.icon);

            }
        });


    }
    void initValues(){

        notes.setText(Utils.niceFormat(curentLab.getNotes()));
        done.setChecked(curentLab.isDone());
        done.setText(curentLab.isDone()?"Le "+Utils.format(curentLab.getDoneDate()):"");
        labName.setText(Utils.niceFormat(curentLab.getLabName()));
        labValue.setText(Utils.niceFormat(curentLab.getLabValue()));
        labType.setSelection(readyForNew? LabType.OTHERLAB.index: LabType.indexOf(curentLab.getType()));

    }
}
