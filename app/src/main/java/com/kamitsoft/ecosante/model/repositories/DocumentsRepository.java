package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.PatientDAO;
import com.kamitsoft.ecosante.model.DocumentInfo;
import java.util.List;
import androidx.lifecycle.LiveData;

public class DocumentsRepository {
    private PatientDAO patientDAO;
    private LiveData<List<DocumentInfo>> allPatientDocuments;
    private EcoSanteApp app;

    public DocumentsRepository(Application application) {
        app = (EcoSanteApp)application;
        patientDAO = app.getDb().patientDAO();
        allPatientDocuments = patientDAO.getDocuments(app.getCurrentPatient().getUuid());
    }

    public LiveData<List<DocumentInfo>> getPatientDocs() {
        return allPatientDocuments;
    }

    public void insert (DocumentInfo doc) {
        new Insert(patientDAO).execute(doc);
    }

    public void update(DocumentInfo doc) {
        new updateAsyncTask(patientDAO).execute(doc);
    }

    public void delete(DocumentInfo doc) {
        new deleteAsyncTask(patientDAO).execute(doc);
    }

    private static class Insert extends AsyncTask<DocumentInfo, Void, Void> {

        private PatientDAO mAsyncTaskDao;

        Insert(PatientDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DocumentInfo... params) {
            mAsyncTaskDao.insertDocumentInfo(params);
            return null;
        }
    }
    private static class updateAsyncTask extends AsyncTask<DocumentInfo, Void, Void> {

        private PatientDAO mAsyncTaskDao;

        updateAsyncTask(PatientDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DocumentInfo... params) {
            mAsyncTaskDao.updateDoc(params);
            return null;
        }
    }
    private static class deleteAsyncTask extends AsyncTask<DocumentInfo, Void, Void> {

        private PatientDAO mAsyncTaskDao;

        deleteAsyncTask(PatientDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DocumentInfo... params) {
            mAsyncTaskDao.deleteDoc(params);
            return null;
        }
    }

}
