package com.kamitsoft.ecosante.constant;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.kamitsoft.ecosante.R;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;
import top.defaults.drawabletoolbox.DrawableBuilder;

public enum UserStatusConstant {

    AVAILABLE(0,0, Color.GREEN,R.string.available), //0
    UNAVAILABLE(1,1, Color.RED, R.string.unavailable), //0
    UNKNOWN(3,-1, Color.LTGRAY, R.string.unknown); //0

    public final int status;
    public final int name;
    public final int index;
    public final int color;
    public final Drawable drawable;

    UserStatusConstant(int index, int status, @ColorInt int color, @StringRes int name){
        this.index = index;
        this.status = status;
        this.name = name;
        this.color = color;
        this.drawable =  new DrawableBuilder()
                .oval()
                .solidColor(color)
                .build();
    }




    public static UserStatusConstant ofStatus(int status){
        for (UserStatusConstant s: values()) {
            if (s.status == status){
                return s;
            }
        }
        return UNKNOWN;
    }
}
