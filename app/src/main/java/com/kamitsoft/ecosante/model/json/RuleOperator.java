package com.kamitsoft.ecosante.model.json;

public class RuleOperator implements IsRule{
    public String name;
    public int value;

    @Override
    public String toString() {
        return name;
    }
}
