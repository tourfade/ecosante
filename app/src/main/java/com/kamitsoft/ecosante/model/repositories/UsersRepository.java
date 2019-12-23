package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.database.UserDAO;
import com.kamitsoft.ecosante.model.PhysNursPat;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UsersRepository {
    private UserDAO userDAO;
    private Map<UserType, LiveData<List<UserInfo>>> allUsers;
    private LiveData<UserInfo> connectedUser;
    private LiveData<List<PhysNursPat>> links;
    private EcoSanteApp app;
    private LiveData<UserAccountInfo> connectAccount;

    public UsersRepository(Application application) {
        app = (EcoSanteApp)application;
        userDAO = app.getDb().userDAO();
        allUsers = new HashMap<>();
        connectedUser = userDAO.getConnectedUser();
        links = userDAO.getAllPnpLinks();
        connectAccount = userDAO.getConnectedAccount();
    }

    public  LiveData<UserInfo> getConnectedUser(){
        return  connectedUser;
    }
    public LiveData<List<UserInfo>> getUserOfType(UserType type) {
        if(allUsers.get(type) == null ) {
            allUsers.put(type, userDAO.getUsers(type.type));
        }
        return allUsers.get(type);
    }
    public LiveData<UserAccountInfo> getAccount() {
        if(connectAccount ==null){
            connectAccount = new MutableLiveData<>();
        }
        return connectAccount;
    }
    public void insert (UserInfo... doc) {
        new insertAsyncTask(userDAO).execute(doc);
    }

    public void update(UserInfo doc) {
        new updateAsyncTask(userDAO).execute(doc);
    }

    public void delete(UserInfo doc) {
        new deleteAsyncTask(userDAO).execute(doc);
    }

    public Map<UserType, LiveData<List<UserInfo>>> getUsers() {
        return allUsers;
    }

    public UserInfo getUser(UserType type) {
        return userDAO.getUser(type.type);
    }

    public void connect(UserAccountInfo accountInfo) {

        (new connectAsyncTask(userDAO)).execute(accountInfo);
    }

    public void disconnectUser() {
        (new DisconnectAsyncTask(userDAO)).execute();
    }

    public void insert (PhysNursPat... doc) {
        new insertPhysNursAsyncTask(userDAO).execute(doc);
    }

    public UserInfo getSupervisor(String nurseUuid) {
        return  userDAO.getNurseSupervisor(nurseUuid);
    }

    public PhysNursPat getNursePnp(String uuid) {
        return userDAO.getNursePnp( uuid) ;
    }

    public LiveData<List<PhysNursPat>> getPnpLinks() {
        return links;
    }



    private static class DisconnectAsyncTask extends AsyncTask<Void, Void, Void> {

        private UserDAO mAsyncTaskDao;

        DisconnectAsyncTask(UserDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mAsyncTaskDao.disconnect();
            return null;
        }
    }

    private static class connectAsyncTask extends AsyncTask<UserAccountInfo, Void, Void> {

        private UserDAO mAsyncTaskDao;

        connectAsyncTask(UserDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final UserAccountInfo... params) {
            mAsyncTaskDao.disconnect();
            mAsyncTaskDao.connect(params);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<UserInfo, Void, Void> {

        private UserDAO mAsyncTaskDao;

        insertAsyncTask(UserDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final UserInfo... params) {
            mAsyncTaskDao.insert(params);
            return null;
        }
    }
    private static class updateAsyncTask extends AsyncTask<UserInfo, Void, Void> {

        private UserDAO mAsyncTaskDao;

        updateAsyncTask(UserDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final UserInfo... params) {
            mAsyncTaskDao.update(params);
            return null;
        }
    }
    private static class deleteAsyncTask extends AsyncTask<UserInfo, Void, Void> {

        private UserDAO mAsyncTaskDao;

        deleteAsyncTask(UserDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final UserInfo... params) {
            mAsyncTaskDao.delete(params);
            return null;
        }
    }
    private static class insertPhysNursAsyncTask extends AsyncTask<PhysNursPat, Void, Void> {

        private UserDAO mAsyncTaskDao;

        insertPhysNursAsyncTask(UserDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PhysNursPat... params) {
            mAsyncTaskDao.insert(params);
            return null;
        }
    }

}
