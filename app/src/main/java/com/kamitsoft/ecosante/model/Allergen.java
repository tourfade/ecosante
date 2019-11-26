package com.kamitsoft.ecosante.model;

import java.sql.Timestamp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("code"), @Index("name"), @Index("description")})
public class Allergen {

    @PrimaryKey()
    private int code;
    private String name;
    private String description;
    private Timestamp lastUpdated;
    public Allergen(){
        lastUpdated = new Timestamp(System.currentTimeMillis());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Allergen && code == ((Allergen) obj).code;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
