package com.kamitsoft.ecosante.client.admin;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.user.UserProfile;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class UserProfileEditor extends ImagePickerActivity {
    public static final String KIND = "Kind";
    public static final String NEW_PHYSICIAN = "NewPhysician";
    public static final String EDIT_PHYSICIAN = "EditPhysician";
    public static final String NEW_NURSE = "EditNurse";
    public static final String EDIT_NURSE = "NewNurse";

    private EcoSanteApp app;
    private UserProfile userProfileEditor;
    private UsersViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_editor);
        app = (EcoSanteApp)getApplication();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        userProfileEditor = (UserProfile)getSupportFragmentManager().findFragmentById(R.id.user_editor);
        app = (EcoSanteApp)getApplication();
        model = ViewModelProviders.of(this).get(UsersViewModel.class);

        switch (getIntent().getStringExtra(KIND)){
            case NEW_PHYSICIAN:
                userProfileEditor.edit(true);

                setTitle(R.string.new_physician);
                break;
            case EDIT_PHYSICIAN:
                setTitle(R.string.edit_physician);

                break;
            case NEW_NURSE:

                userProfileEditor.edit(true);

                setTitle(R.string.new_nurse);
                break;
            case EDIT_NURSE:
                setTitle(R.string.edit_nurse);

                break;
        }
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
        switch (getIntent().getStringExtra(KIND)){
            case NEW_PHYSICIAN:
            case NEW_NURSE:
                overridePendingTransition(R.anim.fade_in,R.anim.slide_down);
                break;
            case EDIT_PHYSICIAN:
            case EDIT_NURSE:
                overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                break;
        }


    }
}
