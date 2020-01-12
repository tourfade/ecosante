package com.kamitsoft.ecosante.client.patient.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.patient.oracles.PhysistOracleAdapter;
import com.kamitsoft.ecosante.constant.AppointmentRequestStatus;
import com.kamitsoft.ecosante.model.AppointmentInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.PhysicianInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.AppointmentsViewModel;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;

import java.sql.Timestamp;
import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class ApptRequestorDialog extends DialogFragment {
    private  boolean readyForNew;
    private  AppointmentInfo current;
    private  final Calendar dateTime = Calendar.getInstance();
    private  EditText  details, place, date ;

    private AppointmentsViewModel model;
    private ImageButton locateMe;
    private AlertDialog.Builder alertDialogBuilder;
    private EcoSanteApp app;
    private PatientInfo currentPatient;
    private UserInfo currentUser;
    PhysistOracleAdapter physistOracle;
    private AutoCompleteTextView doctor;
    private UsersViewModel usermodel;

    public ApptRequestorDialog(boolean isNew, AppointmentInfo appointmentInfo){
        this.readyForNew = isNew;
        if(appointmentInfo != null ){
            current = appointmentInfo;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        app = (EcoSanteApp)getActivity().getApplication();
        physistOracle = new PhysistOracleAdapter(getActivity());
        // Use the Builder class for convenient dialog construction
        model = ViewModelProviders.of(this).get(AppointmentsViewModel.class);
        usermodel = ViewModelProviders.of(this).get(UsersViewModel.class);
        if(readyForNew){
            current = model.newAppointment();
            currentPatient = app.getCurrentPatient();
            current.setPatientUuid(currentPatient.getUuid());
            current.setUserObject(getString(R.string.encounter_with)+" "+Utils.formatPatient(getContext(),currentPatient));

            currentUser = app.getCurrentUser();
            current.setUserRequestorUuid(currentUser.getUuid());
        }

        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(readyForNew || current==null? getString(R.string.new_appointment_request):current.getUserObject());

        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(R.layout.appointment_requestor_dialog);
        alertDialogBuilder.setIcon(R.drawable.appointment);
        alertDialogBuilder.setPositiveButton(readyForNew?"Ajouter":"Modifier",(dialog, which)->{
                current.setRequestLatestDate(new Timestamp(dateTime.getTimeInMillis()));
                current.setDetails(details.getText().toString().trim());
                current.setPlace(place.getText().toString().trim());
                current.setStatus(AppointmentRequestStatus.PENDING.status);
                current.setPatient(Utils.formatPatient(getContext(),currentPatient));
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
        doctor = view.findViewById(R.id.doctor);
        doctor.setAdapter(physistOracle);
        date = view.findViewById(R.id.latest_date);
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
        locateMe.setOnClickListener(this::locateMe);
        doctor.setOnItemClickListener(this::doctorSelect);
    }

    private void doctorSelect(AdapterView<?> adapterView, View view, int position, long id) {
        PhysicianInfo physist = physistOracle.getItem(position);
        current.setUserType(physist.userType);
        current.setSpeciality(physist.speciality);
        current.setRecipientUuid(physist.uuid);
        current.setPatientObject(getString(R.string.encounter_with)+" "+Utils.formatUser(getContext(),physist));
        current.setRecipient(Utils.formatUser(getContext(),physist));
        doctor.setText(current.getRecipient());

    }

    void initValues(){
        if(date == null || current == null) return;
        Calendar calendar = Calendar.getInstance();
        if(current.getDate() != null)
            calendar.setTimeInMillis(current.getDate().getTime());

        date.setText(Utils.formatDateWithDayOfWeek(getContext(),calendar));
        details.setText(Utils.niceFormat(current.getDetails()));
        place.setText(Utils.niceFormat(current.getPlace()));
        doctor.setText(current.getRecipient());

    }

    private void locateMe(View view) {

    }


    public void selectDate(final View v) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, month);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date.setText(Utils.niceFormat(Utils.format(dateTime.getTime())));
        },  dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH) );
        datePickerDialog.setTitle(R.string.select_latest_date);
        datePickerDialog.show();
    }
}
