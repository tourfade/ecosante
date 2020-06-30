package com.kamitsoft.ecosante.client;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.*;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;
import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.Splash;
import com.kamitsoft.ecosante.client.admin.ClusterView;
import com.kamitsoft.ecosante.client.admin.DistrictMap;
import com.kamitsoft.ecosante.client.nurse.NurseDistrictMap;
import com.kamitsoft.ecosante.client.nurse.Supervisors;
import com.kamitsoft.ecosante.client.patient.Encounter;
import com.kamitsoft.ecosante.client.user.dialog.PasswordEditorDialog;
import com.kamitsoft.ecosante.constant.NavMenuConstant;
import com.kamitsoft.ecosante.constant.UserStatusConstant;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.nfc.NfcMethod;
import com.kamitsoft.ecosante.signing.SignIn;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.admin.Nurses;
import com.kamitsoft.ecosante.client.admin.Physicians;
import com.kamitsoft.ecosante.client.nurse.MonitoredEncounters;
import com.kamitsoft.ecosante.client.physician.SupervisedEncounters;
import com.kamitsoft.ecosante.client.physician.SupervisedPatients;
import com.kamitsoft.ecosante.client.user.UserAppointments;
import com.kamitsoft.ecosante.client.user.Home;
import com.kamitsoft.ecosante.client.user.UserProfile;
import com.kamitsoft.ecosante.client.nurse.MonitoredPatients;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.view.View;
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
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcIntent;
    private NfcMethod nfcMethod=new NfcMethod();
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        model.getLiveConnectedAccount().observe(this, account -> {
            this.account = account;
            if (account != null && account.getGenPassword() && currentUser != null) {
                new PasswordEditorDialog(currentUser)
                        .show(getSupportFragmentManager(), "PasswordEditorDialog");
            }
        });

        model.getLiveConnectedUser().observeForever(cu -> {
            currentUser = cu;

            if (currentUser != null) {
                initDrawerMenu();
                initHeaderInfo();
                initDrawerCount();
                if (account != null && account.getGenPassword() && currentUser != null) {
                    new PasswordEditorDialog(currentUser)
                            .show(getSupportFragmentManager(), "PasswordEditorDialog");
                }
            } else {
                disconnect();
            }
        });


        navigationView.setCheckedItem(NavMenuConstant.NAV_USER_HOME.id);
        showFragment(Home.class, R.anim.fade_in, R.anim.fade_out);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //Vérifie si l'appareil suporte la technologie NFC
        if (nfcAdapter == null) {// Si NFC n'est pas supporté
             Toast.makeText(this,"Device does not support NFC!",Toast.LENGTH_LONG).show();
        } else {
            if (!nfcAdapter.isEnabled()) { // Si NFC n'est pas activé

                Toast.makeText(this, "Enable NFC!",Toast.LENGTH_LONG).show();

            } else {//Si tout va bien
                nfcIntent = PendingIntent.getActivity(this,
                        0, new Intent(this, getClass())
                                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            }

        }
        handelIntent(getIntent());
    }

    private void initDrawerCount() {
       NavMenuConstant
        .filter(n -> n.isAuthorized(currentUser.getUserType()) && n.isList)
        .forEach(n->{
            MenuItem v = navigationView.getMenu().findItem(n.id);
             if(v.getActionView() != null){
                TextView tv = v.getActionView().findViewById(R.id.textMenuItemCount);
                model.getCount(n.id).observe(this,items ->{
                    long count = 0;
                    switch (NavMenuConstant.ofMenu(n.id)){
                        case NAV_USER_VISITS:
                            count = items.stream().filter(p-> p.getMonitor()!=null && currentUser.getUuid().equals(p.getMonitor().monitorUuid)).count();
                            break;
                        case NAV_USER_PATIENTS:
                            count = items.stream().filter(p-> p.getMonitor()!=null && currentUser.getUuid().equals(p.getMonitor().monitorUuid)).count();

                            break;
                        case NAV_ADMIN_DISTRICTS:
                            count = items.stream()
                                    .filter(p-> currentUser.getAccountId() == p.getAccountId())
                                    .count();

                            break;
                        case NAV_ADMIN_PHYSICIANS:
                            count = items.stream()
                                    .filter(p-> UserType.isPhysist(p.getUserType()) && currentUser.getAccountId() == p.getAccountId())
                                    .count();
                            break;
                        case NAV_ADMIN_NURSES:
                            count = items.stream()
                                    .filter(p-> UserType.isNurse(p.getUserType()) && currentUser.getAccountId() == p.getAccountId())
                                    .count();
                            break;
                        case NAV_NURSE_SUPERVISORS:
                            count = items.stream()
                                    .filter(p-> UserType.isPhysist(p.getUserType()) && currentUser.getDistrictUuid().equals(p.getDistrictUuid()))
                                    .count();

                            break;
                        case NAV_SUPERVISED_NURSES:
                            count = items.stream().filter(p-> UserType.isNurse(p.getUserType())
                                    && currentUser.getDistrictUuid().equals(p.getDistrictUuid())).count();

                            break;
                        case NAV_SUPERVISED_VISITS:
                            count = items.stream().filter(p-> currentUser.getDistrictUuid().equals(p.getDistrictUuid())).count();

                            break;
                        case NAV_USER_APPOINTMENTS:
                            count = items.stream().filter(p-> currentUser.getUuid().equals(p.getUuid())).count();

                            break;
                        case NAV_SUPERVISED_PATIENTS:
                            count = items.stream().filter(p-> currentUser.getDistrictUuid().equals(p.getDistrictUuid())).count();

                            break;

                    }

                    tv.setText(String.valueOf(count));
                });

            }
        });


    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handelIntent(intent);
    }

    private void handelIntent(Intent intent) {
        String action = intent.getAction();

        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ){
            String uuid = nfcMethod.readFromIntent(intent);
            app.postService(service ->
                service.getPatient(uuid, data -> {
                    if(data == null){
                        popMessage();
                    }else if(data[0].getUuid() == null){
                        startActivity(new Intent(this, SignIn.class));
                        finish();
                    }else {
                        app.setCurrentPatient(data[0]);
                        app.setNewEncounter();
                        Intent i = new Intent(this, Encounter.class);
                        i.putExtra("isNew", true);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
                    }
                }));

        }
    }

    private void popMessage() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.nfc)
                .setTitle(R.string.patient_not_found)//Titre de la boite de dialogue
                .setMessage("Impossible de trouver le patient associé. Resssayez plus tard !")//Message affiché
                .setNegativeButton(getString(R.string.ok), (dialogInterface, i) -> {})
                .create().show();
    }

    private void initDrawerMenu() {
        navigationView.getMenu().setGroupVisible(R.id.nurse_menu, false);
        navigationView.getMenu().setGroupVisible(R.id.user_menu, false);
        navigationView.getMenu().setGroupVisible(R.id.physician_menu,false);
        navigationView.getMenu().setGroupVisible(R.id.adminmenu, false);
        if(currentUser == null){return;}

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
            }else{
                Utils.unSubscribe(currentUser);
            }
            int status =  isChecked? UserStatusConstant.AVAILABLE.status:UserStatusConstant.UNAVAILABLE.status;
            model.status(currentUser.getUuid(), isChecked? UserStatusConstant.AVAILABLE.status:UserStatusConstant.UNAVAILABLE.status);
            app.syncStatus(currentUser.getUuid(),status);

        });

    }

    public void initHeaderInfo() {
        assert(currentUser != null);

        TextView fullName = header.findViewById(R.id.textViewPhysistFullName);
        fullName.setText(Utils.formatUser(this, currentUser));
        TextView speciality = header.findViewById(R.id.textViewPhysistSpeciality);
        TextView district = header.findViewById(R.id.tvDistrictName);
        TextView cluster = header.findViewById(R.id.tvClusterName);

        if(UserType.isPhysist(currentUser.getUserType())) {
            speciality.setText(Utils.niceFormat(currentUser.getSpeciality()));
        }
        if(UserType.isNurse(currentUser.getUserType())) {
            speciality.setText(R.string.nurse);
        }
        if(UserType.isAdmin(currentUser.getUserType())) {
            speciality.setText(R.string.admin);
            district.setText("");
        }else {
            district.setText(Utils.niceFormat(currentUser.getDistrictName()));
        }

        cluster.setText(Utils.niceFormat(app.getClusterName()));

        ImageView headerIv = header.findViewById(R.id.imageViewHeader);

        if(headerIv !=null) {
            //R.drawable.user_avatar,R.drawable.physist
            UserType type = UserType.typeOf(currentUser.getUserType());
            Utils.load(getApplicationContext(),BuildConfig.AVATAR_BUCKET,
                     currentUser.getAvatar(),
                     headerIv,type.placeholder,type.placeholder );
        }

        header.setOnClickListener(v->{
            app.setEditingUser(currentUser);
            showFragment(UserProfile.class, R.anim.fade_in, R.anim.fade_out);
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        app.exitPatient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, nfcIntent, null, null);
        }
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

    }

    @Override
    protected void onPause(){
        super.onPause();
        if(nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
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
        item.setChecked(true);
        switch (id){
            case R.id.nav_home:
                showFragment(Home.class, R.anim.fade_in, R.anim.fade_out);
                break;
            //nurse
            case R.id.nav_nurs_distric:
                showFragment(NurseDistrictMap.class, R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_user_visits:
                showFragment(MonitoredEncounters.class, R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_user_patients:
                showFragment(MonitoredPatients.class, R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_supervisors:
                showFragment(Supervisors.class, R.anim.fade_in, R.anim.fade_out);
                break;

            //physician
            case R.id.nav_supervised_nurses:
                showFragment(Nurses.class,R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_supervised_visits:
                showFragment(SupervisedEncounters.class,R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.nav_supervised_patients:
                showFragment(SupervisedPatients.class,R.anim.fade_in, R.anim.fade_out);
                break;
            //======
                //admin
            case R.id.nav_my_districs:
                showFragment(DistrictMap.class,R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.nav_admin_physicians:
                 showFragment(Physicians.class,R.anim.fade_in, R.anim.fade_out);
                 break;

            case R.id.nav_cluster:
                showFragment(ClusterView.class,R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.nav_admin_nurses:
                showFragment(Nurses.class,R.anim.fade_in, R.anim.fade_out);
                break;
                //====== User
            case R.id.nav_user_appointments:
                showFragment(UserAppointments.class, R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_profile:
                app.setEditingUser(currentUser);
                showFragment(UserProfile.class, R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_disconnect:
                confirmDisconnecting();
                break;

            case R.id.nav_available:
                return true;


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

            disconnect();

        });
        alertDialogBuilder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        alertDialogBuilder.create().show();
    }

    @SuppressLint("ResourceType")
    public  BaseFragment  showFragment(Class<? extends BaseFragment> fragmentClass,  int inAnim,  int outAnim) {
      return   showFragment(fragmentClass, inAnim,outAnim, 0, 0);
    }

        @SuppressLint("ResourceType")
    public  BaseFragment  showFragment(Class<? extends BaseFragment> fragmentClass,  int inAnim,  int outAnim,  int popInAnim,  int popOutAnim) {
        try {
            currentFragment = (BaseFragment)getSupportFragmentManager().findFragmentByTag(fragmentClass.getName());
            if(currentFragment == null) {
                currentFragment = fragmentClass.newInstance();
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(inAnim > 0 && outAnim > 0)
                ft.setCustomAnimations(inAnim, outAnim);
            if(currentFragment.getNavLevel() > 0){
                if(inAnim > 0 && outAnim > 0 && popInAnim > 0 && popOutAnim > 0)
                    ft.setCustomAnimations(inAnim, outAnim, popInAnim, popOutAnim);
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


    public  void disconnect(){
        model.disconnectUser();

        Intent i = new Intent(this, SignIn.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_up,R.anim.fade_out);
        finish();
    }


}
