package com.kamitsoft.ecosante.constant;

import android.content.Context;

import androidx.annotation.StringRes;

import com.kamitsoft.ecosante.R;


public enum MaritalStatus {
    SINGLE(0,0, R.string.single), //0
    MARRIED(1,1,R.string.married),//1
    DIVORCED(2,2,R.string.divorced),//2
    WIDOW(3,-1,R.string.widow),//-1
    UNKNOWN(4,-1,R.string.unknown);//-1
    public final int status;
    public final int title;
    public final int index;

    MaritalStatus(int idx, int status, @StringRes int title){
        this.index = idx;
        this.status = status;
        this.title = title;
    }

    public static int indexOf(int status) {
        for (MaritalStatus g: values()) {
            if (g.status == status){
                return g.index;
            }
        }
        return UNKNOWN.index;
    }

    public  String title(Context c){
        return c.getString(title);
    }
    public static MaritalStatus status(int v){
        for (MaritalStatus g: values()) {
            if (g.status == v){
                return g;
            }
        }
        return UNKNOWN;
    }


}
