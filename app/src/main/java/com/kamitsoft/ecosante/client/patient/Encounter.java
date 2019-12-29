package com.kamitsoft.ecosante.client.patient;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.VitalFilter;
import com.kamitsoft.ecosante.client.TextWatchAdapter;
import com.kamitsoft.ecosante.client.adapters.LabsAdapter;
import com.kamitsoft.ecosante.client.adapters.MedicationsAdapter;
import com.kamitsoft.ecosante.client.patient.dialogs.LabEditorDialog;
import com.kamitsoft.ecosante.client.patient.dialogs.MedicationEditorDialog;
import com.kamitsoft.ecosante.constant.BehaviorType;
import com.kamitsoft.ecosante.constant.StatusConstant;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.constant.VitalType;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.MedicationInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.EncountersViewModel;
import com.kamitsoft.ecosante.model.viewmodels.EntitiesViewModel;
import com.kamitsoft.ecosante.model.viewmodels.LabsViewModel;
import com.kamitsoft.ecosante.model.viewmodels.MedicationViewModel;

import java.sql.Timestamp;
import java.util.stream.Collectors;


public class Encounter extends ImagePickerActivity implements View.OnClickListener, CheckBox.OnCheckedChangeListener, AdapterView.OnItemClickListener {
    private EncounterInfo encounterInfo;
    private TextView pressure,temperature,weight,glycemy, height, waist,breath, heart, duration;
    private CheckBox smoking, ethylism, tea, otherBehaviour, diabeticDiet ,hypocaloricDiet, hyposodeDiet, hyperprotidicDiet,
    omi, apathy, emaciation, anorexia, asthenia, dysphnea;
    private Spinner orientation,autonomy;
    private RecyclerView lab, drugs;
    private LabsAdapter labsAdapter;
    private MedicationsAdapter drugsAdapter;
    private LabInfo curentLab;
    private EditText field, advisings, runningTreatment;
    private EcoSanteApp app;
    private MedicationInfo curentMedication;
    private LabsViewModel labsModel;
    private MedicationViewModel medModel;
    private EncountersViewModel model;
    private EntitiesViewModel entityModel;
    private EncountersViewModel encounterModel;
    private View editActions, reviesActions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (EcoSanteApp)getApplication();
        setTitle(R.string.encounter);
        setContentView(R.layout.encounter_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        model = ViewModelProviders.of(this).get(EncountersViewModel.class);
        app.getCurrentLivePatient().observe(this, patientInfo -> toolbar.setSubtitle(Utils.formatPatient(Encounter.this,patientInfo)));


        labsModel = ViewModelProviders.of(this).get(LabsViewModel.class);
        medModel = ViewModelProviders.of(this).get(MedicationViewModel.class);
        entityModel = ViewModelProviders.of(this).get(EntitiesViewModel.class);
        encounterModel = ViewModelProviders.of(this).get(EncountersViewModel.class);
        editActions = findViewById(R.id.editActions);
        reviesActions = findViewById(R.id.reviewActions);
        pressure = findViewById(R.id.pressure);
        temperature = findViewById(R.id.temperature);
        weight = findViewById(R.id.weight);
        glycemy = findViewById(R.id.glycemy);
        height = findViewById(R.id.height);
        waist = findViewById(R.id.waistSize);
        breath = findViewById(R.id.breathrate);
        heart = findViewById(R.id.heartrate);
        field = findViewById(R.id.field);
        smoking = findViewById(R.id.smoking);
        ethylism = findViewById(R.id.ethylism);
        tea = findViewById(R.id.tea);
        otherBehaviour  = findViewById(R.id.otherlfiebehaviours);

        runningTreatment = findViewById(R.id.running_treatment);
        duration = findViewById(R.id.durationValue);
        duration.setOnEditorActionListener(null);
        lab = findViewById(R.id.labs);
        labsAdapter = new LabsAdapter(this);
        lab.setAdapter(labsAdapter);
        drugs = findViewById(R.id.drugs);
        drugsAdapter = new MedicationsAdapter(this);
        drugs.setAdapter(drugsAdapter);


        advisings = findViewById(R.id.advisings);
        diabeticDiet = findViewById(R.id.diabetic_diet);
        hypocaloricDiet = findViewById(R.id.hypocaloric_diet);
        hyposodeDiet = findViewById(R.id.hyposode_diet);
        hyperprotidicDiet = findViewById(R.id.hyperprotidic_diet);

        omi = findViewById(R.id.omi);
        apathy = findViewById(R.id.apathy);
        emaciation = findViewById(R.id.emaciation);
        anorexia = findViewById(R.id.anorexia);
        asthenia = findViewById(R.id.asthenia);
        dysphnea = findViewById(R.id.dysphnea);
        autonomy = findViewById(R.id.autonomy);
        orientation = findViewById(R.id.orientation);
        encounterInfo = app.getCurrentEncounter();

        findViewById(R.id.cancel).setOnClickListener(v->{
            setResult(Activity.RESULT_OK);
            app.exitEncounter();
            finish();
        });
        findViewById(R.id.save).setOnClickListener(v->{
            setResult(Activity.RESULT_OK);
            model.insert(encounterInfo);
            app.exitEncounter();
            finish();
        });
        findViewById(R.id.reject).setOnClickListener(v->{
            setResult(Activity.RESULT_OK);
            encounterInfo.setCurrentStatus(StatusConstant.REJECTED);
            model.insert(encounterInfo);
            app.exitEncounter();
            finish();
        });
        findViewById(R.id.accept).setOnClickListener(v->{
            setResult(Activity.RESULT_OK);
            encounterInfo.setCurrentStatus(StatusConstant.ACCEPTED);
            model.insert(encounterInfo);
            app.exitEncounter();
            finish();
        });
        initViewListeners();
        initValue();
        labsModel.getEncounterLabs().observe(this, info -> {
            if(encounterInfo!=null) {
                labsAdapter.syncData(info.stream().filter(m -> m.getEncounterUuid().equals(encounterInfo.getUuid())).collect(Collectors.toList()));
            }
        });
        medModel.getEncounterMedications().observe(this, info -> {
            if(encounterInfo!=null) {
                drugsAdapter.syncData(info.stream().filter(m -> m.getEncounterUuid().equals(encounterInfo.getUuid())).collect(Collectors.toList()));
            }
        });
        encounterModel.getEncounters().observe(this,encounterInfos -> {
            for(EncounterInfo e:encounterInfos){
                if(encounterInfo == null || e.getUuid().equals(encounterInfo.getUuid())){
                    this.encounterInfo = e;
                    initValue();
                    break;
                }
            }
        });


        entityModel.getDirtyEntities().observe(this, entitySyncs -> {
            for(EntitySync e:entitySyncs){

                if(e.getEntity().equalsIgnoreCase(MedicationInfo.class.getSimpleName())){
                    app.service().requestSync(MedicationInfo.class,null);
                }
                if( e.getEntity().equalsIgnoreCase(LabInfo.class.getSimpleName())){
                    app.service().requestSync(LabInfo.class,null);
                }
                if( e.getEntity().equalsIgnoreCase(EncounterInfo.class.getSimpleName())){
                    app.service().requestSync(EncounterInfo.class,null);
                }

            }
        });
    }



    private void initViewListeners() {
        findViewById(R.id.pressureC).setOnClickListener(this);
        findViewById(R.id.temperatureC).setOnClickListener(this);
        findViewById(R.id.weightC).setOnClickListener(this);
        findViewById(R.id.glycemyC).setOnClickListener(this);
        findViewById(R.id.glycemyC).setOnClickListener(this);

        findViewById(R.id.heightC).setOnClickListener(this);
        findViewById(R.id.waistsizeC).setOnClickListener(this);
        findViewById(R.id.breathRateC).setOnClickListener(this);
        findViewById(R.id.heartRateC).setOnClickListener(this);

        omi.setOnCheckedChangeListener(this);
        apathy.setOnCheckedChangeListener(this);
        emaciation.setOnCheckedChangeListener(this);
        anorexia.setOnCheckedChangeListener(this);
        asthenia.setOnCheckedChangeListener(this);
        dysphnea.setOnCheckedChangeListener(this);

        autonomy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                encounterInfo.setAutonomy(position); ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                encounterInfo.setAutonomy(0);
            }
        });
        orientation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                encounterInfo.setOrientation(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                encounterInfo.setAutonomy(0);
            }
        });

        field.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                encounterInfo.setField(s.toString());
            }
        });

        findViewById(R.id.smokingC).setOnClickListener(this);
        findViewById(R.id.ethylismC).setOnClickListener(this);
        findViewById(R.id.teaC).setOnClickListener(this);
        findViewById(R.id.otherC).setOnClickListener(this);

        runningTreatment.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
                encounterInfo.setRunningTreatment(s.toString());
            }
        });

        findViewById(R.id.durationC).setOnClickListener(this);
        findViewById(R.id.durationValue).setOnClickListener(this);
        findViewById(R.id.addLab).setOnClickListener(this);
        labsAdapter.setItemClickListener((itemPosition, v) -> {
            curentLab = labsAdapter.getItem(itemPosition);
            if(v.getId() == R.id.item_delete){
                curentLab.setDeleted(true);
                labsModel.update(curentLab);
                return;
            }
            showLab();
        });
        findViewById(R.id.addMedication).setOnClickListener(this);
        drugsAdapter.setItemClickListener((itemPosition, v) -> {
            curentMedication = drugsAdapter.getItem(itemPosition);
            if(v.getId() == R.id.item_delete){
                curentMedication.setDeleted(true);
                medModel.update(curentMedication);
                return;
            }
            showMedication();
        });

        advisings.addTextChangedListener(new TextWatchAdapter(){

            @Override
            public void afterTextChanged(Editable s) {
            encounterInfo.setAdvising(s.toString());
            }
        });

        diabeticDiet.setOnCheckedChangeListener(this);
        hypocaloricDiet.setOnCheckedChangeListener(this);
        hyposodeDiet.setOnCheckedChangeListener(this);
        hyperprotidicDiet.setOnCheckedChangeListener(this);
    }

    private  void initValue(){
        UserInfo user = app.getCurrentUser();
        if(user !=null){
            reviesActions.setVisibility(user.getUserType() == UserType.PHYSIST.type?View.VISIBLE:View.GONE);
            editActions.setVisibility(user.getUserType() == UserType.NURSE.type?View.VISIBLE:View.GONE);
        }else {
            reviesActions.setVisibility(View.GONE);
        }
        field.setText(encounterInfo.getField());
        runningTreatment.setText(encounterInfo.getRunningTreatment());
        advisings.setText(encounterInfo.getAdvising());
        diabeticDiet.setChecked(encounterInfo.getDiabeticDiet());
        hypocaloricDiet.setChecked(encounterInfo.getHypocaloricDiet());
        hyposodeDiet.setChecked(encounterInfo.getHyposodeDiet());
        hyperprotidicDiet.setChecked(encounterInfo.getHyperprotidicDiet());

        omi.setChecked(encounterInfo.isOmi());
        apathy.setChecked(encounterInfo.isApathy());
        emaciation.setChecked(encounterInfo.isEmaciation());
        anorexia.setChecked(encounterInfo.isAnorexia());
        asthenia.setChecked(encounterInfo.isAsthenia());
        dysphnea.setChecked(encounterInfo.isDysphnea());

        autonomy.setSelection(encounterInfo.getAutonomy());
        orientation.setSelection(encounterInfo.getOrientation());

        for(VitalType vt:VitalType.values()){
            setVitalValues(vt);
        }
        for(BehaviorType bt:BehaviorType.values()){
            setBehaviorsValues(bt);
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pressureC:
                showVital(VitalType.PRESSURE);
                break;
            case R.id.temperatureC:
                showVital(VitalType.TEMPERATURE);
                break;

            case R.id.weightC:
                showVital(VitalType.WEIGHT);
                break;
            case R.id.glycemyC:
                showVital(VitalType.GLYCEMY);
                break;

            case R.id.heightC:
                showVital(VitalType.HEIGHT);
                break;
            case R.id.waistsizeC:
                showVital(VitalType.WAISTSIZE);
                break;
            case R.id.breathRateC:
                showVital(VitalType.BREATHRATE);
                break;
            case R.id.heartRateC:
                showVital(VitalType.HEARTRATE);
                break;
            case R.id.smokingC:
                showBehaviors(BehaviorType.SMOKING);
                break;
            case R.id.ethylismC:
                showBehaviors(BehaviorType.ETHYLISM);
                break;
            case R.id.teaC:
                showBehaviors(BehaviorType.TEA);
                break;
            case R.id.otherC:
                showBehaviors(BehaviorType.OTHERBEHAVIORS);
                break;
            case R.id.durationC:
            case R.id.durationValue:
                showBehaviors(BehaviorType.DURATION);
                break;
            case R.id.addLab:
                curentLab = null;
                showLab();
                break;
            case R.id.addMedication:
                curentMedication = null;
                showMedication();
                break;
        }
    }

    private void showLab() {
        boolean readyForNew = false;
        if(curentLab == null){
            curentLab = app.newLab();
            readyForNew = true;
        }
        new LabEditorDialog(curentLab,readyForNew).show(getSupportFragmentManager(),"labdialog");

    }

    private void showMedication() {
        boolean readyForNew = false;
        if(curentMedication == null){
            curentMedication = app.newMedication();
            readyForNew = true;
        }
        new MedicationEditorDialog(curentMedication,readyForNew).show(getSupportFragmentManager(),"medicationdialog");

    }

    private void showBehaviors(BehaviorType behave) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(behave.title);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(behave.pickerLayout);
        alertDialogBuilder.setIcon(behave.icon);
        alertDialogBuilder.setPositiveButton("Ok",(dialog, which)->{
            mapDialogBehaveValues(behave, ((AlertDialog) dialog));
        });
        alertDialogBuilder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            AlertDialog d = (AlertDialog)dialog;
            //((ImageView)d.findViewById(R.id.behaveIcon)).setImageResource(behave.icon);
            ((TextInputLayout) d.findViewById(R.id.behaveQuantityContainner)).setHint(getString(behave.hint));

            initBehaveDialog(behave,d);
        });
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }
    public void showVital(VitalType vitalType){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(vitalType.title);
        alertDialogBuilder.setIcon(vitalType.drawable);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(vitalType.pickerLayout);
        alertDialogBuilder.setPositiveButton("Ok",(dialog, which)->{mapVitalDialogValues(vitalType, (AlertDialog)dialog );});
        alertDialogBuilder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            AlertDialog d = (AlertDialog)dialog;
            initVitalDialog(vitalType,d);
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        alertDialog.show();
    }

    private void initBehaveDialog(BehaviorType behave, AlertDialog d) {
        EditText bq = d.findViewById(R.id.behaveQuantity);
        bq.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                mapDialogBehaveValues(behave, d);
                d.dismiss();
            }
            return false;
        });
        switch (behave){
            case SMOKING:
                bq.setText(Utils.niceFormat(encounterInfo.getSmokeNbCigarettes()));
                break;
            case ETHYLISM:
                bq.setText(Utils.niceFormat(encounterInfo.getAlcoholNbCups()));
                break;
            case TEA:
                bq.setText(Utils.niceFormat(encounterInfo.getTeaNbCups()));
                break;
            case OTHERBEHAVIORS:
                bq.setText(Utils.niceFormat(encounterInfo.getOtherBehaviorsNotes()));
                break;

            case DURATION:
                bq.setText(Utils.niceFormat(encounterInfo.getRunningTreatmentDuration()));
                ((AppCompatSpinner)d.findViewById(R.id.durationUnit)).setSelection(encounterInfo.getRunningTreatmentDurationUnit(), true);

                break;
        }

    }

    private void initVitalDialog(VitalType vitalType, AlertDialog d) {
        switch (vitalType){
            case HEARTRATE:
                ((AppCompatSpinner)d.findViewById(R.id.heartRateBodySite)).setSelection(encounterInfo.getHeartRateSite(), true);
                EditText hr = d.findViewById(R.id.heartrate);
                hr.setFilters(new InputFilter[]{new VitalFilter(35, 280)});
                hr.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE){
                        mapVitalDialogValues(vitalType, d );
                        d.dismiss();
                    }
                    return false;
                });
                hr.setText(Utils.niceFormat(encounterInfo.getHeartRate()));
                break;
            case PRESSURE:
                EditText sys = d.findViewById(R.id.systolic);
                EditText dias = d.findViewById(R.id.diastolic);
                sys.addTextChangedListener(new TextWatchAdapter() {
                    @Override
                    public void afterTextChanged(Editable s) {
                      if(s.toString().length() >= 3){
                          dias.requestFocus();
                      }

                    }
                });
                sys.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_NEXT)
                        dias.requestFocus();
                    return false;
                });
                dias.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE){
                        mapVitalDialogValues(vitalType, d );
                        d.dismiss();
                    }
                    return false;
                });
                sys.setFilters(new InputFilter[]{new VitalFilter(80, 300)});
                dias.setFilters(new InputFilter[]{new VitalFilter(50, 100)});
                sys.setText(Utils.niceFormat0(encounterInfo.getPressureSystolic()));
                dias.setText(Utils.niceFormat0(encounterInfo.getPressureDiastolic()));
                break;
            case BREATHRATE:
                EditText breath = d.findViewById(R.id.breathrate);
                breath.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE){
                        mapVitalDialogValues(vitalType, d );
                        d.dismiss();
                    }
                    return false;
                });
                breath.setFilters(new InputFilter[]{new VitalFilter(10, 80)});
                breath.setText(Utils.niceFormat0(encounterInfo.getBreathRate()));
                break;
            case GLYCEMY:
                EditText glyc = d.findViewById(R.id.glycemy);
                glyc.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE){
                        mapVitalDialogValues(vitalType, d );
                        d.dismiss();
                    }
                    return false;
                });
                glyc.setFilters(new InputFilter[]{new VitalFilter(50, 180)});
                ((AppCompatSpinner)d.findViewById(R.id.glycemyState)).setSelection(encounterInfo.getGlycemyState(), true);
                glyc.setText(Utils.niceFormat(encounterInfo.getGlycemy()));
                break;

            case HEIGHT:
                EditText heigh = d.findViewById(R.id.height);
                heigh.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE){
                        mapVitalDialogValues(vitalType, d );
                        d.dismiss();
                    }
                    return false;
                });
                heigh.setFilters(new InputFilter[]{new VitalFilter(50, 230)});
                heigh.setText(Utils.niceFormat(encounterInfo.getHeight()));
                break;
            case WEIGHT:
                EditText weight = d.findViewById(R.id.weight);
                weight.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE){
                        mapVitalDialogValues(vitalType, d );
                        d.dismiss();
                    }
                    return false;
                });
                weight.setFilters(new InputFilter[]{new VitalFilter(50, 600)});
                weight.setText(Utils.niceFormat(encounterInfo.getWeight()));
                break;
            case WAISTSIZE:
                EditText waistSize = d.findViewById(R.id.waistSize);
                waistSize.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE){
                        mapVitalDialogValues(vitalType, d );
                        d.dismiss();
                    }
                    return false;
                });
                waistSize.setFilters(new InputFilter[]{new VitalFilter(30, 180)});
                waistSize.setText(Utils.niceFormat(encounterInfo.getWaistSize()));
                break;

            case TEMPERATURE:
                EditText temperature = d.findViewById(R.id.temperature);
                temperature.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE){
                        mapVitalDialogValues(vitalType, d );
                        d.dismiss();
                    }
                    return false;
                });
                temperature.setFilters(new InputFilter[]{new VitalFilter(24, 42)});
                ((AppCompatSpinner)d.findViewById(R.id.temperatureBodyPart)).setSelection(encounterInfo.getTemperatureBodyPart(), true);
                temperature.setText(Utils.niceFormat(encounterInfo.getTemperature()));
                break;
        }
    }


    private void mapDialogBehaveValues(BehaviorType behave,AlertDialog d  ) {

        int qt = 0;
        switch (behave) {
            case SMOKING:
                qt = Utils.intFromEditText(d.findViewById(R.id.behaveQuantity));
                encounterInfo.setSmoke(qt > 0);
                encounterInfo.setSmokeNbCigarettes(qt);
                break;
            case ETHYLISM:
                qt = Utils.intFromEditText(d.findViewById(R.id.behaveQuantity));
                encounterInfo.setAlcohol(qt > 0);
                encounterInfo.setAlcoholNbCups(qt);
                break;
            case TEA:
                qt = Utils.intFromEditText(d.findViewById(R.id.behaveQuantity));
                encounterInfo.setTea(qt > 0);
                encounterInfo.setTeaNbCups(qt);
                break;
            case OTHERBEHAVIORS:
                String note = ((EditText)(d.findViewById(R.id.behaveQuantity))).getText().toString();
                encounterInfo.setOtherBehaviors(note !=null && note.trim().length() > 0);
                encounterInfo.setOtherBehaviorsNotes(note);
                break;

            case DURATION:
                int nbvalue = Utils.intFromEditText(d.findViewById(R.id.behaveQuantity));
                encounterInfo.setRunningTreatmentDuration(nbvalue);
                int unit = ((AppCompatSpinner)d.findViewById(R.id.durationUnit)).getSelectedItemPosition();
                encounterInfo.setRunningTreatmentDurationUnit(unit);
                break;
        }
        setBehaviorsValues(behave);
    }

    private void mapVitalDialogValues(VitalType vitalType, AlertDialog d) {
        switch (vitalType){
            case PRESSURE:
                encounterInfo.setPressureSystolic(Utils.floatFromEditText(d.findViewById(R.id.systolic)));
                encounterInfo.setPressureDiastolic(Utils.floatFromEditText(d.findViewById(R.id.diastolic)));
                break;

            case TEMPERATURE:
                encounterInfo.setTemperature(Utils.floatFromEditText(d.findViewById(R.id.temperature)));
                Spinner s = d.findViewById(R.id.temperatureBodyPart);
                encounterInfo.setTemperatureBodyPart(s.getSelectedItemPosition());
                break;

            case WEIGHT:
                encounterInfo.setWeight(Utils.floatFromEditText(d.findViewById(R.id.weight)));
                break;

            case GLYCEMY:
                encounterInfo.setGlycemy(Utils.floatFromEditText(d.findViewById(R.id.glycemy)));
                Spinner state = d.findViewById(R.id.glycemyState);
                encounterInfo.setGlycemyState(state.getSelectedItemPosition());
                break;

            case HEIGHT:
                encounterInfo.setHeight(Utils.floatFromEditText(d.findViewById(R.id.height)));

                break;

            case WAISTSIZE:
                encounterInfo.setWaistSize(Utils.floatFromEditText(d.findViewById(R.id.waistSize)));

                break;

            case BREATHRATE:
                encounterInfo.setBreathRate(Utils.intFromEditText(d.findViewById(R.id.breathrate)));

                break;

            case HEARTRATE:
                encounterInfo.setHeartRate(Utils.intFromEditText(d.findViewById(R.id.heartrate)));
                Spinner site = d.findViewById(R.id.heartRateBodySite);
                encounterInfo.setHeartRateSite(site.getSelectedItemPosition());
                break;



        }
        setVitalValues(vitalType);
    }

    private void setVitalValues(VitalType vitalType) {
        switch (vitalType){
            case PRESSURE:
                pressure.setText(Utils.format(encounterInfo.getPressureSystolic())+"/"+
                        Utils.format(encounterInfo.getPressureDiastolic())+" "+getString(vitalType.unit));
                break;

            case TEMPERATURE:
                temperature.setText(Utils.format(encounterInfo.getTemperature())+getString(vitalType.unit));
                break;

            case WEIGHT:
                weight.setText(Utils.format(encounterInfo.getWeight())+getString(vitalType.unit));
                break;

            case GLYCEMY:
                glycemy.setText(Utils.format(encounterInfo.getGlycemy())+getString(vitalType.unit));
                break;


            case HEIGHT:
                height.setText(Utils.format(encounterInfo.getHeight())+getString(vitalType.unit));
                break;

            case WAISTSIZE:
                waist.setText(Utils.format(encounterInfo.getWaistSize())+getString(vitalType.unit));
                break;

            case BREATHRATE:
                breath.setText(Utils.format(encounterInfo.getBreathRate())+getString(vitalType.unit));
                break;

            case HEARTRATE:
                heart.setText(Utils.format(encounterInfo.getHeartRate())+getString(vitalType.unit));
                break;


        }
    }
    private void setBehaviorsValues(BehaviorType behaviorType) {
        switch (behaviorType){
            case SMOKING:
                smoking.setChecked(encounterInfo.isSmoke());

                smoking.setText(encounterInfo.isSmoke() ? encounterInfo.getSmokeNbCigarettes()+" "+getString(R.string.cigperday):"");

                break;
            case ETHYLISM:
                ethylism.setChecked(encounterInfo.isAlcohol());
                ethylism.setText(encounterInfo.isAlcohol() ? encounterInfo.getAlcoholNbCups()+" "+getString(R.string.gcupperday):"");
                break;
            case TEA:
                tea.setChecked(encounterInfo.isTea());
                tea.setText(encounterInfo.isTea() ? encounterInfo.getTeaNbCups()+" "+getString(R.string.cupperday):"");
                break;
            case OTHERBEHAVIORS:
                otherBehaviour.setChecked(encounterInfo.isOtherBehaviors());
                otherBehaviour.setText(encounterInfo.isOtherBehaviors() ? encounterInfo.getOtherBehaviorsNotes():"");
                break;

            case DURATION:
                String unit = (getResources().getStringArray(R.array.duration_unit))[encounterInfo.getRunningTreatmentDurationUnit()];
                duration.setText(""+(encounterInfo.getRunningTreatmentDuration() <= 0 ? "":
                               (encounterInfo.getRunningTreatmentDuration()+unit.substring(0,3)+".")));
                break;

        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.omi: encounterInfo.setOmi(isChecked);break;
            case R.id.apathy: encounterInfo.setApathy(isChecked);break;
            case R.id.emaciation: encounterInfo.setEmaciation(isChecked);break;
            case R.id.anorexia: encounterInfo.setAnorexia(isChecked);break;
            case R.id.asthenia: encounterInfo.setAsthenia(isChecked);break;
            case R.id.dysphnea: encounterInfo.setDysphnea(isChecked);break;

            case R.id.diabetic_diet: encounterInfo.setDiabeticDiet(isChecked);break;
            case R.id.hyperprotidic_diet: encounterInfo.setHyperprotidicDiet(isChecked);break;
            case R.id.hypocaloric_diet: encounterInfo.setHypocaloricDiet(isChecked);break;
            case R.id.hyposode_diet: encounterInfo.setHyposodeDiet(isChecked);break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


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

}
