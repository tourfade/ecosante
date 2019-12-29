package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.AppointmentDAO;
import com.kamitsoft.ecosante.model.AppointmentInfo;
import com.kamitsoft.ecosante.model.UserAccountInfo;

import java.util.List;
import java.util.stream.Collectors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AppointmentsRepository {
    private AppointmentDAO dao;
    private LiveData<List<AppointmentInfo>> userData;
    private LiveData<List<AppointmentInfo>> data;
    private LiveData<List<AppointmentInfo>> patientData;
    private EcoSanteApp app;

    public AppointmentsRepository(Application application) {
        app = (EcoSanteApp)application;
        dao = app.getDb().appointmentDAO();
        userData = dao.getAppointments();
        data = dao.getAppointments();
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
    public LiveData<List<AppointmentInfo>> getData() {
        return data;
    }

    public LiveData<List<AppointmentInfo>> getUserData() {
        if(userData == null){
            userData  = new MutableLiveData<>();
        }
        return userData;
    }

    public void insert (AppointmentInfo... bean) {
        new Insert(dao).execute(bean);
    }

    public void update(AppointmentInfo bean) {
        new Update(dao).execute(bean);
    }

    public void delete(AppointmentInfo bean) {
        new Delete(dao).execute(bean);
    }

    public void reset(UserAccountInfo accountInfo) {
        List<AppointmentInfo> toDelete = dao.getAppointments().getValue();
        if(toDelete != null) {
            toDelete = toDelete.parallelStream().filter(a ->
                    !(accountInfo.getUserUuid().equals(a.getRecipientUuid())
                            || accountInfo.getUserUuid().equals(a.getUserRequestorUuid())))
                    .collect(Collectors.toList());

            if (toDelete != null && toDelete.size() > 0)
                dao.delete(toDelete.toArray(new AppointmentInfo[]{}));
        }
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
