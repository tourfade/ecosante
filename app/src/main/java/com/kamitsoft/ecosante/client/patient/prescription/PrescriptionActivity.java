package com.kamitsoft.ecosante.client.patient.prescription;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.MedicationStatus;
import com.kamitsoft.ecosante.constant.PrescriptionType;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.viewmodels.LabsViewModel;
import com.kamitsoft.ecosante.model.viewmodels.MedicationViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PrescriptionActivity extends ImagePickerActivity  {

    private EcoSanteApp app;
    private RecyclerView recyclerView;
    private PresRxAdapter medicationsAdapter;
    private PresLabAdapter labAdapter;
    private EncounterInfo encounter;
    private MedicationViewModel medModel;
    private LabsViewModel labModel;
    private EditText email;
    private Button addEmail, sign, cancel;
    private LinearLayout recepients;
    private Map<String, String> recepientsEA = new HashMap<>();
    private PrescriptionType type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescription);
        app = (EcoSanteApp)getApplication();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        recyclerView = findViewById(R.id.drug_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        encounter = app.getCurrentEncounter();
        toolbar.setSubtitle(Utils.formatPatient(this,app.getCurrentPatient()));
        app.getCurrentLivePatient().observe(this, patientInfo -> {
            toolbar.setSubtitle(Utils.formatPatient(PrescriptionActivity.this,app.getCurrentPatient()));

        });
        type = PrescriptionType.getPrescriptionType(getIntent().getExtras().getInt("type"));

        switch (type){
            case PHARMACY:
                medModel = ViewModelProviders.of(this).get(MedicationViewModel.class);
                medicationsAdapter = new PresRxAdapter(this);
                recyclerView.setAdapter(medicationsAdapter);
                medModel.getEncounterMedications().observe(this, info -> {
                    if(encounter !=null) {
                        medicationsAdapter.syncData(info.stream()
                                .filter(m -> m.getEncounterUuid().equals(encounter.getUuid())
                                        && m.getStatus() == MedicationStatus.NEW.status)
                                .collect(Collectors.toList()));
                    }
                });
                break;

            case LAB:
                labModel = ViewModelProviders.of(this).get(LabsViewModel.class);
                labAdapter = new PresLabAdapter(this);
                recyclerView.setAdapter(labAdapter);
                labModel.getEncounterLabs().observe(this, info -> {
                    if(encounter !=null) {
                        labAdapter.syncData(info.stream()
                                .filter(m -> m.getEncounterUuid().equals(encounter.getUuid())
                                        && !m.isDone())
                                .collect(Collectors.toList()));
                    }
                });
                break;
            default: finish();
        }



        initWidgets();
        if(!Utils.isNullOrEmpty(encounter.getSupervisor().email))
            addEmail(encounter.getSupervisor().email,
                    encounter.getSupervisor().supFullName,
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

        app.service().sendPrescription(
                type,
                encounter.getUuid(),
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