package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;

import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.repositories.DocumentsRepository;
import com.kamitsoft.ecosante.model.repositories.FileRepository;
import com.kamitsoft.ecosante.services.UnsyncFile;

import java.sql.Timestamp;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class FilesViewModel extends AndroidViewModel {
    private FileRepository repository;
    private LiveData<List<UnsyncFile>> files;

    public FilesViewModel(Application app){
        super(app);
        repository = new FileRepository(app);
        files = repository.getFiles();
    }

    public LiveData<List<UnsyncFile>> getUnsyncFiles() {
        if (files == null) {
            files = new MutableLiveData<>();
        }
        return files;
    }

    public void insert(UnsyncFile file){
        file.setDate(System.currentTimeMillis());
        repository.insert(file);
    }

    public void remove(String uuid) {
        repository.remove(uuid);
    }
}


