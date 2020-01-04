package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.EncounterDAO;
import com.kamitsoft.ecosante.database.PatientDAO;
import com.kamitsoft.ecosante.model.AppointmentInfo;
import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.UserAccountInfo;

import java.util.List;
import java.util.stream.Collectors;

import androidx.lifecycle.LiveData;

public class LabsRepository {
    private EncounterDAO encounterDAO;
    private LiveData<List<LabInfo>> encounterLabs;
    private EcoSanteApp app;

    public LabsRepository(Application application) {
        app = (EcoSanteApp)application;
        encounterDAO = app.getDb().encounterDAO();
        encounterLabs = encounterDAO.getLabs();
    }

    public LiveData<List<LabInfo>> getEncounterLabs() {
        return encounterLabs;
    }

    public void insert (LabInfo... doc) {
        new insertAsyncTask(encounterDAO).execute(doc);
    }

    public void update(LabInfo doc) {
        new updateAsyncTask(encounterDAO).execute(doc);
    }

    public void delete(LabInfo... doc) {
        new deleteAsyncTask(encounterDAO).execute(doc);
    }




    private static class insertAsyncTask extends AsyncTask<LabInfo, Void, Void> {

        private EncounterDAO mAsyncTaskDao;

        insertAsyncTask(EncounterDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final LabInfo... params) {
            mAsyncTaskDao.insertLab(params);
            return null;
        }
    }
    private static class updateAsyncTask extends AsyncTask<LabInfo, Void, Void> {

        private EncounterDAO mAsyncTaskDao;

        updateAsyncTask(EncounterDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final LabInfo... params) {
            mAsyncTaskDao.updateLab(params);
            return null;
        }
    }
    private static class deleteAsyncTask extends AsyncTask<LabInfo, Void, Void> {

        private EncounterDAO mAsyncTaskDao;

        deleteAsyncTask(EncounterDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final LabInfo... params) {
            mAsyncTaskDao.deleteLab(params);
            return null;
        }
    }

}
