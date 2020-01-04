package com.kamitsoft.ecosante.database.converters;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kamitsoft.ecosante.model.json.Status;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.room.TypeConverter;

public class SqlDateTypeConverter {
    static Type listType = new TypeToken<int[]>(){}.getType();

    @TypeConverter
    public static int[] toDate(String value) {
        if(value == null){
            return  null;
        }
        int[] date = new Gson().fromJson(value, listType);
        return date;
    }

    @TypeConverter
    public static String toString(int[] value) {
        if(value == null){
            return null;
        }

        return new Gson().toJson(value);
    }
}

