package com.kamitsoft.ecosante.model;

import java.sql.Timestamp;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PhysicianNurses {
    @PrimaryKey
    @NonNull
    private String uuid;
    private String physicianUuid;
    private String nurseUuid;
    private Timestamp createdAt;
    private  Timestamp updatedAt;

    PhysicianNurses(){
        uuid = UUID.randomUUID().toString();
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
}
