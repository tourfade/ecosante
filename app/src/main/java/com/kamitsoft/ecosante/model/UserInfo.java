package com.kamitsoft.ecosante.model;


import com.kamitsoft.ecosante.model.json.Supervisor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by fadel toure on 05/09/2019.
 */
@Entity
public class UserInfo {

    private int accountID;
    @NonNull
    @PrimaryKey
    private String uuid;
    private int userID;
    private String username;
    private int title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String lang;
    private int sex;
    private String pob;
    private int[] dob;

    private int userType;
    private int status;
    private boolean deleted;
    private int specialityCode;
    private String speciality;
    private String address;
    private String fixPhone;
    private String mobilePhone;
    private String email;
    private Timestamp updatedAt;

    private String avatar;
    private String token;
    private Supervisor supervisor;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
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

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getAvatar() {
        return avatar;
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

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }



    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof UserInfo ? ((UserInfo) obj).uuid.equals(uuid):false;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public UserInfo(){
        uuid = UUID.randomUUID().toString();
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
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

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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


    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }


    public int getSpecialityCode() {
        return specialityCode;
    }

    public void setSpecialityCode(int specialityCode) {
        this.specialityCode = specialityCode;
    }
}
