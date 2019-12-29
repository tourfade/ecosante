package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.EncounterDAO;
import com.kamitsoft.ecosante.model.EncounterHeaderInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.UserInfo;

import java.util.List;
import java.util.stream.Collectors;

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

    public void reset(UserAccountInfo accountInfo) {

        List<EncounterInfo> values = encounterDAO.getAllEncounters().getValue();
        if(values != null) {
            List<String> todel = values.parallelStream().filter(e ->
                    !accountInfo.getUserUuid().equals(e.getMonitor().monitorUuid)
                            && !accountInfo.getUserUuid().equals(e.getSupervisor().nurseUuid)
                            && !accountInfo.getUserUuid().equals(e.getSupervisor().physicianUuid))
                    .map(e -> e.getUuid())
                    .collect(Collectors.toList());

            String[] ids = todel.toArray(new String[]{});
            encounterDAO.resetEncounters(ids);
            encounterDAO.resetLabs(ids);
            encounterDAO.resetMedications(ids);
        }

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
