package com.kamitsoft.ecosante.database.converters;


import com.google.gson.Gson;
import com.kamitsoft.ecosante.model.json.Monitor;
import com.kamitsoft.ecosante.model.json.Rule;

import androidx.room.TypeConverter;

public class RuleTypeConverter {

    @TypeConverter
    public static Rule json2Rule(String value) {

        return value == null ? null : new Gson().fromJson(value, Rule.class);
    }

    @TypeConverter
    public static  String monitor2Json(Rule value) {
        return value == null ? null : new Gson().toJson(value);
    }
}

