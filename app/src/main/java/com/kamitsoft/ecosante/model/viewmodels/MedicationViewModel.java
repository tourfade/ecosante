package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;

import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.MedicationInfo;
import com.kamitsoft.ecosante.model.repositories.DocumentsRepository;
import com.kamitsoft.ecosante.model.repositories.MedicationsRepository;

import java.sql.Timestamp;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MedicationViewModel extends AndroidViewModel {
    private MedicationsRepository  repository;
    private LiveData<List<MedicationInfo>> model;

    public MedicationViewModel(Application app){
        super(app);
        repository = new MedicationsRepository(app);
        model = repository.getPatientMedications();
    }

    public LiveData<List<MedicationInfo>> getEncounterMedications() {
        if (model == null) {
            model = new MutableLiveData<>();
        }
        return model;
    }

    public void insert(MedicationInfo doc){
        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.insert(doc);
    }
    public void update(MedicationInfo doc){
        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.update(doc);
    }
    public void delete(MedicationInfo doc){
        repository.delete(doc);
    }
}


