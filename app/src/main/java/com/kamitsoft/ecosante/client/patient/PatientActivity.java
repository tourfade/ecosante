package com.kamitsoft.ecosante.client.patient;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.PatientViewsType;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.viewmodels.PatientsViewModel;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class PatientActivity extends ImagePickerActivity implements TabLayout.BaseOnTabSelectedListener {

    private EcoSanteApp app;
    private TabLayout tabLayout;
    private ViewPager pager;
    private CustomPagerAdapter adapter;
    private PatientsViewModel model;

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


        tabLayout = findViewById(R.id.nav_bottom);
        pager =     findViewById(R.id.view_pager);
        adapter = new CustomPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccent, getTheme()));
        tabLayout.setupWithViewPager(pager);
        for(PatientViewsType pwt: PatientViewsType.values()){
            TabLayout.Tab tab =  tabLayout.getTabAt(pwt.value);
            tab.setIcon(pwt.icon);
            tab.setText(pwt.title);
            if(pwt == PatientViewsType.ENCOUNTERS) {
                tab.setCustomView(R.layout.consultation_item);
            }
        }
        tabLayout.addOnTabSelectedListener(this);
        toolbar.setSubtitle(Utils.formatPatient(this,app.getCurrentPatient()));
        app.getCurrentLivePatient().observe(this, patientInfo -> {
            toolbar.setSubtitle(Utils.formatPatient(PatientActivity.this,app.getCurrentPatient()));

        });

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
    public void onTabSelected(TabLayout.Tab tab) {
        setTitle(tab.getText());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }



    public class CustomPagerAdapter extends FragmentPagerAdapter {


        public CustomPagerAdapter(FragmentManager fm) {
           super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }


        @Override
        public Fragment getItem(int position) {
            try {
                return PatientViewsType.values()[position].fragment.newInstance();
            }catch (Exception e){

            }
            return null;
        }

        @Override
        public int getCount() {
            return PatientViewsType.values().length;
        }


    }

}