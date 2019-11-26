package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;

import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.repositories.DocumentsRepository;
import com.kamitsoft.ecosante.model.repositories.LabsRepository;

import java.sql.Timestamp;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LabsViewModel extends AndroidViewModel {
    private LabsRepository repository;
    private LiveData<List<LabInfo>> data;

    public LabsViewModel(Application app){
        super(app);
        repository = new LabsRepository(app);
        data = repository.getEncounterLabs();
    }

    public LiveData<List<LabInfo>> getEncounterLabs() {
        if (data == null) {
            data = new MutableLiveData<>();
        }
        return data;
    }
    public void insert(LabInfo doc){
        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.insert(doc);
    }
    public void update(LabInfo doc){
        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.update(doc);
    }
    public void delete(LabInfo doc){
        repository.delete(doc);
    }
}


