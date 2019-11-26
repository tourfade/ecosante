package com.kamitsoft.ecosante.signing;

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
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.EcoSanteActivity;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class CredentialRestoreActivity extends AppCompatActivity {

    private EcoSanteApp app;
    private EditText account,username,dob,mobile;
    private ProgressBar progressBar;
    private  final Calendar dateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cred_restore);
        app = (EcoSanteApp) getApplication();
        account = findViewById(R.id.input_account);
        username = findViewById(R.id.input_email);
        dob = findViewById(R.id.input_dob);
        mobile = findViewById(R.id.input_mobile);
        progressBar = findViewById(R.id.progressBar);
        Utils.manageDataPicker(this,dob,dateTime);
        findViewById(R.id.btn_restore).setOnClickListener(v -> {
            v.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

    }
}
