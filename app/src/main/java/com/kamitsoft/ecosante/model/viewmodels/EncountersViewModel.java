package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.constant.StatusConstant;
import com.kamitsoft.ecosante.model.ECounterItem;
import com.kamitsoft.ecosante.model.EncounterHeaderInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.json.Status;
import com.kamitsoft.ecosante.model.repositories.EncountersRepository;
import com.kamitsoft.ecosante.services.ApiSyncService;

import java.sql.Timestamp;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class EncountersViewModel extends AndroidViewModel {
    private EncountersRepository  repository;
    private LiveData<List<EncounterInfo>> document;
    private LiveData<List<EncounterHeaderInfo>> userEncountersHeader;
    private LiveData<List<EncounterInfo>> dirty;


    public EncountersViewModel(Application app){
        super(app);
        repository = new EncountersRepository(app);
    }


// Log.d("XXXXXX", "change "+encounterInfos);
    public LiveData<List<EncounterInfo>> getEncounters() {
        document = repository.getPatientEncounters();
        return document;
    }
    public void archive(){
        repository.archive();
    }

    public LiveData<List<EncounterInfo>> getDirty() {
        dirty = repository.getDirty();
        return document;
    }

    public LiveData<List<EncounterHeaderInfo>> getUserEncounters() {
        if(userEncountersHeader== null) {
            userEncountersHeader = repository.getUserEncounterHeader();
        }
        return userEncountersHeader;
    }

    public void insert(EncounterInfo doc){
        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        Status status = doc.currentStatus();
        if(status.status == StatusConstant.NEW.status){
            doc.setCurrentStatus(StatusConstant.PENDING, status.author);
        }
        repository.insert(doc);
    }
    public void update(EncounterInfo doc){
        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.update(doc);
    }


    public EncounterInfo getEncounter(String euuid) {
        return repository.getEncounter(euuid);
    }


    public LiveData<List<ECounterItem>> getCount() {
        return repository.getCount();
    }
}


