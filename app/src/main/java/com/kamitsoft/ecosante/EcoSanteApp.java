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
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.kamitsoft.ecosante.client.patient.Encounter;
import com.kamitsoft.ecosante.client.user.dialog.PasswordEditorDialog;
import com.kamitsoft.ecosante.constant.StatusConstant;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.MedicationInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.SummaryInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.json.Status;
import com.kamitsoft.ecosante.model.repositories.UsersRepository;
import com.kamitsoft.ecosante.services.ApiSyncService;
import com.kamitsoft.ecosante.services.WorkerService;


public class EcoSanteApp extends MultiDexApplication {
    private UserInfo editingUser;
    private KsoftDatabase db;
    private UserInfo currentUser;
    private MutableLiveData<PatientInfo> currentPatient = new MutableLiveData<>();
    private MutableLiveData<EncounterInfo> currentEncounter = new MutableLiveData<>();
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

        usersRepository = new UsersRepository(this);
        usersRepository.getConnectedUser().observeForever(userInfo -> {
            if(userInfo == null && currentUser !=null){
                service().unSubscribe(currentUser.getAccountID());
            }
            currentUser = userInfo;


        });

        currentPatient.observeForever(patientInfo -> {
            if(patientInfo == null){
                this.currentEncounter.setValue(null);
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

                encounterInfo.getMonitor().monitorFullName = Utils.formatUser(this, currentUser);
                encounterInfo.getMonitor().monitorUuid = currentUser.getUuid();
                encounterInfo.getMonitor().active = true;
                encounterInfo.setUserUuid(currentUser.getUuid());
                if(UserType.isPhysist(currentUser.getUserType())){
                    encounterInfo.getSupervisor().physicianUuid = currentUser.getUuid();
                    encounterInfo.getSupervisor().nurseUuid = currentUser.getUuid();
                    encounterInfo.getSupervisor().active = true;
                }
                if(UserType.isNurse(currentUser.getUserType())){
                    encounterInfo.setSupervisor(currentUser.getSupervisor());
                }

            }
        });


        observeNetwork();

        Intent intent = new Intent(this, ApiSyncService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);


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

    public void setCurrentUser(UserInfo userInfo) {
        currentUser = userInfo;
    }
    public UserInfo getCurrentUser() {

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
        EncounterInfo e = new EncounterInfo();
        e.getStatus().add(new Status(StatusConstant.NEW.status));
        currentEncounter.setValue(e);


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






    public LabInfo newLab() {
        LabInfo  li = new LabInfo();
        li.setPatientID(currentPatient.getValue().getPatientID());
        li.setPatientUuid(currentPatient.getValue().getUuid());
        li.setEncounterUuid(currentEncounter.getValue().getUuid());
        return li;
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






}
