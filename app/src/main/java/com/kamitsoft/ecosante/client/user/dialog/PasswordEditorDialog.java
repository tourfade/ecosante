package com.kamitsoft.ecosante.client.user.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.model.UserInfo;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class PasswordEditorDialog extends DialogFragment {

    private final UserInfo userInfo;
    private  EditText oldPassword, newPassword, confirmPassword;
    private TextInputLayout oldpasswordContainer;

    public PasswordEditorDialog(UserInfo userInfo){
        this.userInfo = userInfo;

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.update_password);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(R.layout.password_editor);
        alertDialogBuilder.setIcon(R.drawable.lock);
        alertDialogBuilder.setPositiveButton("Modifier",(dialog, which)->{

        });
        alertDialogBuilder  .setNegativeButton("Fermer", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            initVars((AlertDialog)dialog);
            initValues();
            initListeners();
        });
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return alertDialog;
    }

    private void initValues() {
        oldpasswordContainer.setHint("Ancien mots de passe de "+Utils.niceFormat(userInfo.getUsername()));

    }

    void initVars(AlertDialog d) {
        oldpasswordContainer = d.findViewById(R.id.oldpasswordContainer);
        oldPassword = d.findViewById(R.id.oldpassword);
        newPassword = d.findViewById(R.id.passwordNew);
        confirmPassword = d.findViewById(R.id.passwordConfirm);


    }
    void initListeners() {





    }



}
