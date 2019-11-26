package com.kamitsoft.ecosante.constant;

import android.content.Context;

import androidx.annotation.StringRes;

import com.kamitsoft.ecosante.R;

public enum TitleType {

    UNKNOWN(0,0,R.string.unknown),
    DOCTOR(1,2,R.string.dr),
    PROFESSOR(2,1,R.string.pr),
    MISTRESS(3,4,R.string.mst),
    MISS(4,5,R.string.mss),
    MISTER(5,3,R.string.mr),
    OTHER(6,6,R.string.other);
  

    public final int title;
    public final int value;
    public final int index;

    TitleType(int index, int value, @StringRes  int title){
        this.index = index;
        this.value = value;
        this.title = title;
    }
    public String getLocaleName(Context context){
        return  context.getString(title);
    }

    public static TitleType ofIndex(int idx){
        for(TitleType t: TitleType.values()){
            if(t.index == idx){
                return t;
            }
        }
        return UNKNOWN;
    }
    public static TitleType typeOf(int value){
       for(TitleType t: TitleType.values()){
           if(t.value == value){
               return t;
           }
       }
       return UNKNOWN;
    }

   
}
