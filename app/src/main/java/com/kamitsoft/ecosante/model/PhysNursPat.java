package com.kamitsoft.ecosante.model;

import java.sql.Timestamp;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class PhysNursPat {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int accountId;
    private String physicianUuid;
    private String nurseUuid;
    private String patientUuid;
    private boolean active;
    private Timestamp createdAt;
    private  Timestamp updatedAt;

    public PhysNursPat(){
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }


    public String getPhysicianUuid() {
        return physicianUuid;
    }

    public void setPhysicianUuid(String physicianUuid) {
        this.physicianUuid = physicianUuid;
    }

    public String getNurseUuid() {
        return nurseUuid;
    }

    public void setNurseUuid(String nurseUuid) {
        this.nurseUuid = nurseUuid;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp date) {
        this.createdAt = date;
    }


    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
