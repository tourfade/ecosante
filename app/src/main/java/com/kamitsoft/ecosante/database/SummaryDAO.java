package com.kamitsoft.ecosante.database;


import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.SummaryInfo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface SummaryDAO {

    @Query("SELECT * FROM summaryinfo WHERE uuid=:uuid")
    SummaryInfo getSummary(String uuid);

    @Query("SELECT * FROM summaryinfo WHERE patientUuid=:patUuid")
    SummaryInfo getPatientSummary(String patUuid);

    @Insert
    void insert(SummaryInfo summary);

    @Delete
    void delete(SummaryInfo summary);

    @Update
    int update(SummaryInfo summary);


    @Query("SELECT * FROM summaryinfo WHERE patientUuid=:patientUUID")
    LiveData<SummaryInfo> getPatientSummaryLD(String patientUUID);
}
