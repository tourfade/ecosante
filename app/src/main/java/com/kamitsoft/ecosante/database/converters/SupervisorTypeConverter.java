package com.kamitsoft.ecosante.database.converters;


import com.google.gson.Gson;
import com.kamitsoft.ecosante.model.json.Supervisor;

import androidx.room.TypeConverter;

public class SupervisorTypeConverter {

    @TypeConverter
    public static Supervisor json2Supervisor(String value) {

        return value == null ? null : new Gson().fromJson(value, Supervisor.class);
    }

    @TypeConverter
    public static  String supervisor2Json(Supervisor value) {
        return value == null ? null : new Gson().toJson(value);
    }
}

