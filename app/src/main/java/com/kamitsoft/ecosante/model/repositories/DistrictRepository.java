package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.DistrictDAO;
import com.kamitsoft.ecosante.database.EncounterDAO;
import com.kamitsoft.ecosante.model.DistrictInfo;

import java.util.List;

import androidx.lifecycle.LiveData;

public class DistrictRepository {
    private DistrictDAO dao;
    private LiveData<List<DistrictInfo>> data;
    private LiveData<List<DistrictInfo>> unsyched;
    private EcoSanteApp app;

    public DistrictRepository(Application application) {
        app = (EcoSanteApp)application;
        dao = app.getDb().districtDAO();
        data = dao.all();
        //unsyched = dao.getUnsynched();
    }

    public LiveData<List<DistrictInfo>> getData() {
        return data;
    }

    public void insert (DistrictInfo... infos) {
        new InsertAsyncTask(dao).execute(infos);
    }

    public void update(DistrictInfo infos) {
        new UpdateAsyncTask(dao).execute(infos);
    }

    public void delete(DistrictInfo... infos) {
        new DeleteAsyncTask(dao).execute(infos);
    }

    public void syncUpdate(DistrictInfo... districtInfos) {
        dao.insert(districtInfos);
    }
    public void syncDelete(DistrictInfo... districtInfos) {
        dao.delete(districtInfos);
    }
    private static class InsertAsyncTask extends AsyncTask<DistrictInfo, Void, Void> {

        private DistrictDAO dao;

        InsertAsyncTask(DistrictDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final DistrictInfo... params) {
            dao.insert(params);
            return null;
        }
    }
    private static class UpdateAsyncTask extends AsyncTask<DistrictInfo, Void, Void> {

        private DistrictDAO dao;

        UpdateAsyncTask(DistrictDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final DistrictInfo... params) {
            dao.update(params);
            return null;
        }
    }
    private static class DeleteAsyncTask extends AsyncTask<DistrictInfo, Void, Void> {

        private DistrictDAO dao;

        DeleteAsyncTask(DistrictDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final DistrictInfo... params) {
            dao.delete(params);
            return null;
        }
    }

}
