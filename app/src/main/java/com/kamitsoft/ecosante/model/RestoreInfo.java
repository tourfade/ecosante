package com.kamitsoft.ecosante.model;

import java.sql.Date;
import java.sql.Timestamp;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


public class RestoreInfo {
    public String account;
    public String email;
    public String mobile;
    public int[] dob;

}