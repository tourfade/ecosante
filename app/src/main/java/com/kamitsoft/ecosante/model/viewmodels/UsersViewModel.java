package com.kamitsoft.ecosante.model.viewmodels;

import android.app.Application;

import com.kamitsoft.ecosante.constant.NavMenuConstant;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.CounterItem;
import com.kamitsoft.ecosante.model.PhysNursPat;
import com.kamitsoft.ecosante.model.SubConsumerInfo;
import com.kamitsoft.ecosante.model.SubInstanceInfo;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.repositories.UsersRepository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UsersViewModel extends AndroidViewModel {
    private UsersRepository repository;
    private LiveData<UserAccountInfo> connectedAccount;
    private LiveData<SubConsumerInfo> consumerInfo;
    private LiveData<SubInstanceInfo> subInstanceInfo;
    private Map<Integer, LiveData<List<CounterItem>>> counts;


    public UsersViewModel(Application app){
        super(app);
        repository = new UsersRepository(app);
        connectedAccount = repository.getLiveAccount();
        consumerInfo = repository.getConsumer();
        subInstanceInfo = repository.getSubIntanceInfo();

    }


    public LiveData<UserInfo> getLiveConnectedUser() {
        return repository.getConnectedUser();
    }

    public LiveData<List<UserInfo>> getUsers() {
        return repository.getAllUsers();
    }

    public void insert(UserInfo doc){
        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.insert(doc);
    }

    public void update(UserInfo doc){
        doc.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        repository.update(doc);
    }

    public LiveData<UserAccountInfo> getLiveConnectedAccount() {
        return connectedAccount;
    }
    public UserAccountInfo getConnectedAccount() {
        return repository.getAccount();
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


    public LiveData<List<CounterItem>> getCount(int id) {
        if(counts == null){
            counts = new HashMap<>();
        }
        if(counts.get(id)  == null){
            switch (NavMenuConstant.ofMenu(id)){
                case NAV_SUPERVISED_VISITS:
                case NAV_USER_VISITS:
                    counts.put(id, repository.countVisits());
                    break;
                case NAV_NURSE_DISTRIC:
                case NAV_SUPERVISED_PATIENTS:
                case NAV_USER_PATIENTS:
                    counts.put(id, repository.countPatients());
                    break;
                case NAV_ADMIN_DISTRICTS:
                    counts.put(id, repository.countAdminsDistricts());
                    break;
                case NAV_SUPERVISED_NURSES:
                case NAV_NURSE_SUPERVISORS:
                case NAV_ADMIN_NURSES:
                case NAV_ADMIN_PHYSICIANS:
                    counts.put(id, repository.countUsers());
                   break;
                case NAV_USER_APPOINTMENTS:
                    counts.put(id, repository.countAppts());
                    break;


            }

        }

        return counts.get(id);
    }
}


