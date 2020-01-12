package com.kamitsoft.ecosante.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.Editable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.DiskCache;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.model.AppointmentInfo;
import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.MedicationInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.S3BucketUrl;
import com.kamitsoft.ecosante.model.SummaryInfo;
import com.kamitsoft.ecosante.model.SyncData;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.json.Supervisor;
import com.kamitsoft.ecosante.model.repositories.AppointmentsRepository;
import com.kamitsoft.ecosante.model.repositories.DocumentsRepository;
import com.kamitsoft.ecosante.model.repositories.EncountersRepository;
import com.kamitsoft.ecosante.model.repositories.EntityRepository;
import com.kamitsoft.ecosante.model.repositories.FileRepository;
import com.kamitsoft.ecosante.model.repositories.LabsRepository;
import com.kamitsoft.ecosante.model.repositories.MedicationsRepository;
import com.kamitsoft.ecosante.model.repositories.PatientsRepository;
import com.kamitsoft.ecosante.model.repositories.UsersRepository;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiSyncService extends Service {


    private EncountersRepository encounterRepository;
    private PatientsRepository patientRepository;
    private UsersRepository userRepository;
    private MedicationsRepository medicationRepository;
    private LabsRepository labRepository;
    private DocumentsRepository documentRepository;
    private AppointmentsRepository appointmentRepository;
    private FileRepository fileRepository;
    private EntityRepository entityRepository;




    public class LocalBinder extends Binder {
        public ApiSyncService getService() {
            return ApiSyncService.this;
        }
    }

    private final IBinder binder = new LocalBinder();
    private KsoftDatabase database;
    private EcoSanteApp app;
    private Proxy proxy;
    private DiskCache cache;

    public interface Completion {
        void onReady();
    }
    public interface CompletionWithData<T> {
        void onReady(@Nullable T... data);
    }

    @Override
    public void onCreate() {
        app = (EcoSanteApp) getApplication();
        database = KsoftDatabase.getInstance(getApplicationContext());
        cache = new DiskCache(getApplicationContext());

        initRepositories();
        observ();
        observFiles();

    }

    private void observ() {

        userRepository.getAccount().observeForever(accountInfo->{
            if(accountInfo != null) {
                init(accountInfo);
                userRepository.reset(accountInfo);
                encounterRepository.reset(accountInfo);
                patientRepository.reset(accountInfo);
                appointmentRepository.reset(accountInfo);
                entityRepository.reset();
            }
        });

        encounterRepository.getDirty().observeForever(encounterInfos -> {
            if(proxy == null){
                return;
            }
            EntitySync entitySync = getEntity(EncounterInfo.class);
            List<EncounterInfo> toSynch = encounterInfos.stream().filter(e ->
                    e.getUpdatedAt().getTime() > entitySync.getLastSynced()).collect(Collectors.toList());
            if(toSynch.size() > 0)
                syncEncounters(entitySync, toSynch, null);
        });

        patientRepository.getDirty().observeForever(patients -> {
            if(proxy == null){
                return;
            }
            EntitySync entitySync = getEntity(PatientInfo.class);
            List<PatientInfo> toSynch = patients.parallelStream().filter(e ->
                    e.getUpdatedAt().getTime() > entitySync.getLastSynced()).collect(Collectors.toList());

            if(toSynch.size() > 0)
                syncPatients(entitySync,toSynch, null);

        });

        userRepository.getUserOfType(UserType.PHYSIST).observeForever(users->{
            if(proxy == null){
                return;
            }
            EntitySync entitySycn = getEntity(UserInfo.class);
            List<UserInfo> toSynch = users.stream().filter(e ->
                    e.getUpdatedAt()!=null && e.getUpdatedAt().getTime() > entitySycn.getLastSynced()).collect(Collectors.toList());


            if(toSynch.size() > 0)
                syncUsers(entitySycn, toSynch, null);
        });

        userRepository.getUserOfType(UserType.NURSE).observeForever(users->{
            if(proxy == null){
                return;
            }
            EntitySync entitySycn = getEntity(UserInfo.class);
            List<UserInfo> toSynch = users.stream().filter(e ->
                    e.getUpdatedAt()!=null && e.getUpdatedAt().getTime() > entitySycn.getLastSynced()).collect(Collectors.toList());

            if(toSynch.size() > 0)
                syncUsers(entitySycn, toSynch, null);
        });

        patientRepository.getDirtySummaries().observeForever(summaryInfos -> {
            if(proxy == null){
                return;
            }
            EntitySync entitySycn = getEntity(SummaryInfo.class);
            List<SummaryInfo> toSynch = summaryInfos.stream().filter(e ->
                    e.getUpdatedAt().getTime() > entitySycn.getLastSynced()).collect(Collectors.toList());


            if(toSynch.size() > 0)
                syncSummaries(entitySycn, toSynch, null);
        });

        medicationRepository.getPatientMedications().observeForever(medicationInfos -> {
            if(proxy == null){
                return;
            }
            EntitySync entitySycn = getEntity(MedicationInfo.class);
            List<MedicationInfo> toSynch = medicationInfos.stream().filter(e ->
                    e.getUpdatedAt().getTime() > entitySycn.getLastSynced()).collect(Collectors.toList());


            if(toSynch.size() > 0)
                syncMedications(entitySycn, toSynch, null);
        });

        labRepository.getEncounterLabs().observeForever(labs -> {
            if(proxy == null){
                return;
            }
            EntitySync entitySycn = getEntity(LabInfo.class);
            List<LabInfo> toSynch = labs.stream().filter(e ->
                    e.getUpdatedAt().getTime() > entitySycn.getLastSynced()).collect(Collectors.toList());


            if(toSynch.size() > 0)
                syncLabs(entitySycn, toSynch, null);
        });

        documentRepository.getPatientDocs().observeForever(labs -> {
            if(proxy == null){
                return;
            }

            EntitySync entitySycn = getEntity(DocumentInfo.class);
            List<DocumentInfo> toSynch = labs.stream().filter(e ->
                    e.getUpdatedAt().getTime() > entitySycn.getLastSynced()).collect(Collectors.toList());


            if(toSynch.size() > 0)
                syncDocs(entitySycn, toSynch, null);


        });

        appointmentRepository.getData().observeForever(labs -> {
            if(proxy == null){
                return;
            }
            EntitySync entitySycn = getEntity(AppointmentInfo.class);
            List<AppointmentInfo> toSynch = labs.stream().filter(e ->
                    e.getUpdatedAt().getTime() > entitySycn.getLastSynced()).collect(Collectors.toList());


            if(toSynch.size() > 0)
                syncAppointments(entitySycn, toSynch, null);
        });

    }

    public void requestSync(Class<?> beanClass, Completion completion) {
        //Log.i("XXXXXXXXX--->",beanClass+"");
        EntitySync entitySync = getEntity(beanClass);
        if(entitySync==null ||  !entitySync.isDirty()){
            if(completion != null){
                completion.onReady();
            }
            return;
        }
        //entitySync.setLastSynced(entitySync.getLastSynced()-1000);
        if(beanClass == PatientInfo.class ){
            syncPatients(entitySync,new ArrayList<>(),completion);
        }
        if(beanClass == EncounterInfo.class){
            syncEncounters(entitySync,new ArrayList<>(),completion);
        }
        if(beanClass == SummaryInfo.class){
            syncSummaries(entitySync,new ArrayList<>(),completion);
        }
        if(beanClass == UserInfo.class){
            syncUsers(entitySync,new ArrayList<>(),completion);
        }
        if(beanClass == MedicationInfo.class){
            syncMedications(entitySync,new ArrayList<>(),completion);
        }
        if(beanClass == LabInfo.class){
            syncLabs(entitySync,new ArrayList<>(),completion);
        }
        if(beanClass == DocumentInfo.class){
            syncDocs(entitySync,new ArrayList<>(),completion);
        }
        if(beanClass == AppointmentInfo.class){
            syncAppointments(entitySync,new ArrayList<>(),completion);
        }
    }

    private void initRepositories() {
        encounterRepository = new EncountersRepository(app);
        patientRepository = new PatientsRepository(app);
        userRepository = new UsersRepository(app);
        medicationRepository = new MedicationsRepository(app);
        labRepository = new LabsRepository(app);
        documentRepository = new DocumentsRepository(app);
        appointmentRepository = new AppointmentsRepository(app);
        fileRepository = new FileRepository(app);
        entityRepository = new EntityRepository(app);

    }

    public void updatePassword(String oldpw, String npw, CompletionWithData<Boolean> completion) {
        proxy.updateCredentials(oldpw,npw).enqueue(new Callback<UserAccountInfo>() {
            @Override
            public void onResponse(Call<UserAccountInfo> call, Response<UserAccountInfo> response) {
                if(response.code() == 200){
                    userRepository.connect(response.body());
                }
                completion.onReady(response.code() == 200);

            }

            @Override
            public void onFailure(Call<UserAccountInfo> call, Throwable t) {
                completion.onReady(false);
            }
        });
    }

    private void syncUsers(EntitySync entitySync, List<UserInfo> dirty, Completion completion) {
        SyncData<List<UserInfo>> data = new SyncData<>();
        data.timestamp = entitySync.getLastSynced();
        data.data = dirty;
        entitySync.setLastSynced(System.currentTimeMillis());
        entitySync.setDirty(false);
        proxy.syncUser(data).enqueue(new Callback<List<UserInfo>>(){
            @Override
            public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {

                if (response.code() == 200){
                    response.body().stream().forEach(e->  e.setUpdatedAt(new Timestamp(data.timestamp)));

                    List<UserInfo> toDelete = response.body().stream().filter(e -> e.isDeleted()).collect(Collectors.toList());
                    if(toDelete != null && toDelete.size() > 0)
                        userRepository.delete(toDelete.toArray(new UserInfo[]{}));

                    List<UserInfo> toUpdate = response.body().stream().filter(e -> !e.isDeleted()).collect(Collectors.toList());
                    if(toUpdate != null && toUpdate.size() > 0)
                        userRepository.insert(toUpdate.toArray(new UserInfo[]{}));

                    database.entityDAO().insert(entitySync);
                }
                if(completion !=null){
                    completion.onReady();
                }

            }
            @Override
            public void onFailure(Call<List<UserInfo>> call, Throwable t) {
                Toast.makeText(getApplication(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                if(completion !=null){
                    completion.onReady();
                }

            }
        });


    }

    private void syncPatients(EntitySync entitySync, List<PatientInfo> dirty, Completion completion) {
        SyncData<List<PatientInfo>> data = new SyncData<>();
        data.timestamp = entitySync.getLastSynced();
        data.data = dirty;
        entitySync.setLastSynced(System.currentTimeMillis());
        entitySync.setDirty(false);
        proxy.syncPatients(data).enqueue(new Callback<List<PatientInfo>>(){
                    @Override
                    public void onResponse(Call<List<PatientInfo>> call, Response<List<PatientInfo>> response) {

                        if (response.code() == 200){

                            List<PatientInfo> deleted = response.body().stream().filter(p -> p.isDeleted()).collect(Collectors.toList());
                            if(deleted!=null && deleted.size() > 0)
                                patientRepository.delete(deleted.toArray(new PatientInfo[]{}));

                            List<PatientInfo> updated = response.body().stream().filter(p -> !p.isDeleted()).collect(Collectors.toList());
                            updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                            if(updated!=null && updated.size() > 0)
                                patientRepository.insert(updated.toArray(new PatientInfo[]{}));



                            entityRepository.update(entitySync);
                            if(response.body().size() > 0) {
                                entityRepository.setDirty(DocumentInfo.class.getSimpleName());
                                entityRepository.setDirty(MedicationInfo.class.getSimpleName());
                                entityRepository.setDirty(LabInfo.class.getSimpleName());
                            }
                        }
                        if(completion !=null){
                            completion.onReady();
                        }

                    }
                    @Override
                    public void onFailure(Call<List<PatientInfo>> call, Throwable t) {
                        Toast.makeText(getApplication(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                        if(completion !=null){
                            completion.onReady();
                        }

                    }

                });


    }

    public void syncEncounters(EntitySync entitySync, List<EncounterInfo> dirty, Completion completion) {

        SyncData<List<EncounterInfo>> data = new SyncData<>();
        data.timestamp = entitySync.getLastSynced();
        data.data = dirty;
        entitySync.setLastSynced(System.currentTimeMillis());
        entitySync.setDirty(false);
        proxy.syncEncounters(data).enqueue(new Callback<List<EncounterInfo>>(){
            @Override
            public void onResponse(Call<List<EncounterInfo>> call, Response<List<EncounterInfo>> response) {

                if (response.code() == 200){

                    List<EncounterInfo> deleted = response.body().stream().filter(p -> p.isDeleted()).collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        encounterRepository.delete(deleted.toArray(new EncounterInfo[]{}));

                    List<EncounterInfo> updated = response.body().stream().filter(p -> !p.isDeleted()).collect(Collectors.toList());
                    updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                    if(updated!=null && updated.size() > 0)
                        encounterRepository.insert(updated.toArray(new EncounterInfo[]{}));


                    entityRepository.update(entitySync);


                }
                if(completion !=null){
                    completion.onReady();
                }

            }
            @Override
            public void onFailure(Call<List<EncounterInfo>> call, Throwable t) {

                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_LONG).show();

                if(completion !=null){
                    completion.onReady();
                }
            }
        });



    }

    public void syncSummaries(EntitySync entitySync, List<SummaryInfo> dirty, Completion completion) {
        SyncData<List<SummaryInfo>> data = new SyncData<>();

        data.timestamp = entitySync.getLastSynced();
        data.data = dirty;
        entitySync.setLastSynced(System.currentTimeMillis());
        entitySync.setDirty(false);
        proxy.syncSummaries(data).enqueue(new Callback<List<SummaryInfo>>(){
            @Override
            public void onResponse(Call<List<SummaryInfo>> call, Response<List<SummaryInfo>> response) {

                if (response.code() == 200){

                    List<SummaryInfo> deleted = response.body().stream().filter(s -> s.isDeleted()).collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        patientRepository.delete(deleted.toArray(new SummaryInfo[]{}));

                    List<SummaryInfo> updated = response.body().stream().filter(s -> !s.isDeleted()).collect(Collectors.toList());
                    updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                    if(updated!=null && updated.size() > 0)
                        patientRepository.updateSummaries(updated.toArray(new SummaryInfo[]{}));

                    entityRepository.update(entitySync);


                }
                if(completion !=null){
                    completion.onReady();
                }

            }
            @Override
            public void onFailure(Call<List<SummaryInfo>> call, Throwable t) {
                Toast.makeText(getApplication(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                if(completion !=null){
                    completion.onReady();
                }

            }
        });



    }

    public void syncMedications(EntitySync entitySync, List<MedicationInfo> dirty, Completion completion) {
        SyncData<List<MedicationInfo>> data = new SyncData<>();

        data.timestamp = entitySync.getLastSynced();
        data.data = dirty;
        entitySync.setLastSynced(System.currentTimeMillis());
        entitySync.setDirty(false);
        proxy.syncMedications(data).enqueue(new Callback<List<MedicationInfo>>(){
            @Override
            public void onResponse(Call<List<MedicationInfo>> call, Response<List<MedicationInfo>> response) {

                if (response.code() == 200){

                    List<MedicationInfo> deleted = response.body().stream().filter(s -> s.getDeleted()).collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        medicationRepository.delete(deleted.toArray(new MedicationInfo[]{}));

                    List<MedicationInfo> updated = response.body().stream().filter(s -> !s.getDeleted()).collect(Collectors.toList());
                    updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                    if(updated!=null && updated.size() > 0)
                        medicationRepository.insert(updated.toArray(new MedicationInfo[]{}));


                    entityRepository.update(entitySync);


                }
                if(completion !=null){
                    completion.onReady();
                }

            }
            @Override
            public void onFailure(Call<List<MedicationInfo>> call, Throwable t) {
                Toast.makeText(getApplication(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                if(completion !=null){
                    completion.onReady();
                }

            }
        });



    }

    public void syncLabs(EntitySync entitySync, List<LabInfo> dirty, Completion completion) {
        SyncData<List<LabInfo>> data = new SyncData<>();

        data.timestamp = entitySync.getLastSynced();
        data.data = dirty;
        entitySync.setLastSynced(System.currentTimeMillis());
        entitySync.setDirty(false);
        proxy.syncLabs(data).enqueue(new Callback<List<LabInfo>>(){
            @Override
            public void onResponse(Call<List<LabInfo>> call, Response<List<LabInfo>> response) {

                if (response.code() == 200){
                    List<LabInfo> deleted = response.body().stream().filter(s -> s.getDeleted()).collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        labRepository.delete(deleted.toArray(new LabInfo[]{}));

                    List<LabInfo> updated = response.body().stream().filter(s -> !s.getDeleted()).collect(Collectors.toList());
                    updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                    if(updated!=null && updated.size() > 0)
                        labRepository.insert(updated.toArray(new LabInfo[]{}));

                    entityRepository.update(entitySync);


                }
                if(completion !=null){
                    completion.onReady();
                }

            }
            @Override
            public void onFailure(Call<List<LabInfo>> call, Throwable t) {

                Toast.makeText(getApplication(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                if(completion !=null){
                    completion.onReady();
                }

            }
        });



    }

    public void syncDocs(EntitySync entitySync, List<DocumentInfo> dirty, Completion completion) {
        SyncData<List<DocumentInfo>> data = new SyncData<>();

        data.timestamp = entitySync.getLastSynced();
        data.data = dirty;
        entitySync.setLastSynced(System.currentTimeMillis());
        entitySync.setDirty(false);
        proxy.syncDocuments(data).enqueue(new Callback<List<DocumentInfo>>(){
            @Override
            public void onResponse(Call<List<DocumentInfo>> call, Response<List<DocumentInfo>> response) {

                if (response.code() == 200){
                    List<DocumentInfo> deleted = response.body().stream().filter(s -> s.getDeleted()).collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        documentRepository.delete(deleted.toArray(new DocumentInfo[]{}));

                    List<DocumentInfo> updated = response.body().stream().filter(s -> !s.getDeleted()).collect(Collectors.toList());
                    updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                    if(updated!=null && updated.size() > 0)
                        documentRepository.insert(updated.toArray(new DocumentInfo[]{}));

                    entityRepository.update(entitySync);


                }
                if(completion !=null){
                    completion.onReady();
                }

            }
            @Override
            public void onFailure(Call<List<DocumentInfo>> call, Throwable t) {

                Toast.makeText(getApplication(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                if(completion !=null){
                    completion.onReady();
                }

            }
        });



    }

    public void syncAppointments(EntitySync entitySync, List<AppointmentInfo> dirty, Completion completion) {
        SyncData<List<AppointmentInfo>> data = new SyncData<>();

        data.timestamp = entitySync.getLastSynced();
        data.data = dirty;
        entitySync.setLastSynced(System.currentTimeMillis());
        entitySync.setDirty(false);
        proxy.syncAppointments(data).enqueue(new Callback<List<AppointmentInfo>>(){
            @Override
            public void onResponse(Call<List<AppointmentInfo>> call, Response<List<AppointmentInfo>> response) {

                if (response.code() == 200){
                    List<AppointmentInfo> deleted = response.body().stream().filter(s -> s.isDeleted()).collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        appointmentRepository.delete(deleted.toArray(new AppointmentInfo[]{}));

                    List<AppointmentInfo> updated = response.body().stream().filter(s -> !s.isDeleted()).collect(Collectors.toList());
                    updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                    if(updated!=null && updated.size() > 0)
                        appointmentRepository.insert(updated.toArray(new AppointmentInfo[]{}));

                    entityRepository.update(entitySync);


                }
                if(completion !=null){
                    completion.onReady();
                }

            }
            @Override
            public void onFailure(Call<List<AppointmentInfo>> call, Throwable t) {
                Toast.makeText(getApplication(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                if(completion !=null){
                    completion.onReady();
                }

            }
        });



    }

    public void syncStatus(String uuid, int status) {
        SyncData<Map<String, Integer>> body = new SyncData<>();
        body.data = new HashMap<>();
        body.data.put(uuid, status);
        proxy.syncStatus(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                //Log.i("XXXXXXX", "xxok"+response.toString());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                //Log.i("XXXXXXX", "xxe"+t.getMessage());
            }
        });

    }

    public void getAvailableSupervisors() {
        proxy.getAvailableSupervisors().enqueue(new Callback<List<Supervisor>>() {
            @Override
            public void onResponse(Call<List<Supervisor>> call, Response<List<Supervisor>> response) {
                //Log.i("XXXXXXX", "xxok"+response.toString());
            }

            @Override
            public void onFailure(Call<List<Supervisor>> call, Throwable t) {
                t.printStackTrace();
                //Log.i("XXXXXXX", "xxe"+t.getMessage());
            }
        });
    }
    private void observFiles(){
        fileRepository.getFiles().observeForever(files -> {
            if (files != null) {

                files.forEach(uf -> {
                    if(System.currentTimeMillis() - uf.getLastTry() > 60*1000 ) {
                        File f = cache.getFile(uf.getFkey());
                        if (f.exists()) {
                            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), f);
                            MultipartBody.Part uuid = MultipartBody.Part.createFormData("doid", uf.getFkey());
                            MultipartBody.Part file = MultipartBody.Part.createFormData("file", uf.getFkey(), requestFile);
                            if(uf.getType() == 0) {
                                proxy.uploadAvatar(file, uuid).enqueue(new Callback<S3BucketUrl>() {
                                    @Override
                                    public void onResponse(Call<S3BucketUrl> call, Response<S3BucketUrl> response) {

                                        if (response.code() == 200) {
                                            cache.remove(uf.getFkey());
                                            fileRepository.remove(uf.getFkey());
                                        } else {

                                            uf.setLastTry(System.currentTimeMillis());
                                            uf.setTries(uf.getTries() + 1);
                                            fileRepository.insert(uf);
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<S3BucketUrl> call, Throwable t) {
                                        t.printStackTrace();
                                        uf.setLastTry(System.currentTimeMillis());
                                        uf.setTries(uf.getTries() + 1);
                                        database.fileDAO().update(uf);

                                    }
                                });
                            }else {
                                proxy.uploadDocument(file, uuid).enqueue(new Callback<S3BucketUrl>() {
                                    @Override
                                    public void onResponse(Call<S3BucketUrl> call, Response<S3BucketUrl> response) {

                                        if (response.code() == 200) {
                                            cache.remove(uf.getFkey());
                                            fileRepository.remove(uf.getFkey());
                                        } else {

                                            uf.setLastTry(System.currentTimeMillis());
                                            uf.setTries(uf.getTries() + 1);
                                            fileRepository.insert(uf);
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<S3BucketUrl> call, Throwable t) {
                                        t.printStackTrace();
                                        uf.setLastTry(System.currentTimeMillis());
                                        uf.setTries(uf.getTries() + 1);
                                        database.fileDAO().update(uf);

                                    }
                                });
                            }
                        } else {
                            database.fileDAO().delete(uf);
                        }
                    }
                });
            }
        });
    }


    private EntitySync getEntity(Class<?> beanClass){
        assert (beanClass !=null);
        EntitySync e = database.entityDAO().getEntitySync(beanClass.getSimpleName().toLowerCase());
        if(e== null){
            e = new EntitySync();
            e.setEntity(beanClass.getSimpleName().toLowerCase());
            e.setDirty(true);
            e.setLastSynced(0L);
        }
        return e;
    }

    private void init(UserAccountInfo accountInfo){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .readTimeout(300, TimeUnit.SECONDS)
                            .connectTimeout(300, TimeUnit.SECONDS)
                            .addInterceptor(chain -> {

                                if (accountInfo.getJwtToken() != null) {
                                    Request request = chain
                                            .request()
                                            .newBuilder()
                                            .addHeader("Authorization", accountInfo.getJwtToken())
                                            .build();
                                    return chain.proceed(request);
                                }
                                return chain.proceed(chain.request());
                            }).build();

        proxy =  new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(Utils.getGsonBuilder()))
                .baseUrl(BuildConfig.SERVER_URL)
                .build().create(Proxy.class);


    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void unSubscribe(int account) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseChannels.NURSES_OF+account);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseChannels.PHYSISTS_OF+account);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseChannels.ACCOUNT+account);
    }



}

