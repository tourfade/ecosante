package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;
import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.repositories.DocumentsRepository;

import java.sql.Timestamp;
import java.util.List;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DocumentsViewModel extends AndroidViewModel {
    private DocumentsRepository repository;
    private LiveData<List<DocumentInfo>> document;

    public DocumentsViewModel(Application app){
        super(app);
        repository = new DocumentsRepository(app);
        document = repository.getPatientDocs();
    }

    public LiveData<List<DocumentInfo>> getDocuments() {
        if (document == null) {
            document = new MutableLiveData<>();
        }
        return document;
    }

    public void insert(DocumentInfo doc){
        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.insert(doc);
    }
    public void update(DocumentInfo doc){
        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        repository.update(doc);
    }
}


