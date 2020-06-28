package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.ClusterDAO;
import com.kamitsoft.ecosante.model.ClusterInfo;

import java.util.List;

import androidx.lifecycle.LiveData;

public class ClusterRepository {
    private ClusterDAO dao;
    private LiveData<List<ClusterInfo>> clusters;
    private LiveData<List<ClusterInfo>> dirty;

    private EcoSanteApp app;

    public ClusterRepository(Application application) {
        app = (EcoSanteApp)application;
        dao = app.getDb().clusterDAO();
        dirty = dao.getDirty();
    }

    public LiveData<List<ClusterInfo>> getClusters() {
        if(clusters == null){
            clusters = dao.getAll();
        }
        return clusters;
    }
    public LiveData<List<ClusterInfo>> getDirty() {
        return dirty;
    }

    public void insert (ClusterInfo... info) {
        new Insert(dao).execute(info);
    }

    public void update(ClusterInfo info) {
        new updateAsyncTask(dao).execute(info);
    }

    public void delete(ClusterInfo... info) {
        new deleteAsyncTask(dao).execute(info);
    }

    public LiveData<ClusterInfo> getCluster(String uuid) {
        return dao.getCluster(uuid);
    }


    private static class Insert extends AsyncTask<ClusterInfo, Void, Void> {

        private ClusterDAO dao;

        Insert(ClusterDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final ClusterInfo... params) {
            dao.insert(params);
            return null;
        }
    }
    private static class updateAsyncTask extends AsyncTask<ClusterInfo, Void, Void> {

        private ClusterDAO dao;

        updateAsyncTask(ClusterDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final ClusterInfo... params) {
            dao.update(params);
            return null;
        }
    }
    private static class deleteAsyncTask extends AsyncTask<ClusterInfo, Void, Void> {

         private ClusterDAO dao;

        deleteAsyncTask(ClusterDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final ClusterInfo... params) {
            dao.delete(params);
            return null;
        }
    }

}
