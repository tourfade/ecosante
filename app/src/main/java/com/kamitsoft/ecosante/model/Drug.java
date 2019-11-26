package com.kamitsoft.ecosante.model;

import com.kamitsoft.ecosante.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("dci"), @Index("reference")})
public class Drug {
    @PrimaryKey(autoGenerate = false)
    private  int drugnumber;
    private  int catnumber;
    private String dci;
    private String dosage;
    private String form;
    private String reference;
    private boolean cases;
    private boolean posts;
    private boolean centers;
    private boolean eps1;
    private boolean eps2;
    private boolean eps3;
    private String createdBy;
    private String lastUpdatedBy;
    private int status;



    public int getCatnumber() {
        return catnumber;
    }

    public void setCatnumber(int catnumber) {
        this.catnumber = catnumber;
    }

    public int getDrugnumber() {
        return drugnumber;
    }

    public void setDrugnumber(int drugnumber) {
        this.drugnumber = drugnumber;
    }

    public String getDci() {
        return dci;
    }

    public void setDci(String dci) {
        this.dci = dci;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public boolean isCases() {
        return cases;
    }

    public void setCases(boolean cases) {
        this.cases = cases;
    }

    public boolean isPosts() {
        return posts;
    }

    public void setPosts(boolean posts) {
        this.posts = posts;
    }

    public boolean isCenters() {
        return centers;
    }

    public void setCenters(boolean centers) {
        this.centers = centers;
    }

    public boolean isEps1() {
        return eps1;
    }

    public void setEps1(boolean eps1) {
        this.eps1 = eps1;
    }

    public boolean isEps2() {
        return eps2;
    }

    public void setEps2(boolean eps2) {
        this.eps2 = eps2;
    }

    public boolean isEps3() {
        return eps3;
    }

    public void setEps3(boolean eps3) {
        this.eps3 = eps3;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @NonNull
    @Override
    public String toString() {
        return Utils.niceFormat(this);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Drug && drugnumber == ((Drug) obj).drugnumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
