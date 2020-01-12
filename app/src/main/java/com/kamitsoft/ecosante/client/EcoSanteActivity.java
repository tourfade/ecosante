package com.kamitsoft.ecosante.client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.AnimRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.client.nurse.Supervisors;
import com.kamitsoft.ecosante.client.user.dialog.PasswordEditorDialog;
import com.kamitsoft.ecosante.constant.UserStatusConstant;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.services.FirebaseChannels;
import com.kamitsoft.ecosante.services.Proxy;
import com.kamitsoft.ecosante.signing.SignIn;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.admin.Nurses;
import com.kamitsoft.ecosante.client.admin.Physicians;
import com.kamitsoft.ecosante.client.nurse.UserEncounters;
import com.kamitsoft.ecosante.client.physician.MonitoredEncounters;
import com.kamitsoft.ecosante.client.physician.MonitoredPatients;
import com.kamitsoft.ecosante.client.user.UserAppointments;
import com.kamitsoft.ecosante.client.user.Home;
import com.kamitsoft.ecosante.client.user.UserProfile;
import com.kamitsoft.ecosante.client.nurse.WaitingPatients;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class EcoSanteActivity extends ImagePickerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private BaseFragment currentFragment;
    private EcoSanteApp app;
    private View header;
    private UsersViewModel model;
    private UserInfo currentUser;
    private UserAccountInfo account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmi);
        app = (EcoSanteApp) getApplication();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        model = ViewModelProviders.of(this).get(UsersViewModel.class);
        model.getConnectedUser().observe(this, userInfo -> {
            app.setCurrentUser(userInfo);
            if(userInfo == null){
                return;
            }
            this.currentUser = userInfo;
            initDrawerMenu();
            initHeaderInfo();
            if(account!=null && account.getGenPassword() && currentUser !=null){
                new PasswordEditorDialog(currentUser)
                        .show(getSupportFragmentManager(), "PasswordEditorDialog");
            }
        });

        model.getConnectedAccount().observe(this, account->{
            this.account = account;
            if(account!=null && account.getGenPassword() && currentUser !=null){
                new PasswordEditorDialog(currentUser)
                        .show(getSupportFragmentManager(), "PasswordEditorDialog");
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        showFragment(Home.class, R.anim.fade_in, R.anim.fade_out);


    }



    private void initDrawerMenu() {
        assert(currentUser != null);
        navigationView.getMenu().setGroupVisible(R.id.nurse_menu, false);
        navigationView.getMenu().setGroupVisible(R.id.user_menu, false);
        navigationView.getMenu().setGroupVisible(R.id.physician_menu,false);
        navigationView.getMenu().setGroupVisible(R.id.adminmenu, false);
        switch (UserType.typeOf(currentUser.getUserType())){
            case PHYSIST:
                navigationView.getMenu().setGroupVisible(R.id.user_menu, true);
                navigationView.getMenu().setGroupVisible(R.id.physician_menu, true);
                break;
            case NURSE:
                navigationView.getMenu().setGroupVisible(R.id.user_menu, true);
                navigationView.getMenu().setGroupVisible(R.id.nurse_menu, true);
                break;
            case ADMIN:
               navigationView.getMenu().setGroupVisible(R.id.adminmenu, true);
                break;
        }
        Switch switchView = navigationView.getMenu()
                .findItem(R.id.nav_available)
                .getActionView()
                .findViewById(R.id.availability);
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                Utils.subscribe(currentUser);

                subscribe();
            }else{
                Utils.unSubscribe(currentUser);
            }
            int status =  isChecked? UserStatusConstant.AVAILABLE.status:UserStatusConstant.UNAVAILABLE.status;
            model.status(currentUser.getUuid(), isChecked? UserStatusConstant.AVAILABLE.status:UserStatusConstant.UNAVAILABLE.status);
            app.syncStatus(currentUser.getUuid(),status);

        });

    }

    public void initHeaderInfo() {
        assert(app.getCurrentUser() != null);

        TextView fullName = header.findViewById(R.id.textViewPhysistFullName);
        fullName.setText(Utils.formatUser(this, app.getCurrentUser()));
        TextView speciality = header.findViewById(R.id.textViewPhysistSpeciality);
        speciality.setText(Utils.niceFormat(app.getCurrentUser().getSpeciality()));
        ImageView headerIv = header.findViewById(R.id.imageViewHeader);

        if(headerIv !=null) {
            //R.drawable.user_avatar,R.drawable.physist
            UserInfo user = app.getCurrentUser();
            UserType type = UserType.typeOf(user.getUserType());
            Utils.load(this,BuildConfig.AVATAR_BUCKET,
                     user.getAvatar(),
                     headerIv,type.placeholder,type.placeholder );
        }

        header.setOnClickListener(v->{
            app.setEditingUser(app.getCurrentUser());
            showFragment(UserProfile.class, R.anim.fade_in, R.anim.fade_out);
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
        app.exitPatient();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }else{
            app.setEditingUser(null);
            drawer.openDrawer(GravityCompat.START);
        }

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_home:
                showFragment(Home.class, R.anim.fade_in, R.anim.fade_out);
                break;
            //nurse
            case R.id.nav_encountered:
                showFragment(UserEncounters.class, R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_waiting_list:
                showFragment(WaitingPatients.class, R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_supervisors:
                showFragment(Supervisors.class, R.anim.fade_in, R.anim.fade_out);
                break;

            //physiscian
            case R.id.supervisednurses:
                showFragment(Nurses.class,R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.encounter_to_reviews:
                showFragment(MonitoredEncounters.class,R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.patients:
                showFragment(MonitoredPatients.class,R.anim.fade_in, R.anim.fade_out);
                break;
            //======
                //admin
            case R.id.physicians:
                 showFragment(Physicians.class,R.anim.fade_in, R.anim.fade_out);
                 break;


            case R.id.nurses:
                showFragment(Nurses.class,R.anim.fade_in, R.anim.fade_out);
                break;
                //====== User
            case R.id.nav_appointments_list:
                showFragment(UserAppointments.class, R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_profile:
                app.setEditingUser(app.getCurrentUser());
                showFragment(UserProfile.class, R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_disconnect:
                confirmDisconnecting();
                break;

            case R.id.nav_available:

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void confirmDisconnecting() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.confirm_logout);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle(R.string.logout);
        alertDialogBuilder.setIcon(R.drawable.logout);
        alertDialogBuilder.setPositiveButton("Deconnecter",(dialog, which)->{

            model.disconnectUser();

            Intent i = new Intent(this, SignIn.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_up,R.anim.fade_out);
            finish();

        });
        alertDialogBuilder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        alertDialogBuilder.create().show();



    }


    @SuppressLint("ResourceType")
    public  BaseFragment  showFragment(Class<? extends BaseFragment> fragmentClass, @AnimRes int inAnim, @AnimRes int outAnim) {
        try {
            currentFragment = (BaseFragment)getSupportFragmentManager().findFragmentByTag(fragmentClass.getName());
            if(currentFragment == null) {
                currentFragment = fragmentClass.newInstance();
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(inAnim > 0 && outAnim > 0)
                ft.setCustomAnimations(inAnim, outAnim);
            if(currentFragment.getNavLevel() > 0){
                ft.addToBackStack(fragmentClass.getName());
            }
            ft.replace(R.id.container, currentFragment, fragmentClass.getName());
            //ft.runOnCommit(() -> setTitle(currentFragment.getTitle()));
            ft.commit();


        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  currentFragment;

    }

    public void subscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseChannels.ACCOUNT+currentUser.getAccountID());

        switch (UserType.typeOf(currentUser.getUserType())){
            case PHYSIST:
                break;
            case NURSE:
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseChannels.NURSES_OF+currentUser.getSupervisor().physicianUuid);
                break;
            case ADMIN:
                break;

        }

    }


}
