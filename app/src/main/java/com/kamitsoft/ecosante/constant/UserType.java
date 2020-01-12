package com.kamitsoft.ecosante.constant;


import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.kamitsoft.ecosante.R;

import java.text.NumberFormat;

public enum UserType {
    UNDETERMINATED(0, R.string.underterminated,R.drawable.user_avatar),// for user check
    ADMIN(70,R.string.admin,R.drawable.admin),
    PHYSIST(10,R.string.physist,R.drawable.phys),
	PHARMACYST(20,R.string.pharmacist,R.drawable.pharmacist),
    NURSE(30,R.string.nurse,R.drawable.nurse),
    AGENT(40,R.string.agent,R.drawable.user_avatar),
    REPORTER(60,R.string.reporter,R.drawable.user_avatar),
    LAB_TECH(50,R.string.labtech,R.drawable.user_avatar);

    

    public final int type;
    public final int title;
    public final int placeholder;

    UserType(int type, @StringRes int title, @DrawableRes int placeholder){
        this.type = type;
        this.title = title;
        this.placeholder = placeholder;
    }

    public static boolean isPhysist(int userType) {
        return userType == PHYSIST.type;
    }

    public static boolean isNurse(int userType) {
        return userType == NURSE.type;
    }

    public static boolean isAdmin(int userType) {
        return userType == ADMIN.type;
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