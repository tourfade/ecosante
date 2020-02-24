package com.kamitsoft.ecosante.model;

import java.sql.Timestamp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class SubInstanceInfo {
    private int accountID;

    @PrimaryKey
    @NonNull
    private String uuid;
    private int maxPhysicians;
    private int maxNurses;
    private int maxPatPerNurse;
    private Timestamp validUntil;
    private Timestamp created;
    private Timestamp lastUpdate;

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public int getMaxPhysicians() {
        return maxPhysicians;
    }

    public void setMaxPhysicians(int maxPhysicians) {
        this.maxPhysicians = maxPhysicians;
    }

    public int getMaxNurses() {
        return maxNurses;
    }

    public void setMaxNurses(int maxNurses) {
        this.maxNurses = maxNurses;
    }

    public int getMaxPatPerNurse() {
        return maxPatPerNurse;
    }

    public void setMaxPatPerNurse(int maxPatPerNurse) {
        this.maxPatPerNurse = maxPatPerNurse;
    }



    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Timestamp getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Timestamp until) {
        this.validUntil = until;
    }
}
