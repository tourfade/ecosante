package com.kamitsoft.ecosante.constant;

import android.content.Context;

import androidx.annotation.StringRes;

import com.kamitsoft.ecosante.R;

public enum Gender {
    MALE(0,0, R.string.male), //0
    FEMALE(1,1,R.string.female),//1
    OTHER(2,2,R.string.orther),//2
    UNKNOWN(3,-1,R.string.unknown);//-1
    public final int sex;
    public final int title;
    public final int index;

    Gender(int index, int sex, @StringRes int title){
        this.index = index;
        this.sex = sex;
        this.title = title;
    }

    public  String title(Context c){
        return c.getString(title);
    }
    public static Gender sex(int v){
        for (Gender g: values()) {
            if (g.sex == v){
                return g;
            }
        }
        return UNKNOWN;
    }
    public static int indexOf(int v){
        for (Gender g: values()) {
            if (g.sex == v){
                return g.index;
            }
        }
        return UNKNOWN.index;
    }


}
