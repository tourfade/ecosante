package com.kamitsoft.ecosante.constant;


import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.PatientBaseFragment;
import com.kamitsoft.ecosante.client.patient.PatientAppointments;
import com.kamitsoft.ecosante.client.patient.PatientDocuments;
import com.kamitsoft.ecosante.client.patient.PatientEncounters;
import com.kamitsoft.ecosante.client.patient.PatientProfileView;
import com.kamitsoft.ecosante.client.patient.PatientSummaryView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public enum PatientViewsType {
    PROFILE(R.id.action_profile, R.string.profile,  R.drawable.patient, PatientProfileView.class),// for user check
    SUMMARY(R.id.action_summary,R.string.summary, R.drawable.action, PatientSummaryView.class),
    ENCOUNTERS(R.id.action_visits,R.string.encounters, R.drawable.encounters, PatientEncounters.class),
    DOCUMENTS(R.id.action_docs,R.string.docs, R.drawable.docs, PatientDocuments.class),
    APPOINTMENTS(R.id.action_appointments,R.string.diary, R.drawable.appointment, PatientAppointments.class);


    public final int value;
    public final int title;
    public final int icon;
    public final Class<? extends PatientBaseFragment> fragment;

    PatientViewsType(int value, @StringRes int title,  @DrawableRes int icon, Class<?extends PatientBaseFragment> frag){
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