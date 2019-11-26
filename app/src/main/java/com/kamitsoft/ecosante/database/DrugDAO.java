package com.kamitsoft.ecosante.database;


import com.kamitsoft.ecosante.model.Drug;
import com.kamitsoft.ecosante.model.UserInfo;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface DrugDAO {

    @Query("SELECT * FROM drug LIMIT 1")
    Drug getDrug();

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(Drug... drug);

    @Delete
    void delete(Drug... drug);

    @Update(onConflict= OnConflictStrategy.REPLACE)
    int update(Drug... drug);

    @Query("DELETE FROM drug")
    void deleteAll();

    @Query("SELECT * FROM drug WHERE dci like:text OR reference like:text")
    List<Drug> finDrugs(String text);
}
