package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.AlarmDAO;
import com.kamitsoft.ecosante.database.PatientDAO;
import com.kamitsoft.ecosante.model.AlarmInfo;

import java.util.List;

import androidx.lifecycle.LiveData;

public class AlarmRepository {
    private AlarmDAO dao;
    private LiveData<List<AlarmInfo>> alarms;
    private EcoSanteApp app;

    public AlarmRepository(Application application) {
        app = (EcoSanteApp)application;
        dao = app.getDb().alarmDAO();
    }

    public LiveData<List<AlarmInfo>> getAlarms() {
        if(alarms == null){
            alarms = dao.getAll();
        }
        return alarms;
    }
    public LiveData<List<AlarmInfo>> getDirty() {
        return alarms;
    }

    public void insert (AlarmInfo... doc) {
        new Insert(dao).execute(doc);
    }

    public void update(AlarmInfo doc) {
        new updateAsyncTask(dao).execute(doc);
    }

    public void delete(AlarmInfo... doc) {
        new deleteAsyncTask(dao).execute(doc);
    }




    private static class Insert extends AsyncTask<AlarmInfo, Void, Void> {

        private AlarmDAO dao;

        Insert(AlarmDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final AlarmInfo... params) {
            dao.insert(params);
            return null;
        }
    }
    private static class updateAsyncTask extends AsyncTask<AlarmInfo, Void, Void> {

        private AlarmDAO dao;

        updateAsyncTask(AlarmDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final AlarmInfo... params) {
            dao.update(params);
            return null;
        }
    }
    private static class deleteAsyncTask extends AsyncTask<AlarmInfo, Void, Void> {

         private AlarmDAO dao;

        deleteAsyncTask(AlarmDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final AlarmInfo... params) {
            dao.delete(params);
            return null;
        }
    }

}
