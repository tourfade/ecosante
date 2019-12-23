package com.kamitsoft.ecosante.constant;


import android.content.Context;

import com.kamitsoft.ecosante.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

public enum VitalType {
    UNDETERMINATED(0, R.string.underterminated, 0,0,R.drawable.vital),// for user check
    PRESSURE(1,R.string.pressure, R.string.cmhg,R.layout.pressure_vital, R.drawable.blood_pressure),
    TEMPERATURE(2,R.string.temperature, R.string.dc,R.layout.temperature_vital,R.drawable.vs_temperature),
    WEIGHT(3,R.string.weight, R.string.kg,R.layout.weight_vital,R.drawable.vs_weight),
    GLYCEMY(4,R.string.gaj, R.string.mgpdl,R.layout.glycemy_vital,R.drawable.gluco),
    HEIGHT(5,R.string.height, R.string.cm,R.layout.height_vital,R.drawable.height),
    WAISTSIZE(6,R.string.waistSize, R.string.cm,R.layout.waist_vital,R.drawable.waist),
    BREATHRATE(7,R.string.resprate_long, R.string.respmn,R.layout.breathrate_vital,R.drawable.vs_breath),
    HEARTRATE(8,R.string.heartrate_long, R.string.batpmn,R.layout.heart_vital,R.drawable.heartrate);



    public final int type;
    public final int title;
    public final int unit;
    public final int pickerLayout;
    public final int drawable;

    VitalType(int type, @StringRes int title, int unit, @LayoutRes int layout_picker, @DrawableRes  int drawable ){
        this.type = type;
        this.title = title;
        this.unit = unit;
        this.pickerLayout = layout_picker;
        this.drawable = drawable;
    }
    public String getLocaleName(Context context){
        return context.getString(title);
    }




    public static VitalType typeOf(int type){
       for(VitalType u: values()){
           if(u.type == type){
               return u;
           }
       }
       return UNDETERMINATED;
    }



}