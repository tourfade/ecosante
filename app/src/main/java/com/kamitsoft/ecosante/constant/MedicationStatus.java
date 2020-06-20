package com.kamitsoft.ecosante.constant;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.kamitsoft.ecosante.R;

import androidx.annotation.StringRes;

public enum MedicationStatus {
    NEW(0,0, R.string.new_med), //0
    RUNNING(1,1,R.string.running),//1
    TERMINATED(2,2,R.string.terminated),//2
    UNKNOWN(3,-1,R.string.unknown);//-1
    private static Context appContext;
    public final int status;
    public final int title;
    public final int index;

    MedicationStatus(int index, int status, @StringRes int title){
        this.index = index;
        this.status = status;
        this.title = title;
    }

    public  String title(Context c){
        return c.getString(title);
    }
    public static MedicationStatus ofStatus(int v){
        for (MedicationStatus g: values()) {
            if (g.status == v){
                return g;
            }
        }
        return UNKNOWN;
    }
    public static MedicationStatus atIndex(int index){
        for (MedicationStatus g: values()) {
            if (g.index == index){
                return g;
            }
        }
        return UNKNOWN;
    }

    public static  SpinnerAdapter getAdapter(Context context, int type){
        appContext = context;
        return new ArrayAdapter<MedicationStatus>(context, R.layout.m_spinner_item, values()){

            @Override
            public boolean isEnabled(int position) {
                if(UserType.isNurse(type) && ( MedicationStatus.NEW.index == position))
                    return false;
                return true;
            }
            // Change color item
            @Override
            public View getDropDownView(int position, View convertView,  ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (!isEnabled(position)) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };


    }

    @Override
    public String toString() {
        return title(appContext);
    }
}
