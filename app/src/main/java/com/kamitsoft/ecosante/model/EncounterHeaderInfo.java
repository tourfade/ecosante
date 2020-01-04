package com.kamitsoft.ecosante.model;

import com.kamitsoft.ecosante.model.json.Monitor;
import com.kamitsoft.ecosante.model.json.Status;
import com.kamitsoft.ecosante.model.json.Supervisor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class EncounterHeaderInfo {

    private String uuid;
    private int patientID;
    private Timestamp createdAt;
    private float pressureSystolic;
    private float pressureDiastolic;
    private float glycemy;
    private int glycemyState;
    private String patientUuid;
    private String firstName;
    private String middleName;
    private String lastName;
    private int sex;
    private String pob;
    private Timestamp dob;
    private String mobile;
    private String  avatar;
    private Monitor monitor;
    private Supervisor supervisor;
    private List<Status> status;

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPob() {
        return pob;
    }

    public void setPob(String pob) {
        this.pob = pob;
    }

    public Timestamp getDob() {
        return dob;
    }

    public void setDob(Timestamp dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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

    public float getGlycemy() {
        return glycemy;
    }

    public void setGlycemy(float glycemy) {
        this.glycemy = glycemy;
    }

    public int getGlycemyState() {
        return glycemyState;
    }

    public void setGlycemyState(int glycemyState) {
        this.glycemyState = glycemyState;
    }


    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof EncounterHeaderInfo ? ((EncounterHeaderInfo) obj).uuid.equals(uuid):false;
    }


    public String getAvatar() {
        return avatar;
    }
    public void  setAvatar(String av) {
         avatar = av;
    }

    public List<Status> getStatus() {
        if(status == null){
            status = new ArrayList<>();
            status.add(new Status());
        }
        return status;
    }

    public void setStatus(List<Status> status) {
        this.status = status;
    }

    public Status currentStatus(){
        Status current = getStatus().get(0);
        for(Status s:status)
            if(s.date != null && s.date.after(current.date)){
                current = s;
            }

        return current;
    }
}
