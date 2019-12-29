package com.kamitsoft.ecosante.database.converters;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kamitsoft.ecosante.model.json.ExtraData;
import com.kamitsoft.ecosante.model.json.Status;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.room.TypeConverter;

public class StatusTypeConverter {
    static Type listType = new TypeToken<ArrayList<Status>>(){}.getType();

    @TypeConverter
    public static List<Status> json2DiseaseData(String value) {
        return value == null ? null : new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String diseaseData2Json(List<Status> value) {
        return value == null ? null : new Gson().toJson(value);
    }
}

