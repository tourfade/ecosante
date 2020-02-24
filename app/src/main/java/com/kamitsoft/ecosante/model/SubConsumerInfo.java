package com.kamitsoft.ecosante.model;

import java.sql.Timestamp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class SubConsumerInfo {
    @PrimaryKey
    @NonNull
    private String uuid;
    private String subscriptionUuid;
    private int accountID;
    private String userUuid;
    private int nbPhysicians;
    private int nbNurses;
    private int nbPatPerNurse;
    private Timestamp lastUpdate;

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public int getNbPhysicians() {
        return nbPhysicians;
    }

    public void setNbPhysicians(int nbPhysicians) {
        this.nbPhysicians = nbPhysicians;
    }

    public int getNbNurses() {
        return nbNurses;
    }

    public void setNbNurses(int nbNurses) {
        this.nbNurses = nbNurses;
    }

    public int getNbPatPerNurse() {
        return nbPatPerNurse;
    }

    public void setNbPatPerNurse(int nbPatPerNurse) {
        this.nbPatPerNurse = nbPatPerNurse;
    }



    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getSubscriptionUuid() {
        return subscriptionUuid;
    }

    public void setSubscriptionUuid(String subscriptionUuid) {
        this.subscriptionUuid = subscriptionUuid;
    }
}
