package com.kamitsoft.ecosante.model.json;

import com.kamitsoft.ecosante.constant.StatusConstant;

import java.sql.Timestamp;

public class Status {
    public int status;
    public Timestamp date;
    public Status(){
        status = StatusConstant.NEW.status;
        date = new Timestamp(System.currentTimeMillis());
    }
    public Status(int status){
        this.status = status;
        this.date = new Timestamp(System.currentTimeMillis());
    }
}
