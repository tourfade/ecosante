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

import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.TextWatchAdapter;
import com.kamitsoft.ecosante.client.patient.oracles.DistrictOracleAdapter;
import com.kamitsoft.ecosante.client.patient.oracles.SpecialityOracleAdapter;
import com.kamitsoft.ecosante.constant.Gender;
import com.kamitsoft.ecosante.constant.TitleType;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.model.DistrictInfo;
import com.kamitsoft.ecosante.model.Speciality;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.repositories.UsersRepository;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

public class UserEditor extends BaseFragment {

    private ImageView userPicture;
    private EditText firstname, lastname,dob, pob, mobile,email;
    private AutoCompleteTextView specialitySelector, districtSelector;
    private AppCompatSpinner sex, title;
    private UserInfo currentEditing;
    private ImagePickerActivity picker;
    private SpecialityOracleAdapter specialityOracle;
    private DistrictOracleAdapter districtOracle;
    private View specialityContainner;
    private Button save;
    private String oldavatar;


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
        currentEditing = app.getEditingUser();
        save = view.findViewById(R.id.save);
        userPicture = view.findViewById(R.id.userPicture);
        title = view.findViewById(R.id.title);
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        dob = view.findViewById(R.id.dob);
        pob = view.findViewById(R.id.pob);
        specialitySelector = view.findViewById(R.id.specialitySelector);
        specialityContainner = view.findViewById(R.id.specialityContainner);
        districtSelector = view.findViewById(R.id.districtSelector);
        mobile = view.findViewById(R.id.mobile);
        email = view.findViewById(R.id.email);
        sex = view.findViewById(R.id.sex);
        districtOracle = new DistrictOracleAdapter(getContext());
        districtSelector.setAdapter(districtOracle);

        oldavatar = currentEditing.getAvatar();
        if(connectedUser == null){
            connectedUser  = new UsersRepository(app).getConnected();
            if(connectedUser == null)
                contextActivity.disconnect();
        }
        initValues();
        initListeners();
        switch (UserType.typeOf(currentEditing.getUserType())){
            case PHYSIST:
                if(currentEditing.getAccountId() == 0) {
                    edit(true);
                    getActivity().setTitle(R.string.new_physician);
                }else{
                    edit(false);
                    getActivity().setTitle(R.string.edit_physician);
                }

                break;

            case NURSE:
                if(currentEditing.getAccountId() == 0) {
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
    public void onResume() {
        super.onResume();
        if(currentEditing != null) {
            contextActivity.setTitle(Utils.formatName(contextActivity, currentEditing.getFirstName(), currentEditing.getLastName(), currentEditing.getTitle()));
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
                currentEditing.setAccountId(connectedUser.getAccountId());
                model.insert(currentEditing);
                picker.syncAvatar(currentEditing.getAvatar(), oldavatar, 0);

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
        specialitySelector.setEnabled(edit);
        districtSelector.setEnabled(edit);
        mobile.setEnabled(edit);
        email.setEnabled(currentEditing.getAccountId() <= 0);
        sex.setEnabled(edit);
    }

    private void initListeners() {
        if(picker != null) {
            picker.setPlaceholder(R.drawable.physist);
            picker.setSelectionFinishedListener((avatar)-> currentEditing.setAvatar(avatar));
            userPicture.setOnClickListener(picker::pick);
        }
        save.setOnClickListener(v->{
            currentEditing.setAccountId(connectedUser.getAccountId());
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
            int[] d = currentEditing.getDob();
            d = d!=null && d.length == 3? d:Utils.toArray(Calendar.getInstance());
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                currentEditing.setDob(new int[]{year, month, dayOfMonth});
                dob.setText(Utils.format(currentEditing.getDob()));
            },d[0],d[1],d[2]);
            datePickerDialog.show();
        });

        specialitySelector.setOnItemClickListener((parent, view, position, id) -> {
            Speciality s = specialityOracle.getItem(position);
            specialitySelector.setText(s.getName());
            currentEditing.setSpeciality(s.toString());
            currentEditing.setSpecialityCode(s.getFieldvalue());
        });

        districtSelector.setOnItemClickListener((p,v, pos,id)->{
            DistrictInfo d = districtOracle.getItem(pos);
            districtSelector.setText(d.getName());
            districtSelector.setTextColor(d.getArea().fillColor);
            currentEditing.setDistrictUuid(d.getUuid());
            currentEditing.setDistrictName(Utils.niceFormat(d.getName()));
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
        districtSelector.setText("");
        UserType type = UserType.typeOf(currentEditing.getUserType());
        contextActivity.setTitle(Utils.formatName(contextActivity,currentEditing.getFirstName(), currentEditing.getLastName(),currentEditing.getTitle()));
        specialityContainner.setVisibility(UserType.isNurse(currentEditing.getUserType())?View.GONE:View.VISIBLE);

        if(UserType.isPhysist(type.type)){
            specialityOracle = new SpecialityOracleAdapter(getActivity());
            specialitySelector.setAdapter(specialityOracle);
            specialitySelector.setText(currentEditing.getSpeciality());
        }

        Utils.load(getActivity(),
                    BuildConfig.AVATAR_BUCKET,
                    currentEditing.getAvatar(),
                    userPicture,
                    type.placeholder,
                    type.placeholder);

        title.setSelection(TitleType.typeOf(currentEditing.getTitle()).index);
        firstname.setText(Utils.niceFormat(currentEditing.getFirstName()));
        lastname.setText(Utils.niceFormat(currentEditing.getLastName()));
        dob.setText(Utils.format(currentEditing.getDob()));
        pob.setText(Utils.niceFormat(currentEditing.getPob()));
        mobile.setText(Utils.niceFormat(currentEditing.getMobilePhone()));
        email.setText(Utils.niceFormat(currentEditing.getUsername()));
        sex.setSelection(Gender.indexOf(currentEditing.getSex()), true);
        districtSelector.setText(Utils.niceFormat(currentEditing.getDistrictName()));

    }


}
