package com.kamitsoft.ecosante.database.converters;


import androidx.room.TypeConverter;

import java.sql.Timestamp;

public class TimestampTypeConverter {

    @TypeConverter
    public static Timestamp toTimestamp(Long value) {
        return value == null ? null : new Timestamp(value);
    }

    @TypeConverter
    public static Long toLong(Timestamp value) {
        return value == null ? null : value.getTime();
    }
}

