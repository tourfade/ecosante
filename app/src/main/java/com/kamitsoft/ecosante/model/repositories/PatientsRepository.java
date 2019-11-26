package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.PatientDAO;
import com.kamitsoft.ecosante.database.UserDAO;
import com.kamitsoft.ecosante.model.PatientInfo;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;

public class PatientsRepository {
    private PatientDAO dao;
    private LiveData<List<PatientInfo>> allData;
    private LiveData<List<PatientInfo>> dirty;
    private EcoSanteApp app;

    public PatientsRepository(Application application) {
        app = (EcoSanteApp)application;
        dao = app.getDb().patientDAO();
        allData = dao.getPatients();
        dirty = dao.getUnsync();
    }
    public LiveData<List<PatientInfo>> getDirty() {
        return dirty;
    }

    public LiveData<List<PatientInfo>> getAllData() {
        return allData;
    }

    public void insert (PatientInfo bean) {
        new insertAsyncTask(dao).execute(bean);
    }

    public void update(PatientInfo bean) {
        new updateAsyncTask(dao).execute(bean);
    }

    public void delete(PatientInfo bean) {
        new deleteAsyncTask(dao).execute(bean);
    }




    private static class insertAsyncTask extends AsyncTask<PatientInfo, Void, Void> {

        private PatientDAO mAsyncTaskDao;

        insertAsyncTask(PatientDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PatientInfo... params) {
            mAsyncTaskDao.insert(params);
            return null;
        }
    }
    private static class updateAsyncTask extends AsyncTask<PatientInfo, Void, Void> {

        private PatientDAO mAsyncTaskDao;

        updateAsyncTask(PatientDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PatientInfo... params) {
            mAsyncTaskDao.update(params);
            return null;
        }
    }
    private static class deleteAsyncTask extends AsyncTask<PatientInfo, Void, Void> {

        private PatientDAO mAsyncTaskDao;

        deleteAsyncTask(PatientDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PatientInfo... params) {
            mAsyncTaskDao.delete(params);
            return null;
        }
    }

}
