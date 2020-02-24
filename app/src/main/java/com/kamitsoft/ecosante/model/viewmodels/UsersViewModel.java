package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;

import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.PhysNursPat;
import com.kamitsoft.ecosante.model.SubConsumerInfo;
import com.kamitsoft.ecosante.model.SubInstanceInfo;
import com.kamitsoft.ecosante.model.UserAccountInfo;
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
    private LiveData<UserAccountInfo> connectedAccount;
    private Map<UserType, LiveData<List<UserInfo>>> allUsers;
    private LiveData<SubConsumerInfo> consumerInfo;
    private LiveData<SubInstanceInfo> subInstanceInfo;


    public UsersViewModel(Application app){
        super(app);
        repository = new UsersRepository(app);
        connectedUser = repository.getConnectedUser();
        allUsers = repository.getUsers();
        connectedAccount = repository.getAccount();
        consumerInfo = repository.getConsumer();
        subInstanceInfo = repository.getSubIntanceInfo();
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

    public LiveData<UserAccountInfo> getConnectedAccount() {
        return connectedAccount;
    }


    public UserInfo getUser(UserType type) {
        return repository.getUser(type);
    }

    public void disconnectUser() {
        repository.disconnectUser();
    }

    public void connect(UserAccountInfo accountInfo, UserInfo userInfo) {
        repository.insert(userInfo);
        repository.connect(accountInfo);
    }

    public UserInfo getSupervisor(String nurseUuid) {
        return repository.getSupervisor(nurseUuid);
    }

    public void setSupervisor(PhysNursPat pn) {
        repository.insert(pn);
    }



    public PhysNursPat getNursePnp(String uuid) {
        return repository.getNursePnp(uuid);
    }

    public void status(String uuid, int status) {
        repository.remoteUpdateStatus(uuid,status);
    }

    public LiveData<SubInstanceInfo> getSubInstanceInfo() {
        return subInstanceInfo;
    }


    public LiveData<SubConsumerInfo> getConsumerInfo() {
        return consumerInfo;
    }


}


