package com.kamitsoft.ecosante.model;

import com.kamitsoft.ecosante.model.json.Monitor;
import com.kamitsoft.ecosante.model.json.Supervisor;

import java.sql.Timestamp;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EncounterInfo {
    @PrimaryKey
    @NonNull
    private String uuid;
    private int patientID;
    private Timestamp createdAt;
    private float pressureSystolic;
    private float pressureDiastolic;
    private float temperature;
    private int temperatureBodyPart;
    private float weight;
    private float height;
    private float waistSize;
    private float glycemy;
    private int glycemyState;
    private int breathRate;
    private int heartRate;
    private int heartRateSite;

    private boolean anorexia;
    private boolean asthenia;
    private boolean dysphnea;
    private boolean omi;
    private boolean apathy;
    private boolean emaciation;

    private int autonomy;
    private int orientation;
    private String field;

    private boolean smoke;
    private boolean alcohol;
    private boolean tea;
    private boolean otherBehaviors;
    private int smokeNbCigarettes;
    private int alcoholNbCups;
    private int teaNbCups;
    private String otherBehaviorsNotes;

    private String runningTreatment;
    private int runningTreatmentDuration;
    private int runningTreatmentDurationUnit;
    private String advising;
    private boolean diabeticDiet;
    private boolean hypocaloricDiet;
    private boolean hyposodeDiet;
    private boolean hyperprotidicDiet;
    private String patientUuid;
    private String userUuid;
    private Monitor monitor;
    private Supervisor supervisor;
    private Timestamp updatedAt;
    private boolean deleted;

    public String getRunningTreatment() {
        return runningTreatment;
    }

    public void setRunningTreatment(String runningTreatment) {
        this.runningTreatment = runningTreatment;
    }

    public int getRunningTreatmentDuration() {
        return runningTreatmentDuration;
    }

    public void setRunningTreatmentDuration(int runningTreatmentDuration) {
        this.runningTreatmentDuration = runningTreatmentDuration;
    }

    public int getRunningTreatmentDurationUnit() {
        return runningTreatmentDurationUnit;
    }

    public void setRunningTreatmentDurationUnit(int runningTreatmentDurationUnit) {
        this.runningTreatmentDurationUnit = runningTreatmentDurationUnit;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public void setSmoke(boolean smoke) {
        this.smoke = smoke;
    }

    public boolean isAlcohol() {
        return alcohol;
    }

    public void setAlcohol(boolean alcohol) {
        this.alcohol = alcohol;
    }

    public boolean isTea() {
        return tea;
    }

    public void setTea(boolean tea) {
        this.tea = tea;
    }

    public boolean isOtherBehaviors() {
        return otherBehaviors;
    }

    public void setOtherBehaviors(boolean otherBehaviors) {
        this.otherBehaviors = otherBehaviors;
    }

    public int getSmokeNbCigarettes() {
        return smokeNbCigarettes;
    }

    public void setSmokeNbCigarettes(int smokeNbCigarettes) {
        this.smokeNbCigarettes = smokeNbCigarettes;
    }

    public int getAlcoholNbCups() {
        return alcoholNbCups;
    }

    public void setAlcoholNbCups(int alcoholNbCups) {
        this.alcoholNbCups = alcoholNbCups;
    }

    public int getTeaNbCups() {
        return teaNbCups;
    }

    public void setTeaNbCups(int teaNbCups) {
        this.teaNbCups = teaNbCups;
    }

    public String getOtherBehaviorsNotes() {
        return otherBehaviorsNotes;
    }

    public void setOtherBehaviorsNotes(String otherBehaviorsNotes) {
        this.otherBehaviorsNotes = otherBehaviorsNotes;
    }

    public int getAutonomy() {
        return autonomy;
    }

    public void setAutonomy(int autonomy) {
        this.autonomy = autonomy;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isAnorexia() {
        return anorexia;
    }

    public void setAnorexia(boolean anorexia) {
        this.anorexia = anorexia;
    }

    public boolean isAsthenia() {
        return asthenia;
    }

    public void setAsthenia(boolean asthenia) {
        this.asthenia = asthenia;
    }

    public boolean isDysphnea() {
        return dysphnea;
    }

    public void setDysphnea(boolean dysphnea) {
        this.dysphnea = dysphnea;
    }

    public boolean isOmi() {
        return omi;
    }

    public void setOmi(boolean omi) {
        this.omi = omi;
    }

    public boolean isApathy() {
        return apathy;
    }

    public void setApathy(boolean apathy) {
        this.apathy = apathy;
    }

    public boolean isEmaciation() {
        return emaciation;
    }

    public void setEmaciation(boolean emaciation) {
        this.emaciation = emaciation;
    }

    public  EncounterInfo(){

        uuid = UUID.randomUUID().toString();
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());

    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public float getPressureSystolic() {
        return pressureSystolic;
    }

    public void setPressureSystolic(float pressureSystolic) {
        this.pressureSystolic = pressureSystolic;
    }

    public float getPressureDiastolic() {
        return pressureDiastolic;
    }

    public void setPressureDiastolic(float pressureDiastolic) {
        this.pressureDiastolic = pressureDiastolic;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWaistSize() {
        return waistSize;
    }

    public void setWaistSize(float waistSize) {
        this.waistSize = waistSize;
    }

    public float getGlycemy() {
        return glycemy;
    }

    public void setGlycemy(float glycemy) {
        this.glycemy = glycemy;
    }

    public int getBreathRate() {
        return breathRate;
    }

    public void setBreathRate(int breathRate) {
        this.breathRate = breathRate;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getTemperatureBodyPart() {
        return temperatureBodyPart;
    }

    public void setTemperatureBodyPart(int temperatureBodyPart) {
        this.temperatureBodyPart = temperatureBodyPart;
    }

    public int getGlycemyState() {
        return glycemyState;
    }

    public void setGlycemyState(int glycemyState) {
        this.glycemyState = glycemyState;
    }

    public void setHeartRateSite(int heartRateSite) {
        this.heartRateSite = heartRateSite;
    }

    public int getHeartRateSite() {
        return heartRateSite;
    }

    public void setAdvising(String advisings) {
        this.advising = advisings;
    }

    public String getAdvising() {
        return advising;
    }

    public boolean getDiabeticDiet() {
        return diabeticDiet;
    }

    public void setDiabeticDiet(boolean diabeticDiet) {
        this.diabeticDiet = diabeticDiet;
    }

    public boolean getHypocaloricDiet() {
        return hypocaloricDiet;
    }

    public void setHypocaloricDiet(boolean hypocaloricDiet) {
        this.hypocaloricDiet = hypocaloricDiet;
    }

    public boolean getHyposodeDiet() {
        return hyposodeDiet;
    }

    public void setHyposodeDiet(boolean hyposodeDiet) {
        this.hyposodeDiet = hyposodeDiet;
    }

    public boolean getHyperprotidicDiet() {
        return hyperprotidicDiet;
    }

    public void setHyperprotidicDiet(boolean hyperprotidicDiet) {
        this.hyperprotidicDiet = hyperprotidicDiet;
    }


    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof EncounterInfo ? ((EncounterInfo) obj).uuid.equals(uuid):false;
    }



    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Supervisor getSupervisor() {
        supervisor = supervisor == null ? new Supervisor(): supervisor;
        return supervisor;
    }

    public Monitor getMonitor() {
        monitor = monitor == null?new Monitor():monitor;
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }
}
