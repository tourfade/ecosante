package com.kamitsoft.ecosante.database.converters;


import com.google.gson.Gson;
import com.kamitsoft.ecosante.model.json.Monitor;
import com.kamitsoft.ecosante.model.json.Supervisor;

import androidx.room.TypeConverter;

public class MonitorTypeConverter {

    @TypeConverter
    public static Monitor json2Monitor(String value) {

        return value == null ? null : new Gson().fromJson(value, Monitor.class);
    }

    @TypeConverter
    public static  String monitor2Json(Monitor value) {
        return value == null ? null : new Gson().toJson(value);
    }
}

