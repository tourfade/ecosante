package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.EncounterDAO;
import com.kamitsoft.ecosante.database.EntityDAO;
import com.kamitsoft.ecosante.model.AppointmentInfo;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.UserAccountInfo;

import java.util.List;
import java.util.stream.Collectors;

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

    public void justSynced(EntitySync doc) {
        doc.setDirty(false);
        doc.setLastSynced(System.currentTimeMillis());
        new insertAsyncTask(dao).execute(doc);
    }
    public void update(EntitySync doc) {
        dao.insert(doc);
        //new insertAsyncTask(dao).execute(doc);
    }

    public void setDirty(String entity) {
        new dirtyAsyncTask(dao).execute(entity);
    }

    public void reset() {
        dao.resetAll();
    }

    public EntitySync getEntity(String entity) {
        return dao.getEntitySync(entity);
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
            EntitySync e = dao.getEntitySync(params[0].toLowerCase());
            if(e==null){
                e = new EntitySync();
                e.setEntity(params[0]);
            }
            e.setDirty(true);
            dao.insert(e);
            return null;
        }
    }

}
