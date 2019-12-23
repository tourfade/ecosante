package com.kamitsoft.ecosante.signing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.EcoSanteActivity;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;
import com.kamitsoft.ecosante.services.ApiSyncService;
import com.kamitsoft.ecosante.services.AuthenticationInfo;
import com.kamitsoft.ecosante.services.FirebaseChannels;
import com.kamitsoft.ecosante.services.Proxy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUp extends AppCompatActivity {

    private EcoSanteApp app;
    private EditText username,password, passwordConfirmation;
    private ProgressBar progressBar;
    private Proxy proxy;
    private UsersViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        app = (EcoSanteApp) getApplication();
        username = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        passwordConfirmation = findViewById(R.id.input_password_confirmation);
        progressBar = findViewById(R.id.progressBar);
        model = ViewModelProviders.of(this).get(UsersViewModel.class);

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            if(!passwordConfirmation.getText().toString().equals(password.getText().toString())){
                Toast.makeText(this,"Les mots de passe de correspondent pas", Toast.LENGTH_LONG).show();
                return;
            }
            v.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            UserAccountInfo accountInfo = new UserAccountInfo();
            accountInfo.setAccountId(-1);
            accountInfo.setPassword(password.getText().toString());
            accountInfo.setUsername(username.getText().toString());
            createAccount(accountInfo, (Boolean... success)->{
                        progressBar.setVisibility(View.GONE);
                        if(success[0]) {
                            Intent login = new Intent(this, SignIn.class);
                            login.putExtra("username", username.getText().toString());
                            startActivity(login);
                            overridePendingTransition(R.anim.slide_up,R.anim.fade_out);
                            finish();
                        }else{
                            v.setEnabled(true);
                        }


                    });
        });

        findViewById(R.id.tv_backtologin).setOnClickListener(v->{
            Intent main = new Intent(this, SignIn.class);
            startActivity(main);
            overridePendingTransition(R.anim.fade_in,R.anim.slide_down);
            finish();
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

    private void createAccount(UserAccountInfo accountInfo, final ApiSyncService.CompletionWithData<Boolean> completion ){
        proxy.createAccount(accountInfo)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.code() != 200){
                            String msg = "";
                            if(response.code() == 208){
                                msg = "Le compte est dèja actif";
                            }
                            if(response.code() == 404){
                                msg = "Ce compte n'existe pas, verifiez auprès de votre administrateur";
                            }
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


}
