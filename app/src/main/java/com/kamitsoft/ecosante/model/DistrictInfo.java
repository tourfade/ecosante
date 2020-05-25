package com.kamitsoft.ecosante.model;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.kamitsoft.ecosante.model.json.Area;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DistrictInfo {

    private int id;
    @PrimaryKey
    @NonNull
    private String uuid;
    public int accountID;
    private String name;
    private String description;
    private int population;
    private int maxNurse;
    private int maxPhysist;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean deleted;
    private Area area;
    private boolean needUpdate;

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getMaxNurse() {
        return maxNurse;
    }

    public void setMaxNurse(int maxNurse) {
        this.maxNurse = maxNurse;
    }

    public int getMaxPhysist() {
        return maxPhysist;
    }

    public void setMaxPhysist(int maxPhysist) {
        this.maxPhysist = maxPhysist;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public DistrictInfo(){
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


    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof DistrictInfo ? ((DistrictInfo) obj).uuid.equals(uuid):false;
    }


    public Area getArea() {
        if(area == null){
            area = new Area();
            area.fillColor = Color.parseColor("#ffa911");
            area.strokeColor = Color.parseColor("#0494ff");
        }
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public boolean isNeedUpdate() {
        return needUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
    }
}

