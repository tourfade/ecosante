package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.AppointmentDAO;
import com.kamitsoft.ecosante.model.AppointmentInfo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AppointmentsRepository {
    private AppointmentDAO dao;
    private LiveData<List<AppointmentInfo>> userData;
    private LiveData<List<AppointmentInfo>> dirty;
    private LiveData<List<AppointmentInfo>> patientData;
    private EcoSanteApp app;

    public AppointmentsRepository(Application application) {
        app = (EcoSanteApp)application;
        dao = app.getDb().appointmentDAO();
        userData = dao.getUserAppointments(app.getCurrentUser().getUuid());
        dirty = dao.getUnsync();
    }
    public LiveData<List<AppointmentInfo>> getPatientAppointments(String uuid) {
        if(uuid == null) {
            patientData = new MutableLiveData<>();
        }
        if(patientData == null){
            patientData = dao.getPatientAppointments(uuid);
        }
        return patientData;
    }
    public LiveData<List<AppointmentInfo>> getDirty() {
        return dirty;
    }

    public LiveData<List<AppointmentInfo>> getUserData() {
        if(userData == null){
            userData  = new MutableLiveData<>();
        }
        return userData;
    }

    public void insert (AppointmentInfo bean) {
        new Insert(dao).execute(bean);
    }

    public void update(AppointmentInfo bean) {
        new Update(dao).execute(bean);
    }

    public void delete(AppointmentInfo bean) {
        new Delete(dao).execute(bean);
    }




    private static class Insert extends AsyncTask<AppointmentInfo, Void, Void> {

        private AppointmentDAO mAsyncTaskDao;

        Insert(AppointmentDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AppointmentInfo... params) {
            mAsyncTaskDao.insert(params);
            return null;
        }
    }
    private static class Update extends AsyncTask<AppointmentInfo, Void, Void> {

        private AppointmentDAO mAsyncTaskDao;

        Update(AppointmentDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AppointmentInfo... params) {
            mAsyncTaskDao.update(params);
            return null;
        }
    }
    private static class Delete extends AsyncTask<AppointmentInfo, Void, Void> {

        private AppointmentDAO mAsyncTaskDao;

        Delete(AppointmentDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AppointmentInfo... params) {
            mAsyncTaskDao.delete(params);
            return null;
        }
    }

}
