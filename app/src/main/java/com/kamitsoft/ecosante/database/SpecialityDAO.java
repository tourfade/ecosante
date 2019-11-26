package com.kamitsoft.ecosante.database;


import com.kamitsoft.ecosante.model.Speciality;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface SpecialityDAO {

    @Query("SELECT * FROM Speciality LIMIT 1")
    Speciality getSpeciality();

    @Insert
    void insert(Speciality specialityInfo);

    @Delete
    void delete(Speciality specialityInfo);

    @Update
    int update(Speciality specialityInfo);

    @Query("DELETE FROM Speciality")
    void deleteAll();

    @Query("SELECT * FROM Speciality WHERE name like:text")
    List<Speciality> finSpecialities(String text);
}
