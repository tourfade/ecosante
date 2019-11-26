package com.kamitsoft.ecosante.constant;


import android.content.Context;

import androidx.annotation.StringRes;

import com.kamitsoft.ecosante.R;

public enum UserType {
    UNDETERMINATED(0, R.string.underterminated),// for user check
    ADMIN(70,R.string.admin),
    PHYSIST(10,R.string.physist),
	PHARMACYST(20,R.string.pharmacist),
    NURSE(30,R.string.nurse),
    AGENT(40,R.string.agent),
    REPORTER(60,R.string.reporter),
    LAB_TECH(50,R.string.labtech);

    

    public final int type;
    public final int title;
    UserType(int type, @StringRes int title){
        this.type = type;
        this.title = title;
    }

    public static boolean isPhysist(int userType) {
        return userType == 10;
    }

    public String getLocaleName(Context context){
        return context.getString(title);
    }




    public static UserType typeOf(int type){
       for(UserType u: values()){
           if(u.type == type){
               return u;
           }
       }
       return UNDETERMINATED;
    }



}