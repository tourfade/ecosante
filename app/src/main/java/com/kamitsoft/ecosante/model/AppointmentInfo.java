package com.kamitsoft.ecosante.model;


import java.sql.Timestamp;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("place"), @Index("details")})
public class AppointmentInfo {
    @PrimaryKey
    @NonNull
    private String uuid;
    private String patientUuid;
    private String recipientUuid;
    private String userRequestorUuid;
    private int userType;
    private Timestamp updatedAt;
    private Timestamp createdAt;
    private String userObject;
    private String patientObject;
    private String details;
    private Timestamp date;
    private Timestamp requestLatestDate;
    private String place;
    private float lat,lon;
    private String speciality;
    private boolean deleted;
    private int status;
    private String recipient;
    private String patient;

    public String getUserObject() {
        return userObject;
    }

    public void setUserObject(String userObject) {
        this.userObject = userObject;
    }

    public String getPatientObject() {
        return patientObject;
    }

    public void setPatientObject(String patientObject) {
        this.patientObject = patientObject;
    }

    public AppointmentInfo(){
        uuid = UUID.randomUUID().toString();
        createdAt = new Timestamp(System.currentTimeMillis());
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public String getRecipientUuid() {
        return recipientUuid;
    }

    public void setRecipientUuid(String userUuid) {
        this.recipientUuid = userUuid;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }



    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getUserRequestorUuid() {
        return userRequestorUuid;
    }

    public void setUserRequestorUuid(String userRequestorUuid) {
        this.userRequestorUuid = userRequestorUuid;
    }

    public Timestamp getRequestLatestDate() {
        return requestLatestDate;
    }

    public void setRequestLatestDate(Timestamp requestLatestDate) {
        this.requestLatestDate = requestLatestDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getPatient() {
        return patient;
    }
}
