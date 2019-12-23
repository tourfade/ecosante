package com.kamitsoft.ecosante.model.json;

import java.sql.Timestamp;


public class Monitor {
    public String monitorFullName;
    public int accountId;
    public String monitorUuid;
    public String patientUuid;
    public boolean active;
    public Timestamp createdAt;
    public Timestamp updatedAt;

    public Monitor(){
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }



}
