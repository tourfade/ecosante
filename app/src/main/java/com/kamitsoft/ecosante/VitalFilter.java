package com.kamitsoft.ecosante;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;

public class VitalFilter  implements InputFilter {

    private  int minChar, maxChar;
    private double min, max;

    public VitalFilter(double min, double max) {
        this.min = min;
        this.max = max;
        minChar = (int)(Math.log(min)/Math.log(10));
        maxChar = (int)(Math.log(max)/Math.log(10));
    }



    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {

            String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend);
            // Add the new string in
            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
            int input = Integer.parseInt(newVal);


            if(input <= max){
                return null;
            }


        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return "";
    }


}