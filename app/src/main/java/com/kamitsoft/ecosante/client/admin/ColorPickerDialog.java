package com.kamitsoft.ecosante.client.admin;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.model.DistrictInfo;
import com.skydoves.colorpickerpreference.ColorPickerView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ColorPickerDialog extends DialogFragment {

    private final DistrictInfo district;
    private View viewcolor;
    private ColorPickerView colorPickerView;


    public ColorPickerDialog(View viewColor, DistrictInfo districtInfo){
        this.viewcolor = viewColor;
        this.district = districtInfo;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.color_selector);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(R.layout.color_picker);
        alertDialogBuilder.setPositiveButton("Ok",(dialog, which)->{
            viewcolor.setBackgroundColor(colorPickerView.getColor());
            district.getArea().fillColor = colorPickerView.getColor();
        });
        alertDialogBuilder  .setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            colorPickerView = ((AlertDialog)dialog).findViewById(R.id.colorPickerView);
        });

        return alertDialog;
    }






}
