package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;

import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.repositories.DocumentsRepository;
import com.kamitsoft.ecosante.model.repositories.UsersRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UsersViewModel extends AndroidViewModel {
    private UsersRepository repository;
    private LiveData<UserInfo> connectedUser;
    private Map<UserType, LiveData<List<UserInfo>>> allUsers;


    public UsersViewModel(Application app){
        super(app);
        repository = new UsersRepository(app);
        connectedUser = repository.getConnectedUser();
        allUsers = repository.getUsers();
    }


    public LiveData<List<UserInfo>> getUsersOfType(UserType type) {
        repository.getUserOfType(type);
        return allUsers.get(type);
    }

    public void insert(UserInfo doc){
        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.insert(doc);
    }

    public void update(UserInfo doc){
        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.update(doc);
    }

    public LiveData<UserInfo> getConnectedUser() {
        return connectedUser;
    }


    public UserInfo getUser(UserType type) {
        return repository.getUser(type);
    }

    public void disconnectUser() {
        UserInfo user = connectedUser.getValue();
        user.setConnected(false);
        update(user);
    }
}


