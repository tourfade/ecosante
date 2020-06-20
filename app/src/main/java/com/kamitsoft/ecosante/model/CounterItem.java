package com.kamitsoft.ecosante.model;

import com.kamitsoft.ecosante.model.json.Monitor;
import com.kamitsoft.ecosante.model.json.Supervisor;

public class CounterItem {
    private String uuid;
    private String patientUuid;
    private String districtUuid;
    private Monitor monitor;
    private int userType;

    private Supervisor supervisor;
    private int accountId;

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


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }


    public String getDistrictUuid() {
        return districtUuid;
    }

    public void setDistrictUuid(String districtUuid) {
        this.districtUuid = districtUuid;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}
