package com.kamitsoft.ecosante.client.admin;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.TextWatchAdapter;
import com.kamitsoft.ecosante.client.patient.oracles.PhysistOracleAdapter;
import com.kamitsoft.ecosante.client.patient.oracles.SpecialityOracleAdapter;
import com.kamitsoft.ecosante.constant.Gender;
import com.kamitsoft.ecosante.constant.TitleType;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.PhysicianInfo;
import com.kamitsoft.ecosante.model.Speciality;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.json.Supervisor;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;

import java.sql.Timestamp;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.ViewModelProviders;

public class UserEditor extends BaseFragment {

    private ImageView userPicture;
    private EditText firstname, lastname,dob,
    pob, mobile,email;
    private TextView specialityOrSupervisorText;
    private AutoCompleteTextView specialityOrSupervisor;
    private AppCompatSpinner sex, title;
    private UserInfo currentEditing;
    private ImagePickerActivity picker;
    private SpecialityOracleAdapter specialityOracle;
    private PhysistOracleAdapter physistOracle;
    private UsersViewModel model;
    private View specSupContainner;
    private UserInfo connectedUser;
    private Button save;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ImagePickerActivity)
            picker  = (ImagePickerActivity)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        model = ViewModelProviders.of(this).get(UsersViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_editor, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        save = view.findViewById(R.id.save);
        userPicture = view.findViewById(R.id.userPicture);
        title = view.findViewById(R.id.title);
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        dob = view.findViewById(R.id.dob);
        pob = view.findViewById(R.id.pob);
        specialityOrSupervisorText = view.findViewById(R.id.specialityOrSupervisorText);
        specialityOrSupervisor = view.findViewById(R.id.specialityOrSupervisor);
        specSupContainner = view.findViewById(R.id.specSupContainner);
        mobile = view.findViewById(R.id.mobile);
        email = view.findViewById(R.id.email);
        sex = view.findViewById(R.id.sex);
        currentEditing = app.getEditingUser();
        connectedUser = app.getCurrentUser();
        initListeners();
        initValues();

        switch (UserType.typeOf(currentEditing.getUserType())){
            case PHYSIST:
                if(currentEditing.getAccountID() == 0) {
                    edit(true);
                    getActivity().setTitle(R.string.new_physician);
                }else{
                    edit(false);
                    getActivity().setTitle(R.string.edit_physician);
                }

                break;

            case NURSE:
                if(currentEditing.getAccountID() == 0) {
                    edit(true);
                    getActivity().setTitle(R.string.new_nurse);
                }else {
                    edit(false);
                    getActivity().setTitle(R.string.edit_nurse);
                }
                break;

        }

    }



    @Override
    public String getTitle() {
        return getString(R.string.profile);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.patient, menu);
        menu.findItem(R.id.action_edit).setVisible(!edit);
        menu.findItem(R.id.action_save).setVisible(edit);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    protected int getNavLevel() {
        return 1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                edit(!super.edit);
                break;

            case R.id.action_save:
                model.insert(currentEditing);
                //model.setSupervisor(pnp);
                edit(false);
                if(contextActivity != null){
                    contextActivity.onBackPressed();
                }

                break;

        }
        getActivity().invalidateOptionsMenu();

        return true;

    }

    public void edit(boolean editable){
        super.edit = editable;

        userPicture.setEnabled(edit);
        title.setEnabled(edit);
        firstname.setEnabled(edit);
        lastname.setEnabled(edit);
        dob.setEnabled(edit);
        pob.setEnabled(edit);
        specialityOrSupervisor.setEnabled(edit);
        mobile.setEnabled(edit);
        email.setEnabled(currentEditing.getAccountID() <= 0);
        sex.setEnabled(edit);
    }
    private void initListeners() {
        if(picker != null) {
            picker.setPlaceholder(R.drawable.physist);
            picker.setSelectionFinishedListener((avatar)-> currentEditing.setAvatar(avatar));
            userPicture.setOnClickListener(picker::pick);
        }

        save.setOnClickListener(v->{
            model.insert(currentEditing);
            //model.setSupervisor(pnp);
            edit(false);
            if(contextActivity != null){
                contextActivity.onBackPressed();
            }
        });
        title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentEditing.setTitle(TitleType.ofIndex(position).value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentEditing.setTitle(TitleType.UNKNOWN.value);
            }
        });

        firstname.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    firstname.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    currentEditing.setFirstName(s.toString());
                }
            }
        });
        lastname.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    lastname.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    currentEditing.setLastName(s.toString().toUpperCase());
                }
            }
        });
        pob.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    pob.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    currentEditing.setPob(s.toString());
                }
            }
        });
        dob.setOnClickListener(v->{
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dob.setText(Utils.niceFormat(Utils.format(calendar.getTime())));
                currentEditing.setDob(new Timestamp(calendar.getTimeInMillis()));
            },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH) );
            datePickerDialog.show();
        });

        specialityOrSupervisor.setOnItemClickListener((parent, view, position, id) -> {
            if(UserType.isPhysist(currentEditing.getUserType())) {
                Speciality s = specialityOracle.getItem(position);
                specialityOrSupervisor.setText(s.getName());
                currentEditing.setSpeciality(s.toString());
                currentEditing.setSpecialityCode(s.getFieldvalue());
            }
            if(UserType.isNurse(currentEditing.getUserType())) {
                PhysicianInfo s = physistOracle.getItem(position);
                String sFullName = Utils.formatUser(getActivity(), s);
                specialityOrSupervisor.setText(sFullName);
                Supervisor sup = new Supervisor();
                sup.nurseUuid = currentEditing.getUuid();
                sup.accountId = connectedUser.getAccountID();
                sup.active = true;
                sup.physicianUuid = s.uuid;
                sup.supFullName = sFullName;
                currentEditing.setSupervisor(sup);
            }
        });




        mobile.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    mobile.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    currentEditing.setMobilePhone(s.toString());
                }
            }
        });

        email.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                {
                    if(s==null || s.toString().trim().length()<= 0){
                        email.setError(getString(R.string.shouldNotbeBlank));
                    }else {
                        currentEditing.setEmail(s.toString());
                        currentEditing.setUsername(s.toString());
                    }
                }
            }
        });


        sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               currentEditing.setSex(Gender.values()[position].sex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentEditing.setSex(Gender.UNKNOWN.sex);
            }
        });



    }
    private void initValues(){
        edit(false);
        if(currentEditing == null){return;}
        specSupContainner.setVisibility(View.GONE);
        if(UserType.isPhysist(currentEditing.getUserType())){
            specSupContainner.setVisibility(View.VISIBLE);
            specialityOracle = new SpecialityOracleAdapter(getActivity());
            specialityOrSupervisor.setAdapter(specialityOracle);
            specialityOrSupervisorText.setText(R.string.speciality);
        }
        if( UserType.isNurse(currentEditing.getUserType())){
            specialityOrSupervisorText.setText(R.string.supervisor);
            specSupContainner.setVisibility(View.VISIBLE);
            if(UserType.isAdmin(connectedUser.getUserType()))
                physistOracle = new PhysistOracleAdapter(getActivity());
                specialityOrSupervisor.setAdapter(physistOracle);

            if(UserType.isPhysist(connectedUser.getUserType())){
                if(currentEditing.getSupervisor() == null){
                    currentEditing.setSupervisor(new Supervisor());
                }
                currentEditing.getSupervisor().accountId = connectedUser.getAccountID();
                currentEditing.getSupervisor().active = true;
                currentEditing.getSupervisor().nurseUuid = currentEditing.getUuid();
                currentEditing.getSupervisor().physicianUuid = connectedUser.getUuid();
                currentEditing.getSupervisor().supFullName = Utils.formatUser(contextActivity, connectedUser);
            }

            specialityOrSupervisor.setText(currentEditing.getSupervisor() !=null? currentEditing.getSupervisor().supFullName:"");
            specialityOrSupervisor.setClickable(UserType.isAdmin(connectedUser.getUserType()));

        }


        Utils.load(getActivity(), currentEditing.getAvatar(), userPicture,R.drawable.user_avatar, currentEditing.getUserType() == UserType.PHYSIST.type ? R.drawable.physist: R.drawable.nurse);

        title.setSelection(TitleType.typeOf(currentEditing.getTitle()).index);
        firstname.setText(Utils.niceFormat(currentEditing.getFirstName()));
        lastname.setText(Utils.niceFormat(currentEditing.getLastName()));
        dob.setText(Utils.format(currentEditing.getDob()));
        pob.setText(Utils.niceFormat(currentEditing.getPob()));
        if(UserType.isPhysist(currentEditing.getUserType())) {
            specialityOrSupervisor.setText(Utils.niceFormat(currentEditing.getSpeciality()));
        }

        mobile.setText(Utils.niceFormat(currentEditing.getMobilePhone()));

        email.setText(Utils.niceFormat(currentEditing.getUsername()));


        sex.setSelection(Gender.indexOf(currentEditing.getSex()), true);
    }





}
