package com.kamitsoft.ecosante.model;


import com.kamitsoft.ecosante.model.json.ExtraData;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by fadel on 05/07/2019.
 */
@Entity
public class SummaryInfo {

    @PrimaryKey
    @NonNull
    private String uuid;
    private String patientUuid;
    private int patientID;

    private int doctorID;
    private String doctor;
    private String doctorCell;
    private String doctorEmail;

    private int specialistID;
    private String specialist;
    private String specialistCell;
    private String specialistEmail;

    private boolean surgery;
    private boolean idm;
    private boolean avc;
    private ExtraData falls;
    private String runningLongTreatment;

    private boolean drop, dementia, hta;
    private boolean epilepsy, irc,asthma;
    private boolean glaucoma, hepatitb, hyperthyroid,other;
    private boolean menopause;
    private int rhesus;
    private String notes;
    private ExtraData diabete;
    private ExtraData falciform;

    private Timestamp updatedAt;
    private Timestamp createdAt;
    private ExtraData surgeries;
    private Timestamp idmDate;
    private Timestamp avcDate;
    private Timestamp menopauseDate;
    private ExtraData allergies;


    public SummaryInfo(){
        uuid = UUID.randomUUID().toString();
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public int getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(int doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getDoctorCell() {
        return doctorCell;
    }

    public void setDoctorCell(String doctorCell) {
        this.doctorCell = doctorCell;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }

    public int getSpecialistID() {
        return specialistID;
    }

    public void setSpecialistID(int specialistID) {
        this.specialistID = specialistID;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getSpecialistCell() {
        return specialistCell;
    }

    public void setSpecialistCell(String specialistCell) {
        this.specialistCell = specialistCell;
    }

    public String getSpecialistEmail() {
        return specialistEmail;
    }

    public void setSpecialistEmail(String specialistEmail) {
        this.specialistEmail = specialistEmail;
    }

    public boolean isSurgery() {
        return surgery;
    }

    public void setSurgery(boolean surgery) {
        this.surgery = surgery;
    }

    public boolean isIdm() {
        return idm;
    }

    public void setIdm(boolean idm) {
        this.idm = idm;
    }

    public boolean isAvc() {
        return avc;
    }

    public void setAvc(boolean avc) {
        this.avc = avc;
    }



    public String getRunningLongTreatment() {
        return runningLongTreatment;
    }

    public void setRunningLongTreatment(String runningLongTreatment) {
        this.runningLongTreatment = runningLongTreatment;
    }



    public boolean isDrop() {
        return drop;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

    public boolean isDementia() {
        return dementia;
    }

    public void setDementia(boolean dementia) {
        this.dementia = dementia;
    }

    public boolean isHta() {
        return hta;
    }

    public void setHta(boolean hta) {
        this.hta = hta;
    }

    public boolean isEpilepsy() {
        return epilepsy;
    }

    public void setEpilepsy(boolean epilepsy) {
        this.epilepsy = epilepsy;
    }

    public boolean isIrc() {
        return irc;
    }

    public void setIrc(boolean irc) {
        this.irc = irc;
    }

    public boolean isAsthma() {
        return asthma;
    }

    public void setAsthma(boolean asthma) {
        this.asthma = asthma;
    }

    public boolean isDiabete() {
        return getDiabete().form >= 0;
    }


    public boolean isGlaucoma() {
        return glaucoma;
    }

    public void setGlaucoma(boolean glaucoma) {
        this.glaucoma = glaucoma;
    }

    public boolean isHepatitb() {
        return hepatitb;
    }

    public void setHepatitb(boolean hepatitb) {
        this.hepatitb = hepatitb;
    }

    public boolean isHyperthyroid() {
        return hyperthyroid;
    }

    public void setHyperthyroid(boolean hyperthyroid) {
        this.hyperthyroid = hyperthyroid;
    }

    public boolean isOther() {
        return other;
    }

    public void setOther(boolean other) {
        this.other = other;
    }

    public boolean isMenopause() {
        return menopause;
    }

    public void setMenopause(boolean menopause) {
        this.menopause = menopause;
    }

    public boolean isFalciform() {
        return getFalciform().percent >= 0;
    }

    public ExtraData getFalciform() {
        if(falciform == null){
            falciform = new ExtraData();
        }
        return falciform;
    }

    public void setFalciform(ExtraData falciform) {
        this.falciform = falciform;
    }

    public int getRhesus() {
        return rhesus;
    }

    public void setRhesus(int rhesus) {
        this.rhesus = rhesus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof SummaryInfo ? ((SummaryInfo) obj).uuid.equals(uuid):false;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }



    public ExtraData getDiabete() {
        if(diabete == null){
            diabete = new ExtraData();
        }
        return diabete;
    }

    public void setDiabete(ExtraData diabeteData) {
        this.diabete = diabeteData;
    }

    public ExtraData getSurgeries() {
        if(surgeries == null){
            surgeries = new ExtraData();
            surgeries.items = new ArrayList<>();
        }
        return surgeries;
    }

    public void setSurgeries(ExtraData surgeries) {
        this.surgeries = surgeries;
    }

    public ExtraData getFalls() {
        if(falls == null){
            falls = new ExtraData();
            falls.items = new ArrayList<>();
        }
        return falls;
    }
    public void setFalls(ExtraData falls) {
        this.falls = falls;
    }

    public Timestamp getIdmDate() {
        return idmDate;
    }

    public void setIdmDate(Timestamp idmDate) {
        this.idmDate = idmDate;
    }

    public Timestamp getAvcDate() {
        return avcDate;
    }

    public void setAvcDate(Timestamp avcDate) {
        this.avcDate = avcDate;
    }

    public Timestamp getMenopauseDate() {
        return menopauseDate;
    }

    public void setMenopauseDate(Timestamp menopauseDate) {
        this.menopauseDate = menopauseDate;
    }

    public ExtraData getAllergies() {
        if(allergies == null){
            allergies = new ExtraData();
            allergies.items = new ArrayList<>();
        }
        return allergies;
    }

    public void setAllergies(ExtraData allergies) {
        this.allergies = allergies;
    }
}
