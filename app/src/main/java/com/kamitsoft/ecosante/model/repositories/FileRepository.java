package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.EntityDAO;
import com.kamitsoft.ecosante.database.UnsyncFileDAO;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.services.UnsyncFile;

import java.util.List;

import androidx.lifecycle.LiveData;

public class FileRepository {
    private UnsyncFileDAO dao;
    private LiveData<List<UnsyncFile>> files;
    private EcoSanteApp app;

    public FileRepository(Application application) {
        app = (EcoSanteApp)application;
        dao = app.getDb().fileDAO();
        files  = dao.allFiles();
    }

    public LiveData<List<UnsyncFile>> getFiles() {
        return files;
    }


    public void insert(UnsyncFile doc) {
        new insertAsyncTask(dao).execute(doc);
    }

    public void remove(String... uuid) {
        new removeAsyncTask(dao).execute(uuid);
    }
    private static class removeAsyncTask extends AsyncTask<String, Void, Void> {

        private UnsyncFileDAO dao;

        removeAsyncTask(UnsyncFileDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            dao.delete(params);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<UnsyncFile, Void, Void> {

        private UnsyncFileDAO dao;

        insertAsyncTask(UnsyncFileDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final UnsyncFile... params) {
            dao.insert(params);
            return null;
        }
    }

}
