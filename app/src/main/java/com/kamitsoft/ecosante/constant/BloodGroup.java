package com.kamitsoft.ecosante.constant;

import android.content.Context;

import com.kamitsoft.ecosante.R;

import androidx.annotation.StringRes;

public enum BloodGroup {

    UNKNOWN(0,-1,R.string.unknown),
    A_POSITIVE(1,10, R.string.aplus),
    B_POSITIVE(2,20, R.string.bplus),
    AB_POSITIVE(3,30, R.string.abplus),
    O_POSITIVE(4,40, R.string.oplus),
    A_NEGATIVE(5,50, R.string.aminus),
    B_NEGATIVE(6,60, R.string.bminus),
    AB_NEGATIVE(7,70, R.string.abminus),
    O_NEGATIVE(8,80, R.string.ominus);


    public final int group;
    public final int title;
    public final int index;

    BloodGroup(int index, int group, @StringRes int title){
        this.index = index;
        this.group = group;
        this.title = title;
    }

    public  String title(Context c){
        return c.getString(title);
    }
    public static BloodGroup sex(int v){
        for (BloodGroup g: values()) {
            if (g.group == v){
                return g;
            }
        }
        return UNKNOWN;
    }
    public static int indexOf(int v){
        for (BloodGroup g: values()) {
            if (g.group == v){
                return g.index;
            }
        }
        return UNKNOWN.index;
    }


}
