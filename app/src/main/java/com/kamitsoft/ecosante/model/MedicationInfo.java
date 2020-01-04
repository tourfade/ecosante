package com.kamitsoft.ecosante.model;

import java.sql.Timestamp;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MedicationInfo {
    @PrimaryKey
    @NonNull
    private String uuid;
    private String encounterUuid;
    private int patientID;
    private String patientUuid;
    private Timestamp createdAt;
    private  Timestamp updatedAt;
    private long drugNumber;
    private String drugName;
    private Timestamp startingDate;
    private String direction;
    private int renewal;
    private int status;
    private Timestamp endingDate;
    private boolean deleted;

    public long getDrugNumber() {
        return drugNumber;
    }

    public void setDrugNumber(long drugNumber) {
        this.drugNumber = drugNumber;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }


    public Timestamp getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Timestamp startingDate) {
        this.startingDate = startingDate;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getRenewal() {
        return renewal;
    }

    public void setRenewal(int renewal) {
        this.renewal = renewal;
    }

    public MedicationInfo(){
        uuid = UUID.randomUUID().toString();
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public String getEncounterUuid() {
        return encounterUuid;
    }

    public void setEncounterUuid(String encounterUuid) {
        this.encounterUuid = encounterUuid;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof MedicationInfo ? ((MedicationInfo) obj).uuid.equals(uuid):false;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public String getPatientUuid() {
        return patientUuid;
    }


    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setEndingDate(Timestamp endingDate){
        this.endingDate = endingDate;
    }

    public Timestamp getEndingDate() {
        return endingDate;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean getDeleted() {
        return deleted;
    }


}

