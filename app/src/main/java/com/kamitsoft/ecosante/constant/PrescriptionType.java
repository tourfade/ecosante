package com.kamitsoft.ecosante.constant;

public enum PrescriptionType {
    LAB,
    PHARMACY;

    public static PrescriptionType getPrescriptionType(int prescriptionType) {
        for (PrescriptionType type : values()) {
            if (type.ordinal() == prescriptionType) {
                return type;
            }
        }
        return null;
    }
}
