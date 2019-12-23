package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.EncounterDAO;
import com.kamitsoft.ecosante.database.EntityDAO;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.model.LabInfo;

import java.util.List;

import androidx.lifecycle.LiveData;

public class EntityRepository {
    private EntityDAO dao;
    private LiveData<List<EntitySync>> entities;
    private EcoSanteApp app;

    public EntityRepository(Application application) {
        app = (EcoSanteApp)application;
        dao = app.getDb().entityDAO();
        entities = dao.getDirties();
    }

    public LiveData<List<EntitySync>> getDirtyEntities() {
        return entities;
    }


    public void update(EntitySync doc) {
        new insertAsyncTask(dao).execute(doc);
    }

    public void setDirty(String entity) {
        new dirtyAsyncTask(dao).execute(entity);
    }


    private static class insertAsyncTask extends AsyncTask<EntitySync, Void, Void> {

        private EntityDAO dao;

        insertAsyncTask(EntityDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final EntitySync... params) {
            dao.insert(params);
            return null;
        }
    }
    private static class dirtyAsyncTask extends AsyncTask<String, Void, Void> {

        private EntityDAO dao;

        dirtyAsyncTask(EntityDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            EntitySync e = dao.getEntitySync(params[0]);
            e.setDirty(true);
            dao.insert(e);
            return null;
        }
    }

}
