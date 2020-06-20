package com.kamitsoft.ecosante.model;

import com.kamitsoft.ecosante.model.json.Monitor;
import com.kamitsoft.ecosante.model.json.Status;
import com.kamitsoft.ecosante.model.json.Supervisor;

import java.util.List;

public class ECounterItem {
    private String uuid;
    private String patientUuid;
    private Monitor monitor;
    private Supervisor supervisor;
    private List<Status> status;
    private String districtUuid;

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

    public List<Status> getStatus() {
        return status;
    }

    public void setStatus(List<Status> status) {
        this.status = status;
    }
    public String supUuid(){
        return supervisor==null?"":supervisor.physicianUuid;
    }
    public String monitorUuid(){
        return monitor==null ? "":monitor.monitorUuid;
    }
    public Status currentStatus(){
        Status current = getStatus().get(0);
        for(Status s:status)
            if(s.date != null && s.date.after(current.date)){
                current = s;
            }

        return current;
    }

    public String getDistrictUuid() {
        return districtUuid;
    }

    public void setDistrictUuid(String districtUuid) {
        this.districtUuid = districtUuid;
    }
}
