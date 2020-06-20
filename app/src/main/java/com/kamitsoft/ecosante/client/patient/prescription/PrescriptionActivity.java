package com.kamitsoft.ecosante.client.patient.prescription;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.adapters.MedicationsAdapter;
import com.kamitsoft.ecosante.client.adapters.PatientEncountersAdapter;
import com.kamitsoft.ecosante.constant.MedicationStatus;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.viewmodels.EncountersViewModel;
import com.kamitsoft.ecosante.model.viewmodels.MedicationViewModel;
import com.kamitsoft.ecosante.model.viewmodels.PatientsViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PrescriptionActivity extends ImagePickerActivity  {

    private EcoSanteApp app;
    private RecyclerView medicationsRecyclerView;
    private PresMedAdapter medicationsAdapter;
    private EncountersViewModel encountersViewModel;
    private EncounterInfo prescription;
    private MedicationViewModel medModel;
    private EditText email;
    private Button addEmail, sign, cancel;
    private LinearLayout recepients;
    private Map<String, String> recepientsEA = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescription);
        app = (EcoSanteApp)getApplication();
        encountersViewModel = ViewModelProviders.of(this).get(EncountersViewModel.class);
        medModel = ViewModelProviders.of(this).get(MedicationViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        medicationsRecyclerView = findViewById(R.id.drug_list);
        medicationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        medicationsAdapter = new PresMedAdapter(this);
        medicationsRecyclerView.setAdapter(medicationsAdapter);
        prescription = app.getCurrentEncounter();
        toolbar.setSubtitle(Utils.formatPatient(this,app.getCurrentPatient()));
        app.getCurrentLivePatient().observe(this, patientInfo -> {
            toolbar.setSubtitle(Utils.formatPatient(PrescriptionActivity.this,app.getCurrentPatient()));

        });

        medModel.getEncounterMedications().observe(this, info -> {
            if(prescription!=null) {
                medicationsAdapter.syncData(info.stream()
                        .filter(m -> m.getEncounterUuid().equals(prescription.getUuid())
                                && m.getStatus() == MedicationStatus.NEW.status)
                        .collect(Collectors.toList()));
            }
        });

        initWidgets();
        if(!Utils.isNullOrEmpty(prescription.getSupervisor().email))
            addEmail(prescription.getSupervisor().email,
                    prescription.getSupervisor().supFullName,
                    "sup");

        if(!Utils.isNullOrEmpty(app.getCurrentPatient().getEmail()))
            addEmail(app.getCurrentPatient().getEmail(),
                    Utils.formatPatient(this,app.getCurrentPatient()),
                    "pat");


    }

    private void initWidgets() {
         email = findViewById(R.id.recipient_email);
         addEmail = findViewById(R.id.add_email);
         recepients = findViewById(R.id.recepients_list);
         addEmail.setOnClickListener(v-> addEmail(email.getText().toString().trim(), "Autre","k"+recepientsEA.size()));
         sign = findViewById(R.id.sign);
         sign.setOnClickListener(this::send);
         findViewById(R.id.cancel).setOnClickListener(v-> finish());
    }

    private void send(View view) {

        if(recepientsEA.size() <= 0){
            email.setError("Il faut au moins un destinataire !");
            Toast.makeText(this, "Il faut au moins un destinataire !",Toast.LENGTH_LONG).show();
            return;
        }

        app.service().sendPrescription(prescription.getUuid(),
                recepientsEA,
                ()->{
            Toast.makeText(this, "La prescription a été envoyé",Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void addEmail(String mail, String name, String key) {
        if(mail ==null || mail.trim().length() <= 5){
            email.setError("Saisir  email de destinataire");
            return;
        }
        recepientsEA.put(key, mail);
        View view = getLayoutInflater().inflate(R.layout.pres_email_item, null);
        ((TextView) view.findViewById(R.id.dname)).setText(name);
        ((TextView) view.findViewById(R.id.demail)).setText(mail);
        view.setTag(key);
        view.setOnLongClickListener(this::onLongClick);
        email.setError(null);
        email.setText("");
        recepients.addView(view);

    }


    public boolean onLongClick(View v) {
        View verso = v.findViewById(R.id.item_delete);
        int width = verso.getRight() - verso.getLeft();
        AnimatorSet anset = new AnimatorSet();
        anset.play(ObjectAnimator
                .ofInt(v.findViewById(R.id.insider), "scrollX", 0)
                .setDuration(200))
                .after(3000)
                .after(ObjectAnimator
                        .ofInt(v.findViewById(R.id.insider), "scrollX", width)
                        .setDuration(200));
        anset.start();
        verso.setOnClickListener(vv -> {
            recepientsEA.remove(v.getTag());
            recepients.removeView(v);
        });
        return false;
    }

}