package com.kamitsoft.ecosante.services;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class UnsyncFile {

    @NonNull
    @PrimaryKey
    private String fkey;
    private long date;
    private int tries;
    private long lastTry;
    private int type;// avatar, file

    public String getFkey() {
        return fkey;
    }

    public void setFkey(String fkey) {
        this.fkey = fkey;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    public long getLastTry() {
        return lastTry;
    }

    public void setLastTry(long lastTry) {
        this.lastTry = lastTry;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
