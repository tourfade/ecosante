package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;

import com.kamitsoft.ecosante.model.ClusterInfo;
import com.kamitsoft.ecosante.model.DistrictInfo;
import com.kamitsoft.ecosante.model.repositories.ClusterRepository;
import com.kamitsoft.ecosante.model.repositories.DistrictRepository;

import java.sql.Timestamp;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ClusterViewModel extends AndroidViewModel {
    private ClusterRepository repository;
    private LiveData<List<ClusterInfo>> data;
    public ClusterViewModel(Application app){
        super(app);
        repository = new ClusterRepository(app);
        data = repository.getClusters();
    }



    public LiveData<List<ClusterInfo>> getClusters() {
        if (data == null) {
            data = new MutableLiveData<>();
        }
        return data;
    }
    public LiveData<ClusterInfo> getCluster(String uuid) {

        return repository.getCluster(uuid);
    }
    public void insert(ClusterInfo info){
        info.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.insert(info);
    }
    public void update(ClusterInfo info){
        info.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.update(info);
    }

}


