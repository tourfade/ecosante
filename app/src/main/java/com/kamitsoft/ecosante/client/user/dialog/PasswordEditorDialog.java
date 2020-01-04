package com.kamitsoft.ecosante.client.user.dialog;

import android.app.Dialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;
import com.kamitsoft.ecosante.services.ApiSyncService;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class PasswordEditorDialog extends DialogFragment {

    private  UserInfo userInfo;
    private  UsersViewModel model;
    private EditText oldPassword, newPassword, confirmPassword;
    private TextInputLayout oldpasswordContainer;
    private TextView errorMessage;
    private EcoSanteApp app;
    private View progress;

    public PasswordEditorDialog(UserInfo userInfo){
       this.userInfo = userInfo;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        app = (EcoSanteApp)getActivity().getApplication();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.update_password);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(R.layout.password_editor);
        alertDialogBuilder.setIcon(R.drawable.lock);
        alertDialogBuilder.setPositiveButton("Modifier",null);

        alertDialogBuilder.setNegativeButton("Fermer", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            initVars((AlertDialog)dialog);
            initValues();
            initListeners((AlertDialog)dialog);
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
        errorMessage = d.findViewById(R.id.errorMessage);
        progress = d.findViewById(R.id.progress);
    }
    void initListeners(AlertDialog dialog) {
        confirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                tryUpdate(dialog);
            }
            return false;
        });

        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(view -> {
           tryUpdate(dialog);

        });


    }

    private void tryUpdate(AlertDialog dialog) {
        errorMessage.setText("");
        newPassword.setError(null);
        confirmPassword.setError(null);
        oldPassword.setError(null);
        String opw = oldPassword.getText().toString();
        String npw = newPassword.getText().toString();
        String cpw = confirmPassword.getText().toString();
        if(npw.length() <= 4){
            oldPassword.setError("Doit contenir plus de 4 caractères");
        }
        if(npw.length() < 4 || cpw.length() < 4){
            newPassword.setError("Doit contenir plus de 4 caractères");
            confirmPassword.setError("Doit contenir plus de 4 caractères");
            return;
        }
        if(!npw.equals(cpw)){
            newPassword.setError("");
            confirmPassword.setError("Les deux mots de passe ne correspondent pas");

            return;
        }
        progress.setVisibility(View.VISIBLE);
        app.service().updatePassword(opw, npw, (ok) -> {
            progress.setVisibility(View.GONE);

            if(ok[0])
                dialog.dismiss();
            else{
                oldPassword.setError("Mot de passe icorrect");
                errorMessage.setText("Mot de passe icorrect");
            }
        });
    }



}
