package com.kamitsoft.ecosante.client.patient.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.AppointmentRequestStatus;
import com.kamitsoft.ecosante.model.AppointmentInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.AppointmentsViewModel;

import java.sql.Timestamp;
import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class ApptEditorDialog extends DialogFragment {
    private  boolean readyForNew;
    private  AppointmentInfo current;
    private  final Calendar dateTime = Calendar.getInstance();
    private  EditText  details, place, date, time ;

    private AppointmentsViewModel model;
    private ImageButton locateMe;
    private AlertDialog.Builder alertDialogBuilder;
    private EcoSanteApp app;
    private PatientInfo currentPatient;
    private UserInfo currentUser;

    public ApptEditorDialog(boolean isNew, AppointmentInfo appointmentInfo){
        this.readyForNew = isNew;
        if(appointmentInfo != null ){
            current = appointmentInfo;
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        app = (EcoSanteApp)getActivity().getApplication();
        // Use the Builder class for convenient dialog construction
        model = ViewModelProviders.of(this).get(AppointmentsViewModel.class);
        if(readyForNew){
            current = model.newAppointment();
            currentPatient = app.getCurrentPatient();
            current.setPatientUuid(currentPatient.getUuid());

            currentUser = app.getCurrentUser();
            current.setRecipientUuid(currentUser.getUuid());
            current.setUserType(currentUser.getUserType());
            current.setUserRequestorUuid(currentUser.getUuid());
            current.setSpeciality(currentUser.getSpeciality());
            current.setStatus(AppointmentRequestStatus.ACCEPTED.status);
            current.setPatient(Utils.formatPatient(getContext(),currentPatient));
            current.setPatientObject(getString(R.string.encounter_with)+" "+Utils.formatUser(getContext(),currentUser));
            current.setUserObject(getString(R.string.encounter_with)+" "+Utils.formatPatient(getContext(),currentPatient));


        }

        alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setTitle(readyForNew || current==null? getString(R.string.new_appointment):current.getUserObject());

        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(R.layout.appointment_dialog);
        alertDialogBuilder.setIcon(R.drawable.appointment);
        alertDialogBuilder.setPositiveButton(readyForNew?"Ajouter":"Modifier",(dialog, which)->{
                current.setDate(new Timestamp(dateTime.getTimeInMillis()));
                current.setDetails(details.getText().toString().trim());
                current.setPlace(place.getText().toString().trim());
                model.insert(current);

        });
        alertDialogBuilder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            initvariables((AlertDialog)dialog);
            initListeners();
            if(current != null){
                model.setCurrentAppointment(current);
            }
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
        return alertDialog;
    }
    void initvariables(AlertDialog view){

        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);

        details = view.findViewById(R.id.details);
        place = view.findViewById(R.id.place);
        locateMe = view.findViewById(R.id.locateme);
        model.getCurrentAppointment().observe(this,appointment->{
            current = appointment;
            initValues();
        });

    }
    void initListeners(){
        date.setOnClickListener(this::selectDate);
        time.setOnClickListener(this::selectTime);
        locateMe.setOnClickListener(this::locateMe);
    }

    void initValues(){
        if(date == null || current == null) return;
        Calendar calendar = Calendar.getInstance();
        if(current.getDate() != null)
            calendar.setTimeInMillis(current.getDate().getTime());
        time.setText(Utils.formatTime(calendar));
        date.setText(Utils.formatDateWithDayOfWeek(getContext(),calendar));
        details.setText(Utils.niceFormat(current.getDetails()));
        place.setText(Utils.niceFormat(current.getPlace()));
    }

    private void locateMe(View view) {

    }

    public void selectTime(final View v) {

        TimePickerDialog datePickerDialog = new TimePickerDialog(getActivity(), (view, hourOfDay, minute) -> {
            dateTime.set(Calendar.HOUR, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            dateTime.set(Calendar.SECOND, 0);
            dateTime.set(Calendar.MILLISECOND, 0);
            time.setText(Utils.formatTime(dateTime));

        }, dateTime.get(Calendar.HOUR),dateTime.get(Calendar.MINUTE),true);
        datePickerDialog.setTitle(R.string.select_time);
        datePickerDialog.show();
    }

    public void selectDate(final View v) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date.setText(Utils.niceFormat(Utils.format(dateTime.getTime())));

        },  dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH) );
        datePickerDialog.setTitle(R.string.select_date);
        datePickerDialog.show();
    }
}
