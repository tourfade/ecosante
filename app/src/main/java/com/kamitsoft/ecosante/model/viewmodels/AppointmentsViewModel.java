package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.model.AppointmentInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.repositories.AppointmentsRepository;

import java.sql.Timestamp;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AppointmentsViewModel extends AndroidViewModel {
    private AppointmentsRepository repository;
    private LiveData<List<AppointmentInfo>> userData, patientData;
    private LiveData<AppointmentInfo> current;
    public AppointmentsViewModel(Application app){
        super(app);
        repository = new AppointmentsRepository(app);

        userData = repository.getUserData();

    }
    public LiveData<List<AppointmentInfo>> getPatientData() {
        PatientInfo pi = ((EcoSanteApp) getApplication()).getCurrentPatient();
        if(patientData == null && pi !=null){
             patientData = repository.getPatientAppointments(pi == null?null:pi.getUuid());
        }
        return patientData;
    }

    public LiveData<List<AppointmentInfo>> getUserData() {

        return userData;
    }

    public void insert(AppointmentInfo bean){
        bean.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        bean.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.insert(bean);
    }
    public void update(AppointmentInfo bean){
        bean.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.update(bean);
    }
    public void delete(AppointmentInfo bean){
        repository.delete(bean);
    }
    public LiveData<AppointmentInfo> getCurrentAppointment() {
        if(current == null) {
            current = new MutableLiveData<>();
        }
        return current;
    }
    public AppointmentInfo newAppointment() {
        if(current == null) {
            current = new MutableLiveData<>();
        }
        AppointmentInfo ai = new AppointmentInfo();
        ((MutableLiveData) current).setValue(ai);
       return ai;
    }

    public void setCurrentAppointment(AppointmentInfo item) {
        if(current == null) {
            current = new MutableLiveData<>();
        }

        ((MutableLiveData) current).setValue(item);
    }


}


