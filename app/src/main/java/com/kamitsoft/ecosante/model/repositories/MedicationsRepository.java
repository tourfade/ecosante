package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.EncounterDAO;
import com.kamitsoft.ecosante.model.MedicationInfo;

import java.util.List;

import androidx.lifecycle.LiveData;

public class MedicationsRepository {
    private EncounterDAO encounterDAO;
    private LiveData<List<MedicationInfo>> encounterMedication;
    private EcoSanteApp app;

    public MedicationsRepository(Application application) {
        app = (EcoSanteApp)application;
        encounterDAO = app.getDb().encounterDAO();
        encounterMedication = encounterDAO.getMedications();
    }

    public LiveData<List<MedicationInfo>> getPatientMedications() {
        return encounterMedication;
    }

    public void insert (MedicationInfo... doc) {
        new insertAsyncTask(encounterDAO).execute(doc);
    }

    public void update(MedicationInfo doc) {
        new updateAsyncTask(encounterDAO).execute(doc);
    }

    public void delete(MedicationInfo doc) {
        new deleteAsyncTask(encounterDAO).execute(doc);
    }

    private static class insertAsyncTask extends AsyncTask<MedicationInfo, Void, Void> {

        private EncounterDAO mAsyncTaskDao;

        insertAsyncTask(EncounterDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MedicationInfo... params) {
            mAsyncTaskDao.insertMedication(params);
            return null;
        }
    }
    private static class updateAsyncTask extends AsyncTask<MedicationInfo, Void, Void> {

        private EncounterDAO mAsyncTaskDao;

        updateAsyncTask(EncounterDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MedicationInfo... params) {
            mAsyncTaskDao.updateMedication(params);
            return null;
        }
    }
    private static class deleteAsyncTask extends AsyncTask<MedicationInfo, Void, Void> {

        private EncounterDAO mAsyncTaskDao;

        deleteAsyncTask(EncounterDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MedicationInfo... params) {
            mAsyncTaskDao.deleteMedications(params);
            return null;
        }
    }

}
