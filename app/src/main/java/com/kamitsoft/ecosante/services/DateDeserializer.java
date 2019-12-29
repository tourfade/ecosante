package com.kamitsoft.ecosante.services;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateDeserializer implements JsonSerializer<Date>,JsonDeserializer<Date> {
    private final SimpleDateFormat dateFormat;

    public DateDeserializer(){
        super();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);      //This is the format I need
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    @Override
    public Date deserialize(JsonElement jsonElement, Type typeOF, JsonDeserializationContext context)
            throws JsonParseException {
        if (jsonElement == null)
            return null;

        try {

            return dateFormat.parse(jsonElement.getAsString());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return null;
    }
    @Override
    public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateFormat.format(date));
    }
}
