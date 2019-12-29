package com.kamitsoft.ecosante.database;


import com.kamitsoft.ecosante.model.AppointmentInfo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface AppointmentDAO {

    @Query("SELECT * FROM appointmentinfo WHERE recipientUuid =:userUuid AND deleted <= 0 ORDER BY createdAt DESC")
    LiveData<List<AppointmentInfo>> getUserAppointments(String userUuid);

    @Query("SELECT * FROM appointmentinfo WHERE   date >= :todayMin AND date >= :todayMax AND deleted <= 0 ORDER BY date ASC")
    LiveData<List<AppointmentInfo>> getUserDailyAppointments(long todayMin, long todayMax);

    @Query("SELECT * FROM appointmentinfo WHERE uuid=:uuid")
    LiveData<AppointmentInfo> getAppointment(String uuid);

    @Query("SELECT * FROM appointmentinfo WHERE patientUuid=:uuid AND deleted <= 0")
    LiveData<List<AppointmentInfo>> getPatientAppointments(String uuid);

    @Query("SELECT * FROM appointmentinfo ")
    LiveData<List<AppointmentInfo>> getAppointments();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppointmentInfo... beans);

    @Delete
    void delete(AppointmentInfo... beans);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(AppointmentInfo... beans);


}
