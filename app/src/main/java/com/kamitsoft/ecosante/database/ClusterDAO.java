package com.kamitsoft.ecosante.database;



import com.kamitsoft.ecosante.model.ClusterInfo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface ClusterDAO {

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(ClusterInfo... info);

    @Delete
    void delete(ClusterInfo... info);

    @Update(onConflict= OnConflictStrategy.REPLACE)
    int update(ClusterInfo... info);

    @Query("DELETE FROM ClusterInfo")
    void deleteAll();


    @Query("SELECT * FROM ClusterInfo")
    LiveData<List<ClusterInfo>> getAll();

    @Query("SELECT * FROM ClusterInfo WHERE uuid =:uuid ")
    LiveData<ClusterInfo> getCluster(String uuid);

    @Query("SELECT * FROM ClusterInfo WHERE needUpdate >= 1 ")
    LiveData<List<ClusterInfo>> getDirty();
}
