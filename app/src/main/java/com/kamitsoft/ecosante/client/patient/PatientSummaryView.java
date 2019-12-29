package com.kamitsoft.ecosante.client.patient;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.PatientBaseFragment;
import com.kamitsoft.ecosante.client.TextWatchAdapter;
import com.kamitsoft.ecosante.client.patient.dialogs.AllergiesEditorDialog;
import com.kamitsoft.ecosante.client.patient.dialogs.FallsEditorDialog;
import com.kamitsoft.ecosante.client.patient.dialogs.SurgeriesEditorDialog;
import com.kamitsoft.ecosante.client.patient.oracles.PhysistOracleAdapter;
import com.kamitsoft.ecosante.constant.BloodGroup;
import com.kamitsoft.ecosante.constant.Gender;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.PhysicianInfo;
import com.kamitsoft.ecosante.model.SummaryInfo;
import com.kamitsoft.ecosante.model.json.ExtraData;
import com.kamitsoft.ecosante.model.viewmodels.PatientsViewModel;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.lifecycle.ViewModelProviders;

public class PatientSummaryView extends PatientBaseFragment {
    private EditText doctorCell, doctorEmail,
            specialist, specialistCell, specialistEmail,
            longTermTreatment, notes;
    PhysistOracleAdapter physistOracle;
    private AutoCompleteTextView doctor;
    private AppCompatSpinner rhesus;
    private AppCompatCheckBox  idm, avc, falls, surgeries, allergies, drop, dementia, hta, epilepsy, irc, asthm, diabete,
            glaucoma, hepatitb, hypertyroid, other, menopause, falciform;
    private SummaryInfo currentSummary;
    private PatientInfo currentPatient;
    private PatientsViewModel model;
    private boolean initialized;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.summary_view, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = ViewModelProviders.of(this).get(PatientsViewModel.class);


        model.getCurrentPatient().observe(this, patientInfo -> {
            if(patientInfo == null){
                return;
            }
            this.currentPatient = patientInfo;
            model.getCurrentSummary(currentPatient.getUuid())
                    .observe(PatientSummaryView.this, summaryInfo -> {
                        currentSummary = summaryInfo;
                        if(currentSummary == null){
                            currentSummary = new SummaryInfo();
                            currentSummary.setPatientUuid(currentPatient.getUuid());
                            currentSummary.setPatientID(currentPatient.getPatientID());
                        }
                        initSummaryInfo();
            });

        });

        doctor = view.findViewById(R.id.ecosantedocotor);
        physistOracle = new PhysistOracleAdapter(getActivity());

        doctorCell = view.findViewById(R.id.ecosantedocotorMobile);
        doctorEmail = view.findViewById(R.id.ecosantedocotorEmail);

        specialist = view.findViewById(R.id.specialistdocotor);
        specialistCell = view.findViewById(R.id.specialistdocotorMobile);
        specialistEmail = view.findViewById(R.id.specialistdocotorEmail);

        longTermTreatment = view.findViewById(R.id.longTermTreatment);
        notes = view.findViewById(R.id.diseasenotes);
        surgeries = view.findViewById(R.id.surgeries);
        idm = view.findViewById(R.id.idm);
        avc = view.findViewById(R.id.avc);
        falls = view.findViewById(R.id.falls);

        allergies = view.findViewById(R.id.allergy);
        drop = view.findViewById(R.id.drop);
        dementia = view.findViewById(R.id.dementia);
        hta = view.findViewById(R.id.hta);

        epilepsy = view.findViewById(R.id.epilespy);
        irc = view.findViewById(R.id.irc);
        asthm = view.findViewById(R.id.ashmatic);
        diabete = view.findViewById(R.id.diabetes);

        glaucoma = view.findViewById(R.id.glaucoma);
        hepatitb = view.findViewById(R.id.hepatitis);
        hypertyroid = view.findViewById(R.id.hyperthyroidism);
        other = view.findViewById(R.id.others);

        menopause = view.findViewById(R.id.menopause);
        falciform = view.findViewById(R.id.falciformanemia);

        rhesus = view.findViewById(R.id.gsrh);
        edit(getActivity().getIntent().getBooleanExtra("isNew", false));

        initListeners();


    }

    @Override
    protected Class<?> getEntity() {
        return SummaryInfo.class;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
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
                edit(!super.edit);
                break;
            case R.id.action_save:
                model.updateSummary(currentSummary);
                edit(false);
                break;
        }
        getActivity().invalidateOptionsMenu();
        return true;

    }


    public void edit(boolean editable) {
        super.edit = editable;
        doctor.setEnabled(editable);
        doctorCell.setEnabled(editable);
        doctorEmail.setEnabled(editable);

        specialist.setEnabled(editable);
        specialistCell.setEnabled(editable);
        specialistEmail.setEnabled(editable);

        longTermTreatment.setEnabled(editable);
        notes.setEnabled(editable);
        idm.setEnabled(editable);
        avc.setEnabled(editable);
        //((View)falls.getParent()).setEnabled(editable);
        falls.setEnabled(editable);
        //((View)surgeries.getParent()).setEnabled(editable);
        surgeries.setEnabled(editable);
        allergies.setEnabled(editable);
        drop.setEnabled(editable);
        dementia.setEnabled(editable);
        hta.setEnabled(editable);

        epilepsy.setEnabled(editable);
        irc.setEnabled(editable);
        asthm.setEnabled(editable);


        glaucoma.setEnabled(editable);
        hepatitb.setEnabled(editable);
        hypertyroid.setEnabled(editable);
        other.setEnabled(editable);

        menopause.setEnabled(editable);

        diabete.setEnabled(editable);
        falciform.setEnabled(editable);
        ((View)diabete.getParent()).setEnabled(editable);
        ((View)falciform.getParent()).setEnabled(editable);

        rhesus.setEnabled(editable);
    }

    private void initListeners() {
        doctor.setOnItemClickListener((parent, view, position, id) -> {
            PhysicianInfo doc = physistOracle.getItem(position);
            String dl = Utils.formatUser(getContext(), doc);
            doctor.setText(dl);
            currentSummary.setDoctor(dl);
            currentSummary.setDoctorID(doc.userID);

            doctorCell.setText(Utils.niceFormat(doc.mobilePhone));
            currentSummary.setDoctorCell(Utils.niceFormat(doc.mobilePhone));

            doctorEmail.setText(Utils.niceFormat(doc.email));
            currentSummary.setDoctorEmail(Utils.niceFormat(doc.email));
        });

        doctorCell.addTextChangedListener(new TextWatchAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                currentSummary.setDoctorCell(s.toString());

            }
        });
        doctorEmail.addTextChangedListener(new TextWatchAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                currentSummary.setDoctorEmail(s.toString());

            }
        });

        specialist.addTextChangedListener(new TextWatchAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                currentSummary.setSpecialist(s.toString());
            }
        });
        specialistCell.addTextChangedListener(new TextWatchAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                currentSummary.setSpecialistCell(s.toString());
            }
        });
        specialistEmail.addTextChangedListener(new TextWatchAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                currentSummary.setSpecialistEmail(s.toString());
            }
        });
        longTermTreatment.addTextChangedListener(new TextWatchAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                currentSummary.setRunningLongTreatment(s.toString());
            }
        });
        notes.addTextChangedListener(new TextWatchAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                currentSummary.setNotes(s.toString());
            }
        });

        idm.setOnCheckedChangeListener((buttonView, isChecked) -> {

            currentSummary.setIdm(isChecked);
            if(!buttonView.isPressed()) {
                return;
            }
            if(isChecked){
                Utils.manageDataPicker(getActivity(), currentSummary.getIdmDate(), date -> {
                    idm.setText(Utils.niceFormat(Utils.format(date)));
                    currentSummary.setIdmDate(new Timestamp(date.getTime()));
                });
            }else{
                currentSummary.setIdmDate(null);
                idm.setText("");
            }
        });
        avc.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentSummary.setAvc(isChecked);
            if(!buttonView.isPressed()) {
                return;
            }
            if(isChecked){
                Utils.manageDataPicker(getActivity(),currentSummary.getIdmDate(),date -> {
                    avc.setText(Utils.niceFormat(Utils.format(date)));
                    currentSummary.setAvcDate(new Timestamp(date.getTime()));
                });
            }else{
                currentSummary.setAvcDate(null);
                avc.setText("");
            }
        });

        ((View)falls.getParent()).setOnClickListener((v) -> {
            new FallsEditorDialog(currentSummary.getFalls(),falls,edit)
                    .show(getFragmentManager(), "FallsEditorDialog");

        });


        drop.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentSummary.setDrop(isChecked);
        });

        dementia.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentSummary.setDementia(isChecked);
        });
        hta.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentSummary.setHta(isChecked);
        });

        epilepsy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentSummary.setEpilepsy(isChecked);
        });
        irc.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentSummary.setIrc(isChecked);
        });
        asthm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentSummary.setAsthma(isChecked);
        });

        ((View)allergies.getParent()).setOnClickListener(v -> {
            new AllergiesEditorDialog(currentSummary.getAllergies(),allergies,edit)
                    .show(getFragmentManager(), "AllergyEditorDialog");
        });
        ((View)surgeries.getParent()).setOnClickListener((v) -> {
            new SurgeriesEditorDialog(currentSummary.getSurgeries(),surgeries,edit)
                    .show(getFragmentManager(), "SurgeryEditorDialog");

        });

        ((View)diabete.getParent()).setOnClickListener(this::prepareDiabeteDialog);

        glaucoma.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentSummary.setGlaucoma(isChecked);
        });
        hepatitb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentSummary.setHepatitb(isChecked);
        });
        hypertyroid.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentSummary.setHyperthyroid(isChecked);
        });
        other.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentSummary.setOther(isChecked);
        });

        menopause.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentSummary.setMenopause(isChecked);
            if(!buttonView.isPressed()) {
                return;
            }
            if(isChecked){
                Utils.manageDataPicker(getActivity(),
                        currentSummary.getMenopauseDate(),
                        date -> {
                            currentSummary.setMenopauseDate(new Timestamp(date.getTime()));
                            menopause.setText(Utils.format(date)+"  ("+Utils.formattedAgeOf(currentPatient.getDob())+")");
                        });
            }else{
                currentSummary.setMenopauseDate(null);
                menopause.setText("");
            }
        });

        ((View)falciform.getParent()).setOnClickListener(this::prepareFalciformDialog);

        rhesus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSummary.setRhesus(BloodGroup.values()[position].group);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentSummary.setRhesus(BloodGroup.UNKNOWN.group);
            }
        });


        initialized = true;

    }


    private void initSummaryInfo() {
        if (!initialized || currentSummary == null || currentPatient == null) {
            return;
        }
        ((View)menopause.getParent()).setVisibility(currentPatient.getSex() == Gender.FEMALE.sex? View.VISIBLE:View.GONE);

        doctor.setAdapter(physistOracle);
        doctor.setText(Utils.niceFormat(currentSummary.getDoctor()));
        doctorCell.setText(Utils.niceFormat(currentSummary.getDoctorCell()));
        doctorEmail.setText(Utils.niceFormat(currentSummary.getDoctorEmail()));

        specialist.setText(Utils.niceFormat(currentSummary.getSpecialist()));
        specialistCell.setText(Utils.niceFormat(currentSummary.getSpecialistCell()));
        specialistEmail.setText(Utils.niceFormat(currentSummary.getSpecialistEmail()));

        longTermTreatment.setText(Utils.niceFormat(currentSummary.getRunningLongTreatment()));
        notes.setText(Utils.niceFormat(currentSummary.getNotes()));

        idm.setChecked(currentSummary.isIdm());
        idm.setText(currentSummary.isIdm()?Utils.format(currentSummary.getIdmDate()):"");

        avc.setChecked(currentSummary.isAvc());
        avc.setText(currentSummary.isAvc()?Utils.format(currentSummary.getAvcDate()):"");

        if(currentSummary.getSurgeries().items.size() > 0){
            surgeries.setChecked(true);
            currentSummary.getSurgeries().items.sort((ex1,ex2)->ex1.date > ex2.date?1:-1);
            ExtraData last = currentSummary.getSurgeries().items.get(currentSummary.getSurgeries().items.size()-1);
            surgeries.setText(currentSummary.getSurgeries().items.size() +" Chirurgies. dernier -> le " + Utils.format(new Date(last.date)));

        }else{
            surgeries.setChecked(false);
            surgeries.setText("");
        }
        if(currentSummary.getFalls().items.size() > 0){
            falls.setChecked(true);
            currentSummary.getFalls().items.sort((ex1,ex2)->ex1.date > ex2.date?1:-1);
            ExtraData last = currentSummary.getFalls().items.get(currentSummary.getFalls().items.size()-1);
            falls.setText(currentSummary.getFalls().items.size() +" chutes. dernier -> le " + Utils.format(new Date(last.date)));

        }else{
            falls.setChecked(false);
            falls.setText("");
        }

        if(currentSummary.getAllergies().items.size() > 0){
            allergies.setChecked(true);
            currentSummary.getAllergies().items.sort((ex1,ex2)->ex1.date > ex2.date?1:-1);
            ExtraData last = currentSummary.getAllergies().items.get(currentSummary.getAllergies().items.size()-1);
            allergies.setText(currentSummary.getAllergies().items.size() +" Allergies. "+last.name);

        }else{
            allergies.setChecked(false);
            allergies.setText("");
        }

        drop.setChecked(currentSummary.isDrop());
        dementia.setChecked(currentSummary.isDementia());
        hta.setChecked(currentSummary.isHta());

        epilepsy.setChecked(currentSummary.isEpilepsy());
        irc.setChecked(currentSummary.isIrc());
        asthm.setChecked(currentSummary.isAsthma());
        glaucoma.setChecked(currentSummary.isGlaucoma());
        hepatitb.setChecked(currentSummary.isHepatitb());
        hypertyroid.setChecked(currentSummary.isHyperthyroid());
        other.setChecked(currentSummary.isOther());

        menopause.setChecked(currentSummary.isMenopause());
        if(currentSummary.isMenopause()){
            menopause.setText(Utils.format(currentSummary.getMenopauseDate())+" à ("+Utils.formattedAgeOf(currentPatient.getDob())+")");
        }else{
            menopause.setText("");
        }

        initItem(diabete);
        initItem(falciform);

        rhesus.setSelection(BloodGroup.indexOf(currentSummary.getRhesus()));
    }



    private void initItem(View view) {
        switch (view.getId()){
            case R.id.diabetes:
                diabete.setChecked(currentSummary.isDiabete());
                if(currentSummary.isDiabete()){
                    diabete.setText(Utils.stringFromArray(getContext(),
                            R.array.diabete_forms,currentSummary.getDiabete().form)+
                            " ("+Utils.formattedAgeOf(currentSummary.getDiabete().date)+")");
                }else {
                    diabete.setText("");
                }
                break;

            case R.id.falciformanemia:
                falciform.setChecked(currentSummary.isFalciform());
                if(currentSummary.isFalciform()){
                    falciform.setText(Utils.stringFromArray(getContext(), R.array.anemia_forms,currentSummary.getFalciform().form)+", "+
                            Utils.niceFormat(currentSummary.getFalciform().percent)+"%");
                }else {
                    falciform.setText("");
                }
                break;


        }
    }


    void prepareDiabeteDialog(View v) {

        final Calendar calendar = Calendar.getInstance();
        showDesease(diabete, R.string.diabetes, R.layout.disease_diabete, R.drawable.diabete,
                d -> {
                    currentSummary.getDiabete().form = Utils.intFromSpiner(d.findViewById(R.id.diabeteType));
                    currentSummary.getDiabete().note = Utils.stringFromEditText(d.findViewById(R.id.notes));
                    currentSummary.getDiabete().date = calendar.getTimeInMillis();

                    initItem(diabete);
                },
                d -> {
                    EditText date = d.findViewById(R.id.diag_date);
                    Utils.manageDataPicker(getActivity(), date, calendar);
                    Utils.initEditText(d.findViewById(R.id.notes),Utils.niceFormat(currentSummary.getDiabete().note));
                    long dd = currentSummary.getDiabete().date;
                    if(dd > 0) {
                        calendar.setTimeInMillis(dd);
                    }
                    Utils.initEditText(date,Utils.format(dd > 0 ? new Timestamp(dd):calendar.getTime()));
                    Utils.initSpiner(d.findViewById(R.id.diabeteType),currentSummary.getDiabete().form);

                },
                d -> {
                    currentSummary.getDiabete().form = -1;
                    currentSummary.getDiabete().note = "";
                    currentSummary.getDiabete().date = 0;
                    initItem(diabete);
                });

    }

    void prepareFalciformDialog(View v){
        final Calendar calendar = Calendar.getInstance();
        showDesease(falciform,R.string.falciform, R.layout.disease_falciform, R.drawable.falciform,
                d -> {
                    currentSummary.getFalciform().form = Utils.intFromSpiner(d.findViewById(R.id.forms));
                    currentSummary.getFalciform().percent = Utils.doubleFromEditText(d.findViewById(R.id.percent));
                    currentSummary.getFalciform().date = calendar.getTimeInMillis();
                    initItem(falciform);
                },
                d -> {
                    EditText date = d.findViewById(R.id.diag_date);
                    Utils.manageDataPicker(getActivity(), date, calendar);
                    if(currentSummary.getFalciform().percent >= 0) {
                        Utils.initEditText(d.findViewById(R.id.percent), Utils.niceFormat(currentSummary.getFalciform().percent));
                    }
                    Utils.initEditText(date,Utils.format(calendar.getTime()));
                    Utils.initSpiner(d.findViewById(R.id.forms),currentSummary.getFalciform().form);

                },
                d->{
                    currentSummary.getFalciform().form = 0;
                    currentSummary.getFalciform().percent = -1;
                    currentSummary.getFalciform().date = 0;
                    initItem(falciform);
                });

    }


    private void showDesease(AppCompatCheckBox chkbx,
                             @StringRes int title,
                             @LayoutRes int pickerLayout,
                             @DrawableRes int icon,
                             Mappper mappper,
                             Initer initer,
                             Remover remover) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(pickerLayout);
        alertDialogBuilder.setIcon(icon);

        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
            mappper.map((AlertDialog) dialog);
        });
        alertDialogBuilder.setNegativeButtonIcon(getResources().getDrawable(R.drawable.save, getActivity().getTheme()));

        alertDialogBuilder.setNegativeButton("Supprimer", (dialog, which) -> {
            remover.remove((AlertDialog)dialog);
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            initer.init((AlertDialog) dialog);
        });
        alertDialogBuilder.setNegativeButtonIcon(getResources().getDrawable(R.drawable.delete, getActivity().getTheme()));



        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    @FunctionalInterface
    interface Remover{
        void remove(AlertDialog d);
    }
    @FunctionalInterface
    interface Mappper{
        void map(AlertDialog d);
    }
    @FunctionalInterface
    interface Initer{
        void init(AlertDialog d);
    }
}