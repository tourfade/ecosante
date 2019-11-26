package com.kamitsoft.ecosante.constant;

import android.content.Context;

import com.kamitsoft.ecosante.R;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

public enum AppointmentRequestStatus {
    PENDING(android.R.color.holo_orange_light,0, R.string.pending), //0
    ACCEPTED(android.R.color.holo_green_light,1,R.string.accepted),//1
    REJECTED(android.R.color.holo_red_light,2,R.string.rejected);//2
    public final int status;
    public final int title;
    public final int color;

    AppointmentRequestStatus(@ColorRes int color, int status, @StringRes int title){
        this.color = color;
        this.status = status;
        this.title = title;

    }

    public  String title(Context c){
        return c.getString(title);
    }
    public static AppointmentRequestStatus ofStatus(int v){
        for (AppointmentRequestStatus g: values()) {
            if (g.status == v){
                return g;
            }
        }
        return PENDING;
    }
    public static int ofColor(int v){
        for (AppointmentRequestStatus g: values()) {
            if (g.status == v){
                return g.color;
            }
        }
        return PENDING.color;
    }


}
