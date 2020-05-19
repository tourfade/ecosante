package com.kamitsoft.ecosante.database.converters;


import com.google.gson.Gson;
import com.kamitsoft.ecosante.model.json.Area;
import com.kamitsoft.ecosante.model.json.Monitor;

import androidx.room.TypeConverter;

public class AreaTypeConverter {

    @TypeConverter
    public static Area json2Monitor(String value) {

        return value == null ? null : new Gson().fromJson(value, Area.class);
    }

    @TypeConverter
    public static  String monitor2Json(Area value) {
        return value == null ? null : new Gson().toJson(value);
    }
}

