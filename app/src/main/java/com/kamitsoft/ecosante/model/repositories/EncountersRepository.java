package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.EncounterDAO;
import com.kamitsoft.ecosante.model.EncounterHeaderInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;

import java.util.List;

import androidx.lifecycle.LiveData;

public class EncountersRepository {
    private EncounterDAO encounterDAO;
    private LiveData<List<EncounterInfo>> patientEncounters;
    private LiveData<List<EncounterHeaderInfo>> userEncounters;
    private LiveData<List<EncounterInfo>> dirty;
    private EcoSanteApp app;

    public EncountersRepository(Application application) {
        app = (EcoSanteApp)application;
        encounterDAO = app.getDb().encounterDAO();
        dirty = encounterDAO.getUnsync();

    }
    public LiveData<List<EncounterInfo>> getDirty(){
        return  dirty;
    }

    public LiveData<List<EncounterHeaderInfo>> getUserEncounterHeader() {
        if(userEncounters ==null){
            userEncounters = encounterDAO.getEncounterHeaders();
        }
        return userEncounters;
    }

    public LiveData<List<EncounterInfo>> getPatientEncounters() {
        if(patientEncounters == null){
            patientEncounters = encounterDAO.getPatientEncounters(app.getCurrentPatient().getUuid());
        }
        return patientEncounters;
    }

    public void insert (EncounterInfo... doc) {
        new insertAsyncTask(encounterDAO).execute(doc);
    }

    public void update(EncounterInfo doc) {
        new updateAsyncTask(encounterDAO).execute(doc);
    }

    public void delete(EncounterInfo doc) {
        new deleteAsyncTask(encounterDAO).execute(doc);
    }

    private static class insertAsyncTask extends AsyncTask<EncounterInfo, Void, Void> {

        private EncounterDAO mAsyncTaskDao;

        insertAsyncTask(EncounterDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final EncounterInfo... params) {
            mAsyncTaskDao.insert(params);
            return null;
        }
    }
    private static class updateAsyncTask extends AsyncTask<EncounterInfo, Void, Void> {

        private EncounterDAO mAsyncTaskDao;

        updateAsyncTask(EncounterDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final EncounterInfo... params) {
            mAsyncTaskDao.update(params);
            return null;
        }
    }
    private static class deleteAsyncTask extends AsyncTask<EncounterInfo, Void, Void> {

        private EncounterDAO mAsyncTaskDao;

        deleteAsyncTask(EncounterDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final EncounterInfo... params) {
            mAsyncTaskDao.delete(params);
            return null;
        }
    }

}
