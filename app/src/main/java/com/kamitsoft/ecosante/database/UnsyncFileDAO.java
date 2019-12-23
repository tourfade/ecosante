package com.kamitsoft.ecosante.database;

import com.kamitsoft.ecosante.services.UnsyncFile;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface UnsyncFileDAO {

    @Query("SELECT * FROM unsyncfile WHERE type = 1")
    LiveData<List<UnsyncFile>> allDocumentFiles();

    @Query("SELECT * FROM unsyncfile")
    LiveData<List<UnsyncFile>> allFiles();

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(UnsyncFile... files);

    @Delete
    void delete(UnsyncFile... files);

    @Update(onConflict= OnConflictStrategy.REPLACE)
    int update(UnsyncFile... files);

    @Query("DELETE FROM unsyncfile")
    void deleteAll();

    @Query("SELECT * FROM unsyncfile WHERE fkey=:fk")
    List<UnsyncFile> get(String fk);

    @Query("SELECT * FROM unsyncfile WHERE type = 0")
    LiveData<List<UnsyncFile>> allAvatars();

    @Query("DELETE FROM unsyncfile WHERE fkey IN(:params)")
    void delete(String... params);
}
