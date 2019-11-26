package com.kamitsoft.ecosante.constant;

import android.content.Context;

import com.kamitsoft.ecosante.R;

import androidx.annotation.StringRes;

public enum MedicationStatus {
    NEW(0,0, R.string.new_med), //0
    RUNNING(1,1,R.string.running),//1
    TERMINATED(2,2,R.string.terminated),//2
    UNKNOWN(3,-1,R.string.unknown);//-1
    public final int status;
    public final int title;
    public final int index;

    MedicationStatus(int index, int status, @StringRes int title){
        this.index = index;
        this.status = status;
        this.title = title;
    }

    public  String title(Context c){
        return c.getString(title);
    }
    public static MedicationStatus ofStatus(int v){
        for (MedicationStatus g: values()) {
            if (g.status == v){
                return g;
            }
        }
        return UNKNOWN;
    }
    public static MedicationStatus atIndex(int index){
        for (MedicationStatus g: values()) {
            if (g.index == index){
                return g;
            }
        }
        return UNKNOWN;
    }


}
