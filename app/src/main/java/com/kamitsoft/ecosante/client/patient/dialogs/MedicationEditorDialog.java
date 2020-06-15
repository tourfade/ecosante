package com.kamitsoft.ecosante.client.patient.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.patient.oracles.DrugOracleAdapter;
import com.kamitsoft.ecosante.constant.MedicationStatus;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.Drug;
import com.kamitsoft.ecosante.model.MedicationInfo;
import com.kamitsoft.ecosante.model.viewmodels.MedicationViewModel;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class MedicationEditorDialog  extends DialogFragment implements View.OnClickListener {
    private  boolean readyForNew;
    private  MedicationInfo curentMedication;
    private  DrugOracleAdapter autoCompleteAdapter;
    private  final Calendar sdCalendar = Calendar.getInstance();
    private  final Calendar edCalendar = Calendar.getInstance();
    private  AutoCompleteTextView drug;
    private  EditText directions, startingDate, endingDate;
    private  AppCompatSpinner renewal, status;
    private  Drug selectedDrug;
    private  MedicationStatus medStatus;
    private  MedicationViewModel medModel;
    private  OnSaving completion;
    private  EcoSanteApp app;

    public MedicationEditorDialog(MedicationInfo med, boolean isNew, OnSaving completion){
        this.curentMedication = med;
        this.readyForNew = isNew;
        curentMedication.setNeedUpdate(true);
        this.completion = completion;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        medModel = ViewModelProviders.of(this).get(MedicationViewModel.class);
        app = (EcoSanteApp) (getActivity().getApplication());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(readyForNew? getString(R.string.medication):curentMedication.getDrugName());
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(R.layout.medication_dialog);
        alertDialogBuilder.setIcon(R.drawable.drug);
        alertDialogBuilder.setPositiveButton(readyForNew?R.string.add:R.string.update,(dialog, which)->{
            curentMedication.setDrugName(drug.getText().toString());
            curentMedication.setDirection(directions.getText().toString());
            curentMedication.setStartingDate(new Timestamp(sdCalendar.getTimeInMillis()));
            curentMedication.setRenewal(renewal.getSelectedItemPosition());
            if(selectedDrug != null) {
                curentMedication.setDrugName(Utils.niceFormat(selectedDrug));
                curentMedication.setDrugNumber(selectedDrug.getDrugnumber());
            }else {
                curentMedication.setDrugName(drug.getText().toString());
                curentMedication.setDrugNumber(-1);
            }
            curentMedication.setStatus(medStatus.status);
            curentMedication.setEndingDate(medStatus == MedicationStatus.TERMINATED? new Timestamp(edCalendar.getTimeInMillis()):null);
            curentMedication.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            if(completion!=null){
                completion.saving();
            }
            if(readyForNew) {
                medModel.insert(curentMedication);
            }else {
                medModel.update(curentMedication);
            }

            //lab.scrollToPosition(0);
        });
        alertDialogBuilder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            initvariables((AlertDialog)dialog);
            initListeners();
            initValues();
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
        return alertDialog;
    }
    void initvariables(AlertDialog view){

        startingDate = view.findViewById(R.id.starting_date);
        endingDate = view.findViewById(R.id.ending_date);
        drug  = view.findViewById(R.id.drug);
        directions = view.findViewById(R.id.directions);
        status = view.findViewById(R.id.status);
        renewal = view.findViewById(R.id.renewal);
        autoCompleteAdapter  = new DrugOracleAdapter(getActivity());
        drug.setAdapter(autoCompleteAdapter);

    }
    void initListeners(){

        startingDate.setOnClickListener(this);
        endingDate.setOnClickListener(this);
        drug.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDrug = autoCompleteAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDrug = null;
            }
        });
        status.setAdapter(MedicationStatus.getAdapter(getContext(), app.getCurrentUser().getUserType()));
        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                medStatus = MedicationStatus.atIndex(position);
                endingDate.setEnabled(medStatus == MedicationStatus.TERMINATED);

                if(MedicationStatus.TERMINATED != medStatus){
                    endingDate.setText("N/A");
                }
                if(medStatus == MedicationStatus.NEW){
                    renewal.setEnabled(true);
                }else{
                    renewal.setEnabled(false);
                    renewal.setSelection(0);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void initValues(){
        if(readyForNew && UserType.isNurse(app.getCurrentUser().getUserType())){
            curentMedication.setStatus(MedicationStatus.RUNNING.status);
        }
        medStatus = MedicationStatus.ofStatus(curentMedication.getStatus());

        drug.setText(Utils.niceFormat(curentMedication.getDrugName()));
        directions.setText(Utils.niceFormat(curentMedication.getDirection()));

        startingDate.setText(Utils.format(curentMedication.getStartingDate()));
        status.setSelection(medStatus.index);
        renewal.setSelection(curentMedication.getRenewal());
        endingDate.setText(medStatus!= MedicationStatus.TERMINATED? Utils.format(curentMedication.getEndingDate()):"N/A");

    }

    @Override
    public void onClick(final View v) {
        Calendar current;
        if(v.getId() == R.id.starting_date){
            current = sdCalendar;
        }else{
            current = edCalendar;
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (vie, year, month, dayOfMonth) -> {
            current.set(Calendar.YEAR, year);
            current.set(Calendar.MONTH, month);
            current.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            ((EditText)v).setText(Utils.niceFormat(Utils.format(current.getTime())));

        },  current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH) );

        if(v.getId() == R.id.starting_date){
            if(readyForNew && UserType.isNurse(app.getCurrentUser().getUserType())){
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            }
        }else{
            if(readyForNew && UserType.isNurse(app.getCurrentUser().getUserType())){
                datePickerDialog.getDatePicker().setMinDate(sdCalendar.getTimeInMillis());
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());

            }
        }

        datePickerDialog.show();
    }
}
