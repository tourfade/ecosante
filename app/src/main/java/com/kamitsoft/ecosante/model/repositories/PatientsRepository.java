package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.PatientDAO;
import com.kamitsoft.ecosante.database.SummaryDAO;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.SummaryInfo;
import com.kamitsoft.ecosante.model.UserAccountInfo;

import java.util.List;

import androidx.lifecycle.LiveData;

public class PatientsRepository {
    private PatientDAO dao;
    private SummaryDAO summaryDAO;
    private LiveData<List<PatientInfo>> allData;
    private LiveData<List<PatientInfo>> dirty;
    private LiveData<List<SummaryInfo>> dirtySummaries;
    private EcoSanteApp app;

    public PatientsRepository(Application application) {
        app = (EcoSanteApp)application;
        dao = app.getDb().patientDAO();
        summaryDAO = app.getDb().summaryDAO();
        allData = dao.getPatients();
        dirty = dao.dirtyPatients();
        dirtySummaries = summaryDAO.getDirty();

    }
    public LiveData<List<PatientInfo>> getDirty() {
        return dirty;
    }

    public LiveData<List<PatientInfo>> getAllData() {
        return allData;
    }

    public LiveData<List<SummaryInfo>> getDirtySummaries() {
        return dirtySummaries;
    }

    public LiveData<SummaryInfo> getSummary(String uuid) {
        return summaryDAO.getPatientSummaryLD(uuid);
    }


    public void insert (PatientInfo... bean) {
        new insertAsyncTask(dao).execute(bean);
    }

    public void update(PatientInfo bean) {
        new updateAsyncTask(dao).execute(bean);
    }
    public void delete(SummaryInfo... summaryInfos) {
        new SummaryDeleteAsyncTask(summaryDAO).execute(summaryInfos);
    }
    public void delete(PatientInfo... bean) {
        new deleteAsyncTask(dao).execute(bean);
    }
    public void updateSummaries(SummaryInfo... bean) {
        new SummaryUpdateAsyncTask(summaryDAO).execute(bean);
    }

    public void reset(UserAccountInfo accountInfo) {

        dao.resetePatientsSet();
        dao.reseteDocumentsSet();
        summaryDAO.resetSummariesSet();
    }

    public PatientInfo getPatient(String uuid) {
      return  dao.getPatient(uuid);
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

    private static class SummaryUpdateAsyncTask extends AsyncTask<SummaryInfo, Void, Void> {

        private SummaryDAO mAsyncTaskDao;

        SummaryUpdateAsyncTask(SummaryDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SummaryInfo... params) {
            mAsyncTaskDao.insert(params);
            return null;
        }
    }

    private static class SummaryDeleteAsyncTask extends AsyncTask<SummaryInfo, Void, Void> {

        private SummaryDAO mAsyncTaskDao;

        SummaryDeleteAsyncTask(SummaryDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SummaryInfo... params) {
            mAsyncTaskDao.delete(params);
            return null;
        }
    }

}
