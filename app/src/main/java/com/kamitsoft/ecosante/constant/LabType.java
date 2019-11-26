package com.kamitsoft.ecosante.constant;


import android.content.Context;

import com.kamitsoft.ecosante.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

public enum LabType {
    HEMATOLOGY(1,100,  R.string.hematology,     R.layout.lab_dialog,       R.drawable.hematology),// for user check
    BACTERILOGY(2,200, R.string.bacteriology,   R.layout.lab_dialog,       R.drawable.bacteriology),// for user check
    SEROLOGY(3, 300,   R.string.serology,             R.layout.lab_dialog,       R.drawable.serology),
    BIOCHEMISTRY(4,400,R.string.biochemistry  ,           R.layout.lab_dialog,       R.drawable.biochemistry),
    OTHERLAB(0, 0,     R.string.otherlab,       R.layout.lab_dialog,       R.drawable.labs);


    public final int index;
    public final int type;
    public final int title;
    public final int pickerLayout;
    public final int icon;

    LabType(int idx, int type, @StringRes int title,   @LayoutRes int layout_picker, @DrawableRes int icon){
        this.index = idx;
        this.type = type;
        this.title = title;
        this.pickerLayout = layout_picker;
        this.icon = icon;
    }

    public static LabType labNumberOf(int labNumber) {
        do{
            labNumber = labNumber/10;
        }while (labNumber >= 10);
        return ofType(labNumber*100);
    }

    public String getLocaleName(Context context){
        return context.getString(title);
    }


    public static LabType ofType(int type){
       for(LabType u: values()){
           if(u.type == type){
               return u;
           }
       }
       return OTHERLAB;
    }

    public static LabType atIndex(int idx){
        for(LabType u: values()){
            if(u.index == idx){
                return u;
            }
        }
        return OTHERLAB;
    }

    public static int indexOf(int type){
        for(LabType u: values()){
            if(u.type == type){
                return u.index;
            }
        }
        return OTHERLAB.index;
    }


}