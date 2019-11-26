package com.kamitsoft.ecosante.signing;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.GoogleApiAvailability;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.EcoSanteActivity;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;

public class SignIn extends AppCompatActivity {

    private EcoSanteApp app;
    private EditText account,username,password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        app = (EcoSanteApp) getApplication();
        account = findViewById(R.id.input_account);
        username = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.btn_login).setOnClickListener(v -> {
            v.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            app.service().login(account.getText().toString(),username.getText().toString(), password.getText().toString(),
                    (Boolean... success)->{
                        progressBar.setVisibility(View.GONE);
                        if(success[0]) {
                            Intent main = new Intent(this, EcoSanteActivity.class);
                            startActivity(main);
                            overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                            finish();
                        }else{
                            v.setEnabled(true);
                        }


                    });


        });

        findViewById(R.id.tv_restorcredential).setOnClickListener(v->{
            Intent main = new Intent(this, CredentialRestoreActivity.class);
            startActivity(main);
            overridePendingTransition(R.anim.slide_up,R.anim.fade_out);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

    }
}
