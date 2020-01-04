package com.kamitsoft.ecosante.model;


import com.kamitsoft.ecosante.database.PatientDAO;
import com.kamitsoft.ecosante.model.json.Monitor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by fadel on 05/07/2019.
 */
@Entity
public class PatientInfo {

    @PrimaryKey
    @NonNull
    private String uuid;
    private int patientID;
    private String userName;
    private String firstName;
    private String middleName;
    private String lastName;
    private int sex;
    private String pob;
    private int[] dob;
    private int maritalStatus;
    private String occupation;
    private String mobile;
    private boolean retired;
    private boolean ipres;
    private boolean fnr;
    private boolean official;
    private String address;
    private String fixPhone;
    private String email;
    private String matricule;
    private String contactPerson;
    private String contactPhone1;
    private String contactPhone2;
    private String contactAddress;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String avatar;
    private Monitor monitor;
    private boolean deleted;

    public PatientInfo(){
        uuid = UUID.randomUUID().toString();
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
        matricule = uuid.substring(0,4)+String.valueOf(System.currentTimeMillis()%10000);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

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

    public int[] getDob() {
        return dob;
    }

    public void setDob(int[] dob) {
        this.dob = dob;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public int getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(int maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean getRetired() {
        return retired;
    }

    public void setRetired(boolean retired) {
        this.retired = retired;
    }

    public boolean getIpres() {
        return ipres;
    }

    public void setIpres(boolean ipres) {
        this.ipres = ipres;
    }

    public boolean getFnr() {
        return fnr;
    }

    public void setFnr(boolean fnr) {
        this.fnr = fnr;
    }

    public boolean getOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFixPhone() {
        return fixPhone;
    }

    public void setFixPhone(String fixPhone) {
        this.fixPhone = fixPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone1() {
        return contactPhone1;
    }

    public void setContactPhone1(String contactPhone1) {
        this.contactPhone1 = contactPhone1;
    }

    public String getContactPhone2() {
        return contactPhone2;
    }

    public void setContactPhone2(String contactPhone2) {
        this.contactPhone2 = contactPhone2;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof PatientInfo ? ((PatientInfo) obj).uuid.equals(uuid):false;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Monitor getMonitor() {
        monitor = monitor == null ? new Monitor():monitor;
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
