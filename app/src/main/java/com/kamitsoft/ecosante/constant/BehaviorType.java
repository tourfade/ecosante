package com.kamitsoft.ecosante.constant;


import android.content.Context;

import com.kamitsoft.ecosante.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

public enum BehaviorType {
    OTHERBEHAVIORS(0, R.string.otherBehaviors, R.string.otherBehaviors, 0,R.layout.other_behav_picker, R.drawable.behavors),// for user check
    SMOKING(1, R.string.smoking, R.string.smokedcigretperday, R.string.cigperday,R.layout.behav_picker, R.drawable.cigarette),// for user check
    ETHYLISM(2,R.string.ethylism, R.string.drunkcupperday,R.string.gcupperday,R.layout.behav_picker,R.drawable.alcohol),
    TEA(3,R.string.tea, R.string.drunktassperday, R.string.cupperday,R.layout.behav_picker, R.drawable.tea),
    DURATION(4,R.string.duration, R.string.traitmentduration, 0,R.layout.ltt_duration_picker, R.drawable.calendar_full);


    public final int type;
    public final int title;
    public final int hint;
    public final int unit;
    public final int pickerLayout;
    public final int icon;

    BehaviorType(int type, @StringRes int title, @StringRes int hint, int unit, @LayoutRes int layout_picker, @DrawableRes int icon){
        this.type = type;
        this.title = title;
        this.hint = hint;
        this.unit = unit;
        this.pickerLayout = layout_picker;
        this.icon = icon;
    }
    public String getLocaleName(Context context){
        return context.getString(title);
    }


    public static BehaviorType typeOf(int type){
       for(BehaviorType u: values()){
           if(u.type == type){
               return u;
           }
       }
       return OTHERBEHAVIORS;
    }



}