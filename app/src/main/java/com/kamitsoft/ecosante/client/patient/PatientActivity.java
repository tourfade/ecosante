package com.kamitsoft.ecosante.client.patient;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.*;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.EcoSanteActivity;
import com.kamitsoft.ecosante.client.nurse.WaitingPatients;
import com.kamitsoft.ecosante.constant.PatientViewsType;
import com.kamitsoft.ecosante.model.viewmodels.PatientsViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class PatientActivity extends ImagePickerActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private EcoSanteApp app;

    private PatientsViewModel model;
    private PatientBaseFragment currentFragment;
    private BottomNavigationView navBar;
    private PatientViewsType currentType;
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_layout);
        app = (EcoSanteApp)getApplication();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        navBar = findViewById(R.id.bottom_navigation);
        navBar.setOnNavigationItemSelectedListener(this);



        toolbar.setSubtitle(Utils.formatPatient(this,app.getCurrentPatient()));
        app.getCurrentLivePatient().observe(this, patientInfo -> {
            toolbar.setSubtitle(Utils.formatPatient(PatientActivity.this,app.getCurrentPatient()));

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        navBar.setSelectedItemId(PatientViewsType.ENCOUNTERS.value);

        /*BottomNavigationMenuView mview = (BottomNavigationMenuView)navBar.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView)mview.getChildAt(2);
        View circ = LayoutInflater.from(this).inflate(R.layout.pat_menu_button, mview, false);
        itemView.addView(circ);*/
    }
  /*  @Override
    public void onResume()
    {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, nfcIntent, null,null);

    }*/

    public void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        setIntent(intent);

        if (currentFragment instanceof PatientProfileView) {
            PatientProfileView patientProfileView = (PatientProfileView) currentFragment;

            String action = intent.getAction();
            patientProfileView.writeTag(intent);
            return;
        }
        //Lire les informations du patient
        //1. Lire le tag
        //


    /*    if(action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED) || action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)){
            String tagContent = "";
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            if(action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
            {
                tagContent = ndefReadTag(tag);
            }

        }*/


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getIntent().getBooleanExtra("isNew", false)){
            overridePendingTransition(R.anim.fade_in,R.anim.slide_down);
        }else {
            overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);

        }

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        PatientViewsType type = PatientViewsType.typeOf(item.getItemId());
        setTitle(type.title);
        item.setChecked(true);
        showFragment(type);
        return false;
    }



    @SuppressLint("ResourceType")
    public  PatientBaseFragment  showFragment(PatientViewsType type) {
        try {

            currentFragment = (PatientBaseFragment)getSupportFragmentManager().findFragmentByTag(type.fragment.getName());
            if(currentFragment == null) {
                currentFragment = type.fragment.newInstance();
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(currentType != null){
                boolean rtl = type.ordinal() > currentType.ordinal() ;
                ft.setCustomAnimations(rtl?R.anim.enter_from_right:R.anim.enter_from_left,
                                       rtl?R.anim.exit_to_left:R.anim.exit_to_right);
            }
            //

            ft.replace(R.id.container, currentFragment, type.fragment.getName());
            //ft.runOnCommit(() -> setTitle(currentFragment.getTitle()));
            ft.commit();
            currentType = type;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  currentFragment;

    }




}