package com.kamitsoft.ecosante.database;



import com.kamitsoft.ecosante.model.AlarmInfo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface AlarmDAO {



    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(AlarmInfo... info);

    @Delete
    void delete(AlarmInfo... info);

    @Update(onConflict= OnConflictStrategy.REPLACE)
    int update(AlarmInfo... info);

    @Query("DELETE FROM AlarmInfo")
    void deleteAll();

    @Query("SELECT * FROM AlarmInfo WHERE name like:key1")
    List<AlarmInfo> findParameter(String key1);

    @Query("SELECT * FROM AlarmInfo")
    LiveData<List<AlarmInfo>> getAll();

    @Query("SELECT * FROM AlarmInfo WHERE uuid =:uuid ")
    LiveData<List<AlarmInfo>> getAlarm(String uuid);

    @Query("SELECT * FROM AlarmInfo WHERE userUuid =:userUuid ")
    LiveData<List<AlarmInfo>> getUserAlarms(String userUuid);
}
