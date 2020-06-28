package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;

import com.kamitsoft.ecosante.model.ClusterInfo;
import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.model.repositories.DocumentsRepository;
import com.kamitsoft.ecosante.model.repositories.EntityRepository;

import java.sql.Timestamp;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EntitiesViewModel extends AndroidViewModel {
    private EntityRepository repository;
    private LiveData<List<EntitySync>> entities;

    public EntitiesViewModel(Application app){
        super(app);
        repository = new EntityRepository(app);
        entities = repository.getDirtyEntities();
    }

    public LiveData<List<EntitySync>> getDirtyEntities() {
        if (entities == null) {
            entities = new MutableLiveData<>();
        }
        return entities;
    }

    public void insert(EntitySync doc){

        repository.update(doc);
    }
    public void update(EntitySync doc){

        repository.update(doc);
    }

    public void init(Class<?> entity) {
        EntitySync es = new EntitySync();
        es.setDirty(true);
        es.setLastSynced(0);
        es.setEntity(entity.getSimpleName().toLowerCase());
        repository.update(es);
    }


}


