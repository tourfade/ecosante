package com.kamitsoft.ecosante;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.MedicationInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.SummaryInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.repositories.UsersRepository;
import com.kamitsoft.ecosante.model.viewmodels.DocumentsViewModel;
import com.kamitsoft.ecosante.model.viewmodels.UsersViewModel;
import com.kamitsoft.ecosante.services.ApiSyncService;
import com.kamitsoft.ecosante.services.WorkerService;

import java.sql.Timestamp;

public class EcoSanteApp extends MultiDexApplication {
    private UserInfo editingUser;
    private KsoftDatabase db;
    private UserInfo currentUser;
    private MutableLiveData<PatientInfo> currentPatient = new MutableLiveData<>();
    private MutableLiveData<EncounterInfo> currentEncounter = new MutableLiveData<>();
    private SummaryInfo currentSummary;
    private DocumentInfo currentDocument;
    private UsersRepository usersRepository;
    public static final String  CHANNEL_ID = "com.kamitsoft.dmi.dmichannel";

    private ApiSyncService myService;
    protected boolean mBound;
    protected ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            if(service instanceof  ApiSyncService.LocalBinder) {
                ApiSyncService.LocalBinder binder = (ApiSyncService.LocalBinder) service;
                myService = binder.getService();
                mBound = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            mBound = false;
        }
    };
    private SharedPreferences credential;

    @Override
    public void onCreate() {
        super.onCreate();
        db = KsoftDatabase.getInstance(this);
        credential = getSharedPreferences(getString(R.string.connection_credential), Context.MODE_PRIVATE);

        Stetho.initializeWithDefaults(this);
        creatChannel();
        observeNetwork();
        Intent intent = new Intent(this, ApiSyncService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        usersRepository = new UsersRepository(this);
        usersRepository.getConnectedUser().observeForever(userInfo -> {
            currentUser = userInfo;
        });
        currentPatient.observeForever(patientInfo -> {
            if(patientInfo == null){
                this.currentEncounter.setValue(null);
                this.currentSummary = null;
                this.currentDocument = null;
            }
        });
        currentEncounter.observeForever(encounterInfo -> {
            if(currentUser != null
                    && currentPatient.getValue()!=null
                    && encounterInfo!=null
                    && encounterInfo.getPatientUuid() == null) {

                encounterInfo.setPatientID(currentPatient.getValue().getPatientID());
                encounterInfo.setPatientUuid(currentPatient.getValue().getUuid());
                encounterInfo.setUser(Utils.formatUser(this, currentUser));
                encounterInfo.setUserUuid(currentUser.getUuid());
            }
        });

    }

    private void creatChannel(){
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "DMI health app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void observeNetwork() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent worker =  new Intent(getApplicationContext(), WorkerService.class);
                //startService(worker);
            }
        }, intentFilter);

    }

    public ApiSyncService service(){
        return myService;
    }

    @Override
    public void onTerminate() {
        unbindService(connection);
        super.onTerminate();
    }

    public KsoftDatabase getDb() {
        return db;
    }


    public UserInfo getCurrentUser() {
        if(currentUser == null){
            return null;
        }

        return currentUser;
    }


    public MutableLiveData<PatientInfo> getCurrentLivePatient(){
        return currentPatient;
    }
    public PatientInfo getCurrentPatient() {
        return currentPatient.getValue();
    }
    public void newPatient() {
        exitPatient();
        setCurrentPatient(new PatientInfo());
    }
    public void setCurrentPatient(PatientInfo currentPatient) {
        this.currentPatient.setValue(currentPatient);


    }
    public MutableLiveData<EncounterInfo> getCurrentLiveEncounter() {
        return currentEncounter;
    }

    public EncounterInfo getCurrentEncounter() {
        return currentEncounter.getValue();
    }

    public DocumentInfo getCurrentDocument() {
        return currentDocument;
    }

    public void setCurrentEncounter(EncounterInfo currentEncounter) {
        this.currentEncounter.setValue(currentEncounter);
    }
    public void setNewEncounter() {
        currentEncounter.setValue(new EncounterInfo());


    }
    public void newDocument() {
        currentDocument = new DocumentInfo();
        currentDocument.setPatientID(currentPatient.getValue().getPatientID());
        currentDocument.setPatientUuid(currentPatient.getValue().getUuid());
        currentDocument.setEncounterUuid(currentEncounter.getValue()==null?"":currentEncounter.getValue().getUuid());
        currentDocument.setDocName("Nouveau Document");
    }


    public void exitEncounter() {
        currentEncounter.setValue(null);
    }

    public void exitPatient() {
        currentPatient.setValue(null);
    }

    public SummaryInfo getCurrentSummary() {
        if(currentPatient.getValue() == null){
            return null;
        }
        if(currentSummary == null) {
            currentSummary =  db.summaryDAO().getPatientSummary(currentPatient.getValue().getUuid());
        }
        if(currentSummary == null){
            currentSummary = new SummaryInfo();
            currentSummary.setPatientID(currentPatient.getValue().getPatientID());
            currentSummary.setPatientUuid(currentPatient.getValue().getUuid());
            db.summaryDAO().insert(currentSummary);
        }
        return currentSummary;
    }

    public void saveSummary() {
        final SummaryInfo cs = currentSummary;
        cs.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        if(db.summaryDAO().update(cs) <= 0){
            db.summaryDAO().insert(cs);
        }
    }



    public LabInfo newLab() {
        LabInfo  li = new LabInfo();
        li.setPatientID(currentPatient.getValue().getPatientID());
        li.setPatientUuid(currentPatient.getValue().getUuid());
        li.setEncounterUuid(currentEncounter.getValue().getUuid());
        return li;
    }

    public void disconnectUser() {
        service().unSubscribe(currentUser.getAccountID());
        currentUser = null;
        setConnectionToken("");

    }




    public void updateUserNamePassword() {

    }

    public void setEditingUser(UserInfo item) {
        editingUser = item;
    }
    public UserInfo getEditingUser() {
        return editingUser;
    }

    public MedicationInfo newMedication() {
        MedicationInfo  li = new MedicationInfo();
        li.setPatientID(currentPatient.getValue().getPatientID());
        li.setPatientUuid(currentPatient.getValue().getUuid());
        li.setEncounterUuid(currentEncounter.getValue().getUuid());
        return li;
    }


    public void exitDocument() {
        currentDocument = null;
    }

    public void setCurrentDoc(DocumentInfo item) {
        currentDocument = item;
    }

    public String getConnectionToken() {
        return credential.getString(getString(R.string.connection_token),"");
    }
    public void setConnectionToken(String token) {
        credential
                .edit()
                .putString(getString(R.string.connection_token),token)
                .commit();
    }


}
