package com.kamitsoft.ecosante.client.patient;

import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.ViewModelProviders;

import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.TextWatchAdapter;
import com.kamitsoft.ecosante.constant.Gender;
import com.kamitsoft.ecosante.constant.MaritalStatus;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.viewmodels.PatientsViewModel;
import com.kamitsoft.ecosante.nfc.NfcMethod;

import java.io.IOException;
import java.util.Calendar;

public class PatientProfileView extends PatientBaseFragment  {
    private ImageView patientPicture;
    private EditText firstName, lastName, dob, pob, occupation,
            address, fixPhone, mobile,email,matricule,contactPerson,
            personContactphone1,personContactphone2,perosnContactAddress;
    private AppCompatSpinner sex, status;
    private AppCompatCheckBox retired, ipres,fnr,official;
    private ImagePickerActivity picker;
    private PatientsViewModel model;
    private boolean isNew;
    private String oldavatar;
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcIntent;
    private AlertDialog.Builder dialogNfc;
    private AlertDialog dialog;
    private NfcMethod nfcMethod=new NfcMethod();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.patient_view, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = ViewModelProviders.of(this).get(PatientsViewModel.class);
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);

        patientPicture = view.findViewById(R.id.patientPicture);
        firstName = view.findViewById(R.id.firstname);
        lastName = view.findViewById(R.id.lastname);
        dob = view.findViewById(R.id.dob);
        pob = view.findViewById(R.id.pob);
        sex = view.findViewById(R.id.sex);
        status = view.findViewById(R.id.status);
        occupation = view.findViewById(R.id.occupation);
        retired = view.findViewById(R.id.retired);
        ipres = view.findViewById(R.id.ipres);
        fnr = view.findViewById(R.id.fnr);
        official  = view.findViewById(R.id.official);
        address = view.findViewById(R.id.address);
        fixPhone = view.findViewById(R.id.fixphone);
        mobile = view.findViewById(R.id.mobile);
        email = view.findViewById(R.id.email);
        matricule = view.findViewById(R.id.matricule);
        contactPerson = view.findViewById(R.id.contactPerson);
        personContactphone1 = view.findViewById(R.id.personContactphone1);
        personContactphone2 = view.findViewById(R.id.personContactphone2);
        perosnContactAddress = view.findViewById(R.id.perosnContactAddress);
        //currentPatient = app.getCurrentPatient();
        isNew = getActivity().getIntent().getBooleanExtra("isNew", false);

        initListeners();
        edit(isNew);

        model.getCurrentPatient().observe(this, patientInfo -> {
            if (patientInfo == null) { return;}
            oldavatar = patientInfo.getAvatar();
            this.currentPatient = patientInfo;
            this.currentPatient.setNeedUpdate(true);
            initPatientInfo();
        });
        dialogNfc=new AlertDialog.Builder(view.getContext())
                .setTitle("Sauvegarder")//Titre de la boite de dialogue
                .setMessage("Veuillez approchez une puce NFC pour enregistrer.")//Message affiché

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.


                // A null listener allows the button to dismiss the dialog and take no further action.
                //Bouton pour fermer la boite de dialogue
                .setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);//Ajoute une icône

        dialog=dialogNfc.create();

        model.getAllDatas().observe(this, patientInfo -> {
            if (patientInfo == null || currentPatient == null) { return;}
            for(PatientInfo p:patientInfo) {
                if (currentPatient != null && currentPatient.getUuid().equals(p.getUuid())) {
                    currentPatient.setNeedUpdate(true);
                    model.setCurrentPatient(p);
                    break;
                }
            }

        });

    }



    @Override
    protected Class<?> getEntity() {
        return PatientInfo.class;
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

        switch (item.getItemId()) {
            case R.id.action_edit:
                edit(true);
                break;

            case R.id.action_save:

                dialog.show();

                    nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
                //Vérifie si l'appareil suporte la technologie NFC
                if (nfcAdapter == null) {// Si NFC n'est pas supporté

                    //      Toast.makeText(this,"Device does not support NFC!",Toast.LENGTH_LONG).show();
                    //this.finish();
                } else {
                    if (!nfcAdapter.isEnabled()) { // Si NFC n'est pas activé

                        //   Toast.makeText(this, "Enable NFC!",Toast.LENGTH_LONG).show();

                    } else {//Si tout va bien
                        nfcIntent = PendingIntent.getActivity(this.getContext(),
                                0, new Intent(this.getContext(), PatientActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                        nfcAdapter.enableForegroundDispatch(this.contextActivity, nfcIntent, null,null);

                        break;
                    }

        }
                break;

        }


        return true;

    }

    public void save() {
        currentPatient.setNeedUpdate(true);
        model.insert(currentPatient);
        picker.syncAvatar(currentPatient.getAvatar(),oldavatar, 0);

        //model.setCurrentPatient(currentPatient);
    }

    public void edit(boolean editable){
        super.edit = editable;
        patientPicture.setEnabled(editable);
        patientPicture.setClickable(editable);

        firstName.setEnabled(editable);
        firstName.setError(null);
        lastName.setEnabled(editable);
        lastName.setError(null);
        dob.setEnabled(editable);
        dob.setError(null);

        pob.setEnabled(editable);
        pob.setError(null);

        sex.setEnabled(editable);

        status.setEnabled(editable);

        occupation.setEnabled(editable);
        occupation.setError(null);

        retired.setEnabled(editable);
        ipres.setEnabled(editable);
        fnr.setEnabled(editable);
        official.setEnabled(editable);
        address.setEnabled(editable);
        address.setError(null);

        fixPhone.setEnabled(editable);
        fixPhone.setError(null);

        mobile.setEnabled(editable);
        mobile.setError(null);

        email.setEnabled(editable);
        email.setError(null);

        contactPerson.setEnabled(editable);
        contactPerson.setError(null);

        personContactphone1.setEnabled(editable);
        perosnContactAddress.setError(null);

        personContactphone2.setEnabled(editable);
        personContactphone2.setError(null);

        perosnContactAddress.setEnabled(editable);
        perosnContactAddress.setError(null);
        getActivity().invalidateOptionsMenu();
    }

    private void initListeners() {
        if(getActivity() instanceof  ImagePickerActivity) {
            picker = (ImagePickerActivity) getActivity();
            picker.setPlaceholder(R.drawable.patient);
            picker.setSelectionFinishedListener((avatar)-> {
                currentPatient.setAvatar(avatar);
            });
            patientPicture.setOnClickListener(picker::pick);
        }

        firstName.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    firstName.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    currentPatient.setFirstName(s.toString());
                }
            }
        });
        lastName.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    lastName.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    currentPatient.setLastName(s.toString().toUpperCase());
                }
            }
        });
        pob.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    pob.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    currentPatient.setPob(s.toString());
                }
            }
        });
        occupation.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    occupation.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    currentPatient.setOccupation(s.toString());
                }
            }
        });
        address.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    address.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    currentPatient.setAddress(s.toString());
                }
            }
        });

        fixPhone.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                 {
                    currentPatient.setFixPhone(s.toString());
                }
            }
        });
        mobile.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    mobile.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    currentPatient.setMobile(s.toString());
                }
            }
        });

        email.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                {
                    currentPatient.setEmail(s.toString());
                }
            }
        });
        matricule.setEnabled(false);
        contactPerson.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    contactPerson.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    currentPatient.setContactPerson(s.toString());
                }
            }
        });
        personContactphone1.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                if(s==null || s.toString().trim().length()<= 0){
                    personContactphone1.setError(getString(R.string.shouldNotbeBlank));
                }else {
                    currentPatient.setContactPhone1(s.toString());
                }
            }
        });
        personContactphone2.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                {
                    currentPatient.setContactPhone2(s.toString());
                }
            }
        });
        perosnContactAddress.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                {
                    currentPatient.setContactAddress(s.toString());
                }
            }
        });

        sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPatient.setSex(Gender.values()[position].sex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentPatient.setSex(Gender.UNKNOWN.sex);
            }
        });
        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPatient.setMaritalStatus(MaritalStatus.values()[position].status);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentPatient.setMaritalStatus(MaritalStatus.UNKNOWN.status);
            }
        });

        retired.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentPatient.setRetired(isChecked);
            //Toast.makeText(getContext(),"retired"+isChecked, Toast.LENGTH_LONG).show();
        });
        ipres.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentPatient.setIpres(isChecked);
            //Toast.makeText(getContext(),"ipres"+isChecked, Toast.LENGTH_LONG).show();
        });
        fnr.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentPatient.setFnr(isChecked);
        });
        official.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentPatient.setOfficial(isChecked);
            //Toast.makeText(getContext(),"official"+isChecked, Toast.LENGTH_LONG).show();
        });

        dob.setOnClickListener(v->{

            int[] d = currentPatient.getDob();
            d = d!=null && d.length == 3? d:Utils.toArray(Calendar.getInstance());
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                currentPatient.setDob(new int[]{year, month, dayOfMonth});
                dob.setText(Utils.format(currentPatient.getDob()));
            },d[0],d[1],d[2]);
            datePickerDialog.show();
            
        });
    }

    private  void initPatientInfo(){
        if(currentPatient == null){
            return;
        }
        if(isNew){
            currentPatient.setDistrictUuid(app.getCurrentUser().getDistrictUuid());
            currentPatient.getMonitor().monitorUuid = app.getCurrentUser().getUuid();
            currentPatient.getMonitor().patientUuid = currentPatient.getUuid();
            currentPatient.getMonitor().active = true;
            currentPatient.getMonitor().monitorFullName = Utils.formatUser(getContext(),app.getCurrentUser());
            ((ImagePickerActivity)getActivity()).locateUser(location -> {
                this.currentPatient.setLat(location.getLatitude());
                this.currentPatient.setLon(location.getLongitude());
            });
        }
        int res = Gender.FEMALE.sex==currentPatient.getSex()?R.drawable.patient_f:R.drawable.patient;

        Utils.load(getContext(),
                BuildConfig.AVATAR_BUCKET,
                currentPatient.getAvatar(),
                patientPicture,res,res);


        firstName.setText(Utils.niceFormat(currentPatient.getFirstName()));
        lastName.setText(Utils.niceFormat(currentPatient.getLastName()));
        dob.setText(Utils.niceFormat(Utils.format(currentPatient.getDob())));
        pob.setText(Utils.niceFormat(currentPatient.getPob()));
        sex.setSelection(Gender.indexOf(currentPatient.getSex()));
        status.setSelection(MaritalStatus.indexOf(currentPatient.getMaritalStatus()));
        occupation.setText(Utils.niceFormat(currentPatient.getOccupation()));
        retired.setChecked(currentPatient.getRetired());
        ipres.setChecked(currentPatient.getIpres());
        fnr.setChecked(currentPatient.getFnr());
        official.setChecked(currentPatient.getOfficial());
        address.setText(Utils.niceFormat(currentPatient.getAddress()));
        fixPhone.setText(Utils.niceFormat(currentPatient.getFixPhone()));
        mobile.setText(Utils.niceFormat(currentPatient.getMobile()));
        email.setText(Utils.niceFormat(currentPatient.getEmail()));
        matricule.setText(Utils.niceFormat(currentPatient.getMatricule()));
        contactPerson.setText(Utils.niceFormat(currentPatient.getContactPerson()));
        personContactphone1.setText(Utils.niceFormat(currentPatient.getContactPhone1()));
        personContactphone2.setText(Utils.niceFormat(currentPatient.getContactPhone2()));
        perosnContactAddress.setText(Utils.niceFormat(currentPatient.getContactAddress()));


    }


    public void writeTag(Intent intent) {

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        try {
            nfcMethod.write(app.getCurrentPatient().getUuid(),tag );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        save();
        edit(false);
        dialog.dismiss();

    }
}
