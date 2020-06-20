package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.SummaryInfo;
import com.kamitsoft.ecosante.model.repositories.LabsRepository;
import com.kamitsoft.ecosante.model.repositories.PatientsRepository;

import java.sql.Timestamp;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class PatientsViewModel extends AndroidViewModel {
    private PatientsRepository repository;
    private LiveData<List<PatientInfo>> data;
    private MutableLiveData<PatientInfo> currentPatient;
    private LiveData<SummaryInfo> currentSummary;
    public PatientsViewModel(Application app){
        super(app);
        currentPatient = ((EcoSanteApp) app).getCurrentLivePatient();
        repository = new PatientsRepository(app);
        data = repository.getAllData();
    }

    public void setCurrentPatient(PatientInfo p) {
        this.currentPatient.setValue(p); 
    }

    public MutableLiveData<PatientInfo> getCurrentPatient() {
        return currentPatient;
    }

    public LiveData<List<PatientInfo>> getAllDatas() {
        if (data == null) {
            data = new MutableLiveData<>();
        }
        return data;
    }

    public void insert(PatientInfo bean){
        bean.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.insert(bean);
    }
    public void update(PatientInfo bean){
        bean.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.update(bean);
    }
    public void delete(PatientInfo bean){
        repository.delete(bean);
    }

    public void updateSummary(SummaryInfo bean) {
        bean.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.updateSummaries(bean);
    }

    public LiveData<SummaryInfo> getCurrentSummary(String patientUUID) {
        currentSummary = repository.getSummary(patientUUID);

        return currentSummary;
    }


    public LiveData<Integer> getEncounterCounts(String puuid) {
        return repository.encounterCounts(puuid);
    }
    public LiveData<Integer> getDocumentCounts(String puuid) {
        return repository.docCount(puuid);
    }
    public LiveData<Integer> getAppointmentCounts(String puuid) {
        return repository.appCount(puuid);
    }
}


