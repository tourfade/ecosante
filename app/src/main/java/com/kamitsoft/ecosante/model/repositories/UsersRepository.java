package com.kamitsoft.ecosante.model.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.database.UserDAO;
import com.kamitsoft.ecosante.model.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;

public class UsersRepository {
    private UserDAO userDAO;
    private Map<UserType, LiveData<List<UserInfo>>> allUsers;
    private LiveData<UserInfo> connectedUser;
    private EcoSanteApp app;

    public UsersRepository(Application application) {
        app = (EcoSanteApp)application;
        userDAO = app.getDb().userDAO();
        allUsers = new HashMap<>();
        connectedUser = userDAO.getConnectedUser();
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

    public void insert (UserInfo doc) {
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

    public void connect(UserInfo e) {
        e.setConnected(true);
        (new connectAsyncTask(userDAO)).execute(e);
    }

    private static class connectAsyncTask extends AsyncTask<UserInfo, Void, Void> {

        private UserDAO mAsyncTaskDao;

        connectAsyncTask(UserDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final UserInfo... params) {
            mAsyncTaskDao.disconnect();
            mAsyncTaskDao.insert(params);
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

}
