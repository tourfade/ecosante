package com.kamitsoft.ecosante.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("name")})
public class Speciality {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private int fieldvalue;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFieldvalue() {
        return fieldvalue;
    }

    public void setFieldvalue(int fieldvalue) {
        this.fieldvalue = fieldvalue;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Speciality ? ((Speciality) obj).id == id:false;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}

