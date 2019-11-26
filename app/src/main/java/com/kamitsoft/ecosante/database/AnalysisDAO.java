package com.kamitsoft.ecosante.database;


import com.kamitsoft.ecosante.model.Analysis;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface AnalysisDAO {

    @Query("SELECT * FROM analysis LIMIT 1")
    Analysis getAnalysis();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Analysis... analysis);

    @Delete
    void delete(Analysis... analysis);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Analysis... analysis);

    @Query("DELETE FROM analysis")
    void deleteAll();

    @Query("SELECT * FROM analysis WHERE labName like:key1")
    List<Analysis> finAnalysis(String key1);
}
