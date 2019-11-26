package com.kamitsoft.ecosante.model;

import java.sql.Timestamp;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


public class EncounterHeaderInfo {

    private String uuid;
    private int patientID;
    private Timestamp createdAt;
    private float pressusreSystolic;
    private float pressusreDiastolic;
    private float glycemy;
    private int glycemyState;
    private String user;
    private String patientUuid;
    private String userUuid;
    private String firstName;
    private String middleName;
    private String lastName;
    private int sex;
    private String pob;
    private Timestamp dob;
    private String mobile;
    private String  avatar;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPob() {
        return pob;
    }

    public void setPob(String pob) {
        this.pob = pob;
    }

    public Timestamp getDob() {
        return dob;
    }

    public void setDob(Timestamp dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
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

    public float getPressusreSystolic() {
        return pressusreSystolic;
    }

    public void setPressusreSystolic(float pressusreSystolic) {
        this.pressusreSystolic = pressusreSystolic;
    }

    public float getPressusreDiastolic() {
        return pressusreDiastolic;
    }

    public void setPressusreDiastolic(float pressusreDiastolic) {
        this.pressusreDiastolic = pressusreDiastolic;
    }

    public float getGlycemy() {
        return glycemy;
    }

    public void setGlycemy(float glycemy) {
        this.glycemy = glycemy;
    }

    public int getGlycemyState() {
        return glycemyState;
    }

    public void setGlycemyState(int glycemyState) {
        this.glycemyState = glycemyState;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof EncounterHeaderInfo ? ((EncounterHeaderInfo) obj).uuid.equals(uuid):false;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }


    public String getAvatar() {
        return avatar;
    }
    public void  setAvatar(String av) {
         avatar = av;
    }
}
