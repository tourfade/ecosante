package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;
import android.util.Log;

import com.kamitsoft.ecosante.model.DistrictInfo;
import com.kamitsoft.ecosante.model.repositories.DistrictRepository;
import java.sql.Timestamp;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DistrictViewModel extends AndroidViewModel {
    private DistrictRepository repository;
    private LiveData<List<DistrictInfo>> data;
    public DistrictViewModel(Application app){
        super(app);
        repository = new DistrictRepository(app);
        data = repository.getData();
    }



    public LiveData<List<DistrictInfo>> getDistricts() {
        if (data == null) {
            data = new MutableLiveData<>();
        }
        return data;
    }
    public void insert(DistrictInfo info){
        info.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.insert(info);
    }
    public void update(DistrictInfo info){
        info.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.update(info);
    }
    public void delete(DistrictInfo info){
        info.setDeleted(true);
        update(info);
    }
}


