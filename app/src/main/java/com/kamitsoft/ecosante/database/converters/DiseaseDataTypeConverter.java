package com.kamitsoft.ecosante.database.converters;


import com.google.gson.Gson;
import com.kamitsoft.ecosante.model.json.ExtraData;

import androidx.room.TypeConverter;

public class DiseaseDataTypeConverter {

    @TypeConverter
    public static ExtraData toDiabeteData(String value) {
        return value == null ? null : new Gson().fromJson(value, ExtraData.class);
    }

    @TypeConverter
    public static String toString(ExtraData value) {
        return value == null ? null : new Gson().toJson(value);
    }
}

