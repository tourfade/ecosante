package com.kamitsoft.ecosante;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jackandphantom.blurimage.BlurImage;
import com.kamitsoft.ecosante.client.EcoSanteActivity;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;
import com.kamitsoft.ecosante.services.WorkerService;
import com.kamitsoft.ecosante.signing.SignIn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

/**
 * Created by tourfade on 19-09-02.
 */
public class Splash extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private ImageView logo;
    private UsersViewModel model;
    private UserAccountInfo account;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        model = ViewModelProviders.of(this).get(UsersViewModel.class);
        model.getConnectedAccount().observe(this, acc->{
            this.account = acc;
        });
        setContentView(R.layout.splashscreen);
        ImageView iv = findViewById(R.id.blur_image);
        BlurImage.with(this)
                .load(R.drawable.splash2)
                .intensity(6)
                .Async(true)
                .into(iv);

        logo = findViewById(R.id.app_logo);



        int[] coords = new int[2];
        logo.post(() -> {
            logo.getLocationOnScreen(coords);
            logo.animate()
                    .translationYBy(-1*coords[1])//5-1*logo.getTop())
                    .setDuration(SPLASH_DISPLAY_LENGTH)
                    .start();
        });


        new Handler().postDelayed(() -> {
            if(account == null || Utils.isNullOrEmpty(account.getJwtToken())){
                startActivity(new Intent(Splash.this, SignIn.class));
            }else{
                startActivity(new Intent(Splash.this, EcoSanteActivity.class));
            }
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            Splash.this.finish();
        }, SPLASH_DISPLAY_LENGTH);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent worker = new Intent(this, WorkerService.class);
        startService(worker);
    }
}
