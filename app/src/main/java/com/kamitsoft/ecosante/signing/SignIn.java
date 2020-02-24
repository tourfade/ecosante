package com.kamitsoft.ecosante.signing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
import com.google.firebase.messaging.FirebaseMessaging;
import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.EcoSanteActivity;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;
import com.kamitsoft.ecosante.services.ApiSyncService;
import com.kamitsoft.ecosante.services.AuthenticationInfo;
import com.kamitsoft.ecosante.services.FirebaseChannels;
import com.kamitsoft.ecosante.services.Proxy;

public class SignIn extends AppCompatActivity {

    private EcoSanteApp app;
    private EditText username,password;
    private ProgressBar progressBar;
    private Proxy proxy;
    private UsersViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        app = (EcoSanteApp) getApplication();
        //account = findViewById(R.id.input_account);
        username = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.progressBar);
        model = ViewModelProviders.of(this).get(UsersViewModel.class);

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            v.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            login("",username.getText().toString(), password.getText().toString(),
                    (Boolean... success)->{
                        progressBar.setVisibility(View.GONE);
                        if(success[0]) {
                            Intent main = new Intent(this, EcoSanteActivity.class);
                            startActivity(main);
                            overridePendingTransition(R.anim.fade_in,R.anim.slide_down);
                            finish();
                        }else{
                            password.requestFocus();
                            v.setEnabled(true);
                        }


                    });
        });

        findViewById(R.id.tv_restorcredential).setOnClickListener(v->{
            Intent restore = new Intent(this, CredentialRestoreActivity.class);
            startActivity(restore);
            overridePendingTransition(R.anim.slide_up,R.anim.fade_out);
        });
        findViewById(R.id.tv_activate_account).setOnClickListener(v->{
            Intent signup = new Intent(this, SignUp.class);
            startActivity(signup);
            overridePendingTransition(R.anim.slide_up,R.anim.fade_out);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra("username")){
            username.setText(intent.getStringExtra("username"));
        }
    }

    private void login(String account, String email, String password, final ApiSyncService.CompletionWithData<Boolean> completion ){
        hideSoftKeyBoard();
        proxy.login(new AuthenticationInfo(account, email, password))
                .enqueue(new Callback<UserAccountInfo>() {
                    @Override
                    public void onResponse(Call<UserAccountInfo> call, Response<UserAccountInfo> response) {

                        if (response.code() == 200){
                            UserAccountInfo ua = response.body();
                            ua.getUserInfo().setAccountID(ua.getAccountId());
                            model.connect(ua, ua.getUserInfo());
                            Utils.subscribe(ua.getUserInfo());

                            if(completion != null){
                                completion.onReady(true);
                            }
                        }else {

                            if(response.code() == 401){
                                Toast.makeText(getApplication(), R.string.wrong_username_or_password, Toast.LENGTH_LONG).show();
                            }
                            if(completion != null){
                                completion.onReady(false);
                            }
                        }

                    }
                    @Override
                    public void onFailure(Call<UserAccountInfo> call, Throwable t) {
                        t.printStackTrace();

                        Toast.makeText(getApplication(), R.string.unknown_error, Toast.LENGTH_LONG).show();
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
