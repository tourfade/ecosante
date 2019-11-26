package com.kamitsoft.ecosante.database;


import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.EncounterHeaderInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.MedicationInfo;
import com.kamitsoft.ecosante.model.UserInfo;

import java.sql.Timestamp;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface EncounterDAO {

    @Query("SELECT * FROM encounterinfo WHERE patientUuid =:patientUuid AND deleted<= 0 ORDER BY createdAt DESC")
    LiveData<List<EncounterInfo>> getPatientEncounters(String patientUuid);

    @Query("SELECT e.uuid, e.patientUuid, " +
            "e.userUuid,e.createdAt, " +
            "e.pressusreSystolic,e.pressusreDiastolic, " +
            "e.glycemy, e.glycemyState,  p.patientID, " +
            "e.user, e.patientUuid, e.userUuid, " +
            "p.firstName, p.lastName, p.middleName, " +
            "p.sex, p.pob, p.dob, p.mobile, p.avatar " +
           " FROM encounterinfo e LEFT JOIN patientinfo p ON e.patientUuid = p.uuid" +
           " WHERE e.userUuid =:userUuid AND e.deleted<= 0 ORDER BY e.createdAt DESC")
    LiveData<List<EncounterHeaderInfo>> getUserEncounters(String userUuid);

    @Query("SELECT * FROM encounterinfo ORDER BY createdAt DESC")
    LiveData<List<EncounterInfo>> getAllEncounters();

    @Query("SELECT * FROM encounterinfo WHERE uuid=:uuid")
    EncounterInfo getEncounter(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EncounterInfo... encounterInfo);

    @Delete
    void delete(EncounterInfo... encounterInfo);

    @Update
    int update(EncounterInfo... encounterInfo);

    @Query("SELECT * FROM labinfo WHERE encounterUuid=:encounterUuid AND deleted<= 0 ORDER BY createdAt DESC")
    LiveData<List<LabInfo>> getLabs(String encounterUuid);

    @Query("SELECT * FROM labinfo WHERE uuid=:uuid")
    LabInfo getLab(String uuid);

    @Update
    int updateLab(LabInfo... lab);

    @Insert
    void insertLab(LabInfo... lab);

    @Query("SELECT * FROM medicationinfo WHERE encounterUuid=:encounterUuid AND deleted<= 0 ORDER BY createdAt DESC")
    LiveData<List<MedicationInfo>> getMedications(String encounterUuid);

    @Update
    int updateMedication(MedicationInfo... l);

    @Insert
    void insertMedication(MedicationInfo... l);

    @Query("SELECT * FROM documentinfo WHERE encounterUuid=:encounterUuid  AND deleted<= 0 ORDER BY createdAt DESC")
    List<DocumentInfo> getDoc(String encounterUuid);

    @Delete
    void deleteLab(LabInfo... param);

    @Delete
    void deleteMedications(MedicationInfo... params);

    @Query("SELECT * FROM encounterinfo WHERE patientUuid =:uuid AND deleted<= 0 ORDER BY createdAt DESC")
    List<EncounterInfo> getAllPatientEncounters(String uuid);

    @Query("SELECT * FROM encounterinfo WHERE updatedAt > (SELECT lastSynced FROM entitysync WHERE entity ='encounterinfo')")
    LiveData<List<EncounterInfo>> getUnsync();
}
