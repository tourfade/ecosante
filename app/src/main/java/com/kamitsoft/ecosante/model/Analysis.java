package com.kamitsoft.ecosante.model;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("labName"), @Index("labNumber")})
public class Analysis {

    @PrimaryKey()
    private  int labNumber;
    private  int viewModel;
    private  String labName;
    /*private boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
*/

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Analysis && ((Analysis) obj).labNumber == labNumber;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public int getLabNumber() {
        return labNumber;
    }

    public void setLabNumber(int labNumber) {
        this.labNumber = labNumber;
    }

    public int getViewModel() {
        return viewModel;
    }

    public void setViewModel(int viewModel) {
        this.viewModel = viewModel;
    }


    @NonNull
    @Override
    public String toString() {
        return labName;
    }
}
