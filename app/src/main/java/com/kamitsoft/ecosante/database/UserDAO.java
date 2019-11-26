package com.kamitsoft.ecosante.database;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kamitsoft.ecosante.model.UserInfo;

import java.util.List;


@Dao
public interface UserDAO {

    @Query("SELECT * FROM userinfo WHERE connected > 0 LIMIT 1")
    LiveData<UserInfo> getConnectedUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserInfo... user);

    @Delete
    void delete(UserInfo... user);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(UserInfo... user);

    @Query("DELETE FROM userinfo")
    void deleteAll();

    @Query("SELECT * FROM userinfo WHERE userType =:userType")
    LiveData<List<UserInfo>> getUsers(int userType);

    @Query("SELECT * FROM userinfo WHERE lastName like:key OR firstName like:key OR middleName like:key OR speciality like:key")
    List<UserInfo> findUsers(String key);

    @Query("SELECT * FROM userinfo WHERE userType =:userType LIMIT 1")
    UserInfo getUser(int userType);

    @Query("UPDATE  userinfo SET connected = 0 WHERE connected > 0")
    void disconnect();
}
