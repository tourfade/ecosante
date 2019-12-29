package com.kamitsoft.ecosante.constant;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.kamitsoft.ecosante.R;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;
import top.defaults.drawabletoolbox.DrawableBuilder;

public enum StatusConstant {
    NEW(0,0, Color.LTGRAY,R.string.newStatus), //0

    PENDING(1,1, Color.CYAN, R.string.pending), //0
    REVIEWED(2,2,Color.BLUE, R.string.reviewed),//1
    ACCEPTED(3,3,Color.GREEN, R.string.accepted),//2
    REJECTED(4,4,Color.RED,R.string.rejected),//-1
    ARCHIVED(5,-1,Color.GRAY,R.string.archived),//-1
    DELETED(6,-2,Color.YELLOW,R.string.deleted);//-1
    public final int status;
    public final int name;
    public final int index;
    public final int color;
    public final Drawable drawable;

    StatusConstant(int index, int status, @ColorInt int color, @StringRes int name){
        this.index = index;
        this.status = status;
        this.name = name;
        this.color = color;
        this.drawable =  new DrawableBuilder()
                .oval()
                .solidColor(color)
                .build();
    }




    public static StatusConstant ofStatus(int status){
        for (StatusConstant s: values()) {
            if (s.status == status){
                return s;
            }
        }
        return NEW;
    }
}
