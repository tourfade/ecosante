package com.kamitsoft.ecosante.database;


import com.kamitsoft.ecosante.model.EntitySync;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface EntityDAO {

    @Query("SELECT * FROM entitysync WHERE entity=:entity")
    EntitySync getEntitySync(String entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EntitySync... entity);

    @Delete
    void delete(EntitySync... entity);

    @Query("DELETE  FROM entitysync")
    void deleteAll();

    @Query("UPDATE   entitysync SET lastSynced = 0, isDirty = 0 WHERE entity =:entity")
    void reset(String entity);

    @Query("UPDATE   entitysync SET isDirty = 1 WHERE entity =:entity")
    void setDirty(String entity);

    @Query("SELECT * FROM entitysync WHERE isDirty = 1")
    LiveData<List<EntitySync>> getDirties();
}
