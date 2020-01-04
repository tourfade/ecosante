package com.kamitsoft.ecosante.signing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.EcoSanteActivity;
import com.kamitsoft.ecosante.model.RestoreInfo;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.services.ApiSyncService;
import com.kamitsoft.ecosante.services.Proxy;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CredentialRestoreActivity extends AppCompatActivity {

    private EcoSanteApp app;
    private EditText account,username,dob,mobile;
    private ProgressBar progressBar;
    private  final Calendar dateTime = Calendar.getInstance();
    private Proxy proxy;

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
            RestoreInfo info =  new RestoreInfo();
            info.account = account.getText().toString();

            info.dob = new int[]{dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)};
            info.email = username.getText().toString();
            info.mobile = mobile.getText().toString();

            restore(info, (ok)->{
                progressBar.setVisibility(View.GONE);

                if(ok[0]){
                    String msg = "Un courriel a été envoyé à "+info.email;
                    Toast.makeText(getApplication(),msg, Toast.LENGTH_LONG).show();
                    v.postDelayed( () -> {
                        Intent login = new Intent(this, SignIn.class);
                        login.putExtra("username", info.email);
                        startActivity(login);
                        overridePendingTransition(R.anim.fade_in,R.anim.slide_down);
                        finish();
                    }, 3000);

                }else{
                    v.setEnabled(true);
                    mobile.requestFocus();
                }

            });

        });

        proxy =  new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(Utils.getGsonBuilder()))
                .baseUrl(BuildConfig.SERVER_URL)
                .build().create(Proxy.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

    }

    private void restore(RestoreInfo info, final ApiSyncService.CompletionWithData<Boolean> completion ){
        hideSoftKeyBoard();
        proxy.restoreAccountPassword(info)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.code() != 200){

                            String msg = "Les informations saisies ne correspondent pas Veuillez reéssayer";

                            Toast.makeText(getApplication(),msg, Toast.LENGTH_LONG).show();
                        }

                        if(completion != null){
                            completion.onReady(response.code() == 200);
                        }

                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        t.printStackTrace();

                        Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_LONG).show();
                        if(completion != null){
                            completion.onReady(false);
                        }


                    }
                });


    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
