package com.kamitsoft.ecosante.client.patient;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.PatientViewsType;
import com.kamitsoft.ecosante.model.viewmodels.PatientsViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

public class PatientActivity extends ImagePickerActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private EcoSanteApp app;

    private PatientsViewModel model;
    private PatientBaseFragment currentFragment;
    private BottomNavigationView navBar;
    private PatientViewsType currentType;

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