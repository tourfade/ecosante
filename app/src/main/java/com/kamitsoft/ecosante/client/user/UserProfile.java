package com.kamitsoft.ecosante.client.user;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.BaseFragment;
import com.kamitsoft.ecosante.client.TextWatchAdapter;
import com.kamitsoft.ecosante.client.patient.oracles.PhysistOracleAdapter;
import com.kamitsoft.ecosante.client.patient.oracles.SpecialityOracleAdapter;
import com.kamitsoft.ecosante.client.user.dialog.PasswordEditorDialog;
import com.kamitsoft.ecosante.client.user.subscription.PayDunyaProxy;
import com.kamitsoft.ecosante.client.user.subscription.PaymentWebView;
import com.kamitsoft.ecosante.client.user.subscription.Subscription;
import com.kamitsoft.ecosante.client.user.subscription.order.CustomData;
import com.kamitsoft.ecosante.client.user.subscription.order.IOrder;
import com.kamitsoft.ecosante.client.user.subscription.order.Invoice;
import com.kamitsoft.ecosante.client.user.subscription.order.RedirectResponse;
import com.kamitsoft.ecosante.client.user.subscription.order.Store;
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
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.ViewModelProviders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfile extends BaseFragment {

    private static final int PAYMENT_REQUEST = 101;
    private ImageView userPicture;
    private EditText firstname, lastname,dob,
    pob, address, fixphone,mobile,email,validityDate;
    private TextView specialityOrSupervisorText;
    private AutoCompleteTextView specialityOrSupervisor;
    private Button renewSubscription, updatePassword;
    private AppCompatSpinner sex, title;
    private UserInfo cu;
    private ImagePickerActivity picker;
    private SpecialityOracleAdapter specialityOracle;
    private PhysistOracleAdapter physistOracle;
    private UsersViewModel model;
    private View specSupContainner;
    private UserInfo connectedUser;


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
        return inflater.inflate(R.layout.user_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userPicture = view.findViewById(R.id.userPicture);
        title = view.findViewById(R.id.title);
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        dob = view.findViewById(R.id.dob);
        pob = view.findViewById(R.id.pob);
        specialityOrSupervisorText = view.findViewById(R.id.specialityOrSupervisorText);
        specialityOrSupervisor = view.findViewById(R.id.specialityOrSupervisor);
        specSupContainner = view.findViewById(R.id.specSupContainner);
        address = view.findViewById(R.id.address);
        fixphone = view.findViewById(R.id.fixphone);
        mobile = view.findViewById(R.id.mobile);
        email = view.findViewById(R.id.email);
        renewSubscription = view.findViewById(R.id.renewSubscription);
        updatePassword = view.findViewById(R.id.updatePassword);
        sex = view.findViewById(R.id.sex);
        validityDate = view.findViewById(R.id.validity_date);
        cu = app.getEditingUser();
        connectedUser = app.getCurrentUser();
        initListeners();
        initValues();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                edit(!super.edit);
                break;

            case R.id.action_save:
                model.insert(cu);
                //model.setSupervisor(pnp);
                edit(false);
                if(contextActivity ==null){
                   getActivity().onBackPressed();
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
        address.setEnabled(edit);
        fixphone.setEnabled(edit);
        mobile.setEnabled(edit);
        email.setEnabled(edit);

        sex.setEnabled(edit);
    }
    private void initListeners() {
        if(picker != null) {
            picker.setPlaceholder(R.drawable.physist);
            picker.setSelectionFinishedListener((avatar)-> cu.setAvatar(avatar));
            userPicture.setOnClickListener(picker::pick);
        }

        renewSubscription.setOnClickListener(v->{});
        updatePassword.setOnClickListener(v->{
            new PasswordEditorDialog(cu).show(getFragmentManager(), "PasswordEditorDialog");
        });
        title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cu.setTitle(TitleType.ofIndex(position).value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cu.setTitle(TitleType.UNKNOWN.value);
            }
        });

        firstname.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    firstname.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    cu.setFirstName(s.toString());
                }
            }
        });
        lastname.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    lastname.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    cu.setLastName(s.toString().toUpperCase());
                }
            }
        });
        pob.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    pob.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    cu.setPob(s.toString());
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
                cu.setDob(new Timestamp(calendar.getTimeInMillis()));
            },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH) );
            datePickerDialog.show();
        });

        specialityOrSupervisor.setOnItemClickListener((parent, view, position, id) -> {
            if(UserType.isPhysist(cu.getUserType())) {
                Speciality s = specialityOracle.getItem(position);
                specialityOrSupervisor.setText(s.getName());
                cu.setSpeciality(s.toString());
                cu.setSpecialityCode(s.getFieldvalue());
            }
            if(UserType.isNurse(cu.getUserType())) {
                PhysicianInfo s = physistOracle.getItem(position);
                String sFullName = Utils.formatUser(getActivity(), s);
                specialityOrSupervisor.setText(sFullName);
                Supervisor sup = new Supervisor();
                sup.nurseUuid = cu.getUuid();
                sup.accountId = connectedUser.getAccountID();
                sup.active = true;
                sup.physicianUuid = s.uuid;
                sup.supFullName = sFullName;
                cu.setSupervisor(sup);
            }
        });



        address.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    address.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    cu.setAddress(s.toString());
                }
            }
        });

        fixphone.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                {
                    cu.setFixPhone(s.toString());
                }
            }
        });
        mobile.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    mobile.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    cu.setMobilePhone(s.toString());
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
                        cu.setEmail(s.toString());
                    }
                }
            }
        });


        sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               cu.setSex(Gender.values()[position].sex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cu.setSex(Gender.UNKNOWN.sex);
            }
        });

        renewSubscription.setOnClickListener(v->{
            Subscription s = new Subscription();
            s.name= "Abonnement pour "+UserType.typeOf(cu.getUserType()).getLocaleName(getActivity());
            s.price = 2300;
            s.userUuid = cu.getUuid();
            s.uuid = UUID.randomUUID().toString();
            startPayment(s);

        });

    }
    private void initValues(){
        edit(false);
        if(cu == null){return;}
        specSupContainner.setVisibility(View.GONE);
        if(UserType.isPhysist(cu.getUserType())){
            specSupContainner.setVisibility(View.VISIBLE);
            specialityOracle = new SpecialityOracleAdapter(getActivity());
            specialityOrSupervisor.setAdapter(specialityOracle);
            specialityOrSupervisorText.setText(R.string.speciality);
        }
        if( UserType.isNurse(cu.getUserType())){
            specialityOrSupervisorText.setText(R.string.supervisor);
            specSupContainner.setVisibility(View.VISIBLE);
            if(UserType.isAdmin(connectedUser.getUserType()))
                physistOracle = new PhysistOracleAdapter(getActivity());
                specialityOrSupervisor.setAdapter(physistOracle);

            if(UserType.isPhysist(connectedUser.getUserType())){
                if(cu.getSupervisor() == null){
                    cu.setSupervisor(new Supervisor());
                }
                cu.getSupervisor().accountId = connectedUser.getAccountID();
                cu.getSupervisor().active = true;
                cu.getSupervisor().nurseUuid = cu.getUuid();
                cu.getSupervisor().physicianUuid = connectedUser.getUuid();
                cu.getSupervisor().supFullName = Utils.formatUser(contextActivity, connectedUser);
            }

            specialityOrSupervisor.setText(cu.getSupervisor() !=null? cu.getSupervisor().supFullName:"");
            specialityOrSupervisor.setClickable(UserType.isAdmin(connectedUser.getUserType()));

        }


        Utils.load(getActivity(),cu.getAvatar(), userPicture,R.drawable.user_avatar,cu.getUserType() == UserType.PHYSIST.type ? R.drawable.physist: R.drawable.nurse);

        title.setSelection(TitleType.typeOf(cu.getTitle()).index);
        firstname.setText(Utils.niceFormat(cu.getFirstName()));
        lastname.setText(Utils.niceFormat(cu.getLastName()));
        dob.setText(Utils.format(cu.getDob()));
        pob.setText(Utils.niceFormat(cu.getPob()));
        if(UserType.isPhysist(cu.getUserType())) {
            specialityOrSupervisor.setText(Utils.niceFormat(cu.getSpeciality()));
        }



        address.setText(Utils.niceFormat(cu.getAddress()));
        fixphone.setText(Utils.niceFormat(cu.getFixPhone()));
        mobile.setText(Utils.niceFormat(cu.getMobilePhone()));
        email.setText(Utils.niceFormat(cu.getEmail()));


        sex.setSelection(Gender.indexOf(cu.getSex()), true);
    }


    private void startPayment(Subscription subscription) {

        IOrder order = new IOrder();
        order.invoice = new Invoice();
        order.invoice.description = subscription.name;
        order.invoice.total_amount = subscription.price;
        order.store = new Store();
        order.custom_data = new CustomData(cu,subscription);

        getPayDunyaAPIService().createInvoce(order).enqueue(new Callback<RedirectResponse>() {
            @Override
            public void onResponse(Call<RedirectResponse> call, final Response<RedirectResponse> response) {
                if (response.code() == 200) {
                    RedirectResponse rep = response.body();
                    Intent i = new Intent(getContext(), PaymentWebView.class);
                    i.putExtra("url", rep.response_text);
                    startActivityForResult(i,PAYMENT_REQUEST);
                } else if (response.code() == 206) {
                    Toast.makeText(getContext(), response.body().response_text, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), R.string.uknown_error, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<RedirectResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), R.string.uknown_error, Toast.LENGTH_SHORT).show();

            }
        });

    }
    public  PayDunyaProxy getPayDunyaAPIService() {

        return new Retrofit.Builder()
                .baseUrl(PayDunyaProxy.PD_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(PayDunyaProxy.class);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }
}
