package com.kamitsoft.ecosante.model.json;

import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.model.UserInfo;

import java.sql.Timestamp;

import androidx.room.Entity;


public class Supervisor {
    public String supFullName;
    public int accountId;
    public String physicianUuid;
    public String nurseUuid;
    public boolean active;
    public Timestamp createdAt;
    public Timestamp updatedAt;

    public Supervisor(){
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }


}
