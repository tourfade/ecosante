package com.kamitsoft.ecosante.database;



import com.kamitsoft.ecosante.model.Act;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface ActDAO {



    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(Act... Act);

    @Delete
    void delete(Act... Act);

    @Update(onConflict= OnConflictStrategy.REPLACE)
    int update(Act... Act);

    @Query("DELETE FROM act")
    void deleteAll();

    @Query("SELECT * FROM act WHERE name like:key1")
    List<Act> finAct(String key1);
}
