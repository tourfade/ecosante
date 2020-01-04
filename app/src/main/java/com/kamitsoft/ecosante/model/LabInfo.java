package com.kamitsoft.ecosante.model;

import java.sql.Timestamp;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class LabInfo {
    @PrimaryKey
    @NonNull
    private String uuid;
    private String encounterUuid;
    private int patientID;
    private Timestamp createdAt;
    private int type;
    private double labValue;
    private String labName;
    private String patientUuid;
    private boolean done;
    private String notes;
    private  Timestamp updatedAt;
    private int labNumber;
    private Timestamp doneDate;
    private boolean deleted;


    public LabInfo(){
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getLabValue() {
        return labValue;
    }

    public void setLabValue(double labValue) {
        this.labValue = labValue;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof  LabInfo ? ((LabInfo) obj).uuid.equals(uuid):false;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setLabNumber(int labNumber) {
        this.labNumber = labNumber;
    }

    public int getLabNumber() {
        return labNumber;
    }

    public void setDoneDate(Timestamp doneDate) {
        this.doneDate = doneDate;
    }

    public Timestamp getDoneDate() {
        return doneDate;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean getDeleted() {
        return deleted;
    }


}

