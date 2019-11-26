package com.kamitsoft.ecosante.database;


import com.kamitsoft.ecosante.model.EntitySync;

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

}
