package com.kamitsoft.ecosante.model.json;

import com.kamitsoft.ecosante.constant.OperatorType;

import androidx.annotation.NonNull;

public class RuleItem<T> {
    public String entity;
    public String field;
    public String label;
    public int operator; //-1-> !=; 0-> ==; 1-> <; 2-> >; 3-> <=; 4-> >=;
    public T value;

    @NonNull
    @Override
    public String toString() {
        return label + " " + OperatorType.of(operator).title + " " + value;
    }
}
