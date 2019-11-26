package com.kamitsoft.ecosante.constant;


import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.patient.PatientAppointments;
import com.kamitsoft.ecosante.client.patient.PatientDocuments;
import com.kamitsoft.ecosante.client.patient.PatientEncounters;
import com.kamitsoft.ecosante.client.patient.PatientProfileView;
import com.kamitsoft.ecosante.client.patient.PatientSummaryView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public enum PatientViewsType {
    PROFILE(0, R.string.profile,  R.drawable.patient, PatientProfileView.class),// for user check
    SUMMARY(1,R.string.summary, R.drawable.action, PatientSummaryView.class),
    ENCOUNTERS(2,R.string.encounters, R.drawable.encounters, PatientEncounters.class),
    DOCUMENTS(3,R.string.docs, R.drawable.docs, PatientDocuments.class),
    APPOINTMENTS(4,R.string.diary, R.drawable.appointment, PatientAppointments.class);


    public final int value;
    public final int title;
    public final int icon;
    public final Class<? extends Fragment> fragment;

    PatientViewsType(int value, @StringRes int title,  @DrawableRes int icon, Class<?extends Fragment> frag){
        this.value = value;
        this.title = title;
        this.icon = icon;
        this.fragment = frag;
    }


    public static PatientViewsType typeOf(int value){
       for(PatientViewsType u: values()){
           if(u.value == value){
               return u;
           }
       }
       return PROFILE;
    }



}