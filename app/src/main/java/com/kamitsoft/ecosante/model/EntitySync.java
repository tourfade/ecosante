package com.kamitsoft.ecosante.model;

import java.sql.Timestamp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity()
public class EntitySync {

    @NonNull @PrimaryKey
    private String entity;
    private long lastSynced;

    @NonNull
    public String getEntity() {
        return entity;
    }

    public void setEntity(@NonNull String entity) {
        this.entity = entity;
    }

    public long getLastSynced() {
        return lastSynced;
    }

    public void setLastSynced(long lastSynced) {
        this.lastSynced = lastSynced;
    }

    public EntitySync(){
        lastSynced = System.currentTimeMillis();
    }


}
