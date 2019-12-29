package com.kamitsoft.ecosante.database;


import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.UserInfo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface PatientDAO {

    @Query("SELECT * FROM patientinfo ORDER BY patientID DESC")
    LiveData<List<PatientInfo>> getPatients();

    @Query("SELECT * FROM patientinfo WHERE uuid=:uuid")
    PatientInfo getPatient(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PatientInfo... patientInfos);

    @Delete
    void delete(PatientInfo... patientInfo);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(PatientInfo... patientInfo);

    @Query("SELECT * FROM documentinfo WHERE patientUuid=:patientUuid  AND deleted <= 0 ORDER BY createdAt DESC")
    LiveData<List<DocumentInfo>> getDocuments(String patientUuid);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateDoc(DocumentInfo... l);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDocumentInfo(DocumentInfo... l);

    @Query("SELECT * FROM documentinfo WHERE  deleted <= 0 ORDER BY createdAt DESC")
    LiveData<List<DocumentInfo>> getAllDocuments();

    @Delete()
    void deleteDoc(DocumentInfo... param);

    @Query("SELECT * FROM patientinfo WHERE lastName like:key OR firstName like:key OR middleName like:key OR mobile like:key OR (firstName || ' '|| lastName) like:key")
    List<PatientInfo> findPatients(String key);

    @Query("SELECT * FROM patientinfo WHERE updatedAt > (SELECT lastSynced FROM entitysync WHERE entity ='patientinfo')")
    LiveData<List<PatientInfo>>  getUnsync();

    @Query("DELETE FROM patientinfo ")
    void resetePatientsSet();

    @Query("DELETE FROM documentinfo ")
    void reseteDocumentsSet();


}
