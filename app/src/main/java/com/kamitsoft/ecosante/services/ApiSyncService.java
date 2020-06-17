package com.kamitsoft.ecosante.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.DiskCache;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.constant.PrescriptionType;
import com.kamitsoft.ecosante.constant.StatusConstant;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.dto.PrescriptionDTO;
import com.kamitsoft.ecosante.model.AppointmentInfo;
import com.kamitsoft.ecosante.model.DistrictInfo;
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
import com.kamitsoft.ecosante.model.repositories.DistrictRepository;
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
    private DistrictRepository districtRepository;




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

    public void reset(UserAccountInfo accountInfo){
        userRepository.reset(accountInfo);
        encounterRepository.reset(accountInfo);
        patientRepository.reset();
        appointmentRepository.reset(accountInfo);
        entityRepository.reset();
    }

    private void observ() {
        userRepository.getLiveAccount().observeForever(accountInfo->{
            if(accountInfo != null) {
                init(accountInfo);
            }
        });


        encounterRepository.getDirty().observeForever(toSynch -> {
            if(proxy == null ){
                return;
            }
            if(toSynch.size() > 0)
                syncEncounters(getEntity(EncounterInfo.class), toSynch, null);
        });

        patientRepository.getDirty().observeForever(toSynch -> {
            if(proxy == null){
                return;
            }

            if(toSynch.size() > 0)
                syncPatients(getEntity(PatientInfo.class),toSynch, null);

        });

        userRepository.getDirty().observeForever(toSynch->{
            if(toSynch.size() > 0)
                syncUsers(getEntity(UserInfo.class), toSynch, null);
        });

        patientRepository.getDirtySummaries().observeForever(toSynch -> {
            if(proxy == null){
                return;
            }

            if(toSynch.size() > 0)
                syncSummaries(getEntity(SummaryInfo.class), toSynch, null);
        });

        medicationRepository.dirty().observeForever(toSynch -> {
            if(proxy == null){
                return;
            }


            if(toSynch.size() > 0)
                syncMedications(getEntity(MedicationInfo.class), toSynch, null);
        });

        labRepository.getDirty().observeForever(toSynch -> {
            if(proxy == null){
                return;
            }


            if(toSynch.size() > 0)
                syncLabs(getEntity(LabInfo.class), toSynch, null);
        });

        documentRepository.getDirty().observeForever(toSynch -> {
            if(proxy == null){
                return;
            }
            if(toSynch.size() > 0)
                syncDocs(getEntity(DocumentInfo.class), toSynch, null);


        });

        appointmentRepository.getDirty().observeForever(toSynch -> {
            if(proxy == null){
                return;
            }

            if(toSynch.size() > 0)
                syncAppointments(getEntity(AppointmentInfo.class), toSynch, null);
        });

        districtRepository.getDirty().observeForever(toSynch -> {
            if(proxy == null || toSynch == null){
                return;
            }
            if(toSynch.size() > 0)
                syncDistricts(getEntity(DistrictInfo.class), toSynch, null);


        });

    }

    public void requestSync(Class<?> beanClass, Completion completion) {
        if(beanClass == null || proxy == null){
            if(completion !=null){
                completion.onReady();
            }
            return;
        }
        EntitySync entitySync = getEntity(beanClass);
        if(entitySync == null){
           entitySync = getEntity(beanClass);
        }
        if(System.currentTimeMillis() - entitySync.getLastSynced() <= 2*1000){
            if(completion !=null){
                completion.onReady();
            }

            return;
        }
        //entitySync.setLastSynced(entitySync.getLastSynced()-1000);
        if(beanClass == PatientInfo.class ){
            syncPatients(entitySync, new ArrayList<>(),completion);
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
        if(beanClass == DistrictInfo.class){
            if(entitySync.isDirty())
                syncDistricts(entitySync,new ArrayList<>(),completion);
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
        districtRepository = new DistrictRepository(app);

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
        entitySync.setDirty(false);
        proxy.syncUser(data).enqueue(new Callback<List<UserInfo>>(){
            @Override
            public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {

                if (response.code() == 200){
                    entitySync.setLastSynced(System.currentTimeMillis());
                    entityRepository.update(entitySync);
                    List<UserInfo> toDelete = response.body().stream()
                            .filter(e -> {
                                e.setUpdatedAt(new Timestamp(data.timestamp));
                                return e.isDeleted();})
                            .collect(Collectors.toList());

                    if(toDelete != null && toDelete.size() > 0) {
                       userRepository.delete(toDelete.toArray(new UserInfo[]{}));
                    }

                    List<UserInfo> toUpdate = response.body().stream()
                            .filter(e -> {
                                e.setUpdatedAt(new Timestamp(data.timestamp));
                                return !e.isDeleted();})
                            .collect(Collectors.toList());
                    if(toUpdate != null && toUpdate.size() > 0)
                        userRepository.insert(toUpdate.toArray(new UserInfo[]{}));

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
        entitySync.setDirty(false);
        proxy.syncPatients(data).enqueue(new Callback<List<PatientInfo>>(){
                    @Override
                    public void onResponse(Call<List<PatientInfo>> call, Response<List<PatientInfo>> response) {
                        if (response.code() == 200){
                            entitySync.setLastSynced(System.currentTimeMillis());
                            entityRepository.update(entitySync);

                            List<PatientInfo> deleted = response.body()
                                    .stream()
                                    .filter((s) ->{
                                        s.setUpdatedAt(new Timestamp(data.timestamp));
                                        return s.isDeleted(); })
                                    .collect(Collectors.toList());
                            if(deleted!=null && deleted.size() > 0) {
                                patientRepository.delete(deleted.toArray(new PatientInfo[]{}));
                            }

                            List<PatientInfo> updated = response.body().stream()
                                    .filter((s) ->{
                                        s.setUpdatedAt(new Timestamp(data.timestamp));
                                        return !s.isDeleted(); })
                                    .collect(Collectors.toList());
                            updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                            if(updated!=null && updated.size() > 0)
                                patientRepository.insert(updated.toArray(new PatientInfo[]{}));




                            if(updated!=null && updated.size() > 0) {
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
        data.data = dirty;
        data.timestamp = entitySync.getLastSynced();
        entitySync.setDirty(false);
        proxy.syncEncounters(data).enqueue(new Callback<List<EncounterInfo>>(){
            @Override
            public void onResponse(Call<List<EncounterInfo>> call, Response<List<EncounterInfo>> response) {

                if (response.code() == 200){
                    entitySync.setLastSynced(System.currentTimeMillis());
                    entityRepository.update(entitySync);
                    List<EncounterInfo> deleted = response.body().stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return s.isDeleted() || s.currentStatus().status == StatusConstant.ARCHIVED.status; })
                            .collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        encounterRepository.delete(deleted.toArray(new EncounterInfo[]{}));

                    List<EncounterInfo> updated = response.body().stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return !s.isDeleted(); })
                            .collect(Collectors.toList());
                    updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                    if(updated!=null && updated.size() > 0)
                        encounterRepository.insert(updated.toArray(new EncounterInfo[]{}));

                    requestSync(PatientInfo.class, null);



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
        entitySync.setDirty(false);
        proxy.syncSummaries(data).enqueue(new Callback<List<SummaryInfo>>(){
            @Override
            public void onResponse(Call<List<SummaryInfo>> call, Response<List<SummaryInfo>> response) {

                if (response.code() == 200){
                    entitySync.setLastSynced(System.currentTimeMillis());

                    entityRepository.update(entitySync);
                    List<SummaryInfo> deleted = response.body().stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return s.isDeleted(); })
                            .collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        patientRepository.delete(deleted.toArray(new SummaryInfo[]{}));

                    List<SummaryInfo> updated = response.body().stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return !s.isDeleted(); })
                            .collect(Collectors.toList());
                    updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                    if(updated!=null && updated.size() > 0)
                        patientRepository.updateSummaries(updated.toArray(new SummaryInfo[]{}));




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
        entitySync.setDirty(false);
        proxy.syncMedications(data).enqueue(new Callback<List<MedicationInfo>>(){
            @Override
            public void onResponse(Call<List<MedicationInfo>> call, Response<List<MedicationInfo>> response) {

                if (response.code() == 200){
                    entitySync.setLastSynced(System.currentTimeMillis());

                    entityRepository.update(entitySync);
                    List<MedicationInfo> deleted = response.body().stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return s.getDeleted(); })
                            .collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        medicationRepository.delete(deleted.toArray(new MedicationInfo[]{}));

                    List<MedicationInfo> updated = response.body().stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return !s.getDeleted(); })
                            .collect(Collectors.toList());
                    updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                    if(updated!=null && updated.size() > 0)
                        medicationRepository.insert(updated.toArray(new MedicationInfo[]{}));




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
        entitySync.setDirty(false);
        proxy.syncLabs(data).enqueue(new Callback<List<LabInfo>>(){
            @Override
            public void onResponse(Call<List<LabInfo>> call, Response<List<LabInfo>> response) {

                if (response.code() == 200){
                    entitySync.setLastSynced(System.currentTimeMillis());
                    entityRepository.update(entitySync);
                    List<LabInfo> deleted = response.body().stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return s.getDeleted(); })
                            .collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        labRepository.delete(deleted.toArray(new LabInfo[]{}));

                    List<LabInfo> updated = response.body().stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return !s.getDeleted(); })
                            .collect(Collectors.toList());
                    updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                    if(updated!=null && updated.size() > 0)
                        labRepository.insert(updated.toArray(new LabInfo[]{}));



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
        entitySync.setDirty(false);
        //Toast.makeText(getApplication(),"data "+ new Gson().toJson(data), Toast.LENGTH_LONG).show();

        proxy.syncDocuments(data).enqueue(new Callback<List<DocumentInfo>>(){
            @Override
            public void onResponse(Call<List<DocumentInfo>> call, Response<List<DocumentInfo>> response) {
                //Toast.makeText(getApplication(),"code "+response.code() + new Gson().toJson(response.body()), Toast.LENGTH_LONG).show();

                if (response.code() == 200){
                    entitySync.setLastSynced(System.currentTimeMillis());
                    entityRepository.update(entitySync);
                    List<DocumentInfo> deleted = response.body().stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return s.getDeleted(); })
                            .collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        documentRepository.delete(deleted.toArray(new DocumentInfo[]{}));

                    List<DocumentInfo> updated = response.body().stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return !s.getDeleted(); })
                            .collect(Collectors.toList());
                    updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                    if(updated!=null && updated.size() > 0)
                        documentRepository.insert(updated.toArray(new DocumentInfo[]{}));




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

    private void syncDistricts(EntitySync entitySync, List<DistrictInfo> dirty, Completion completion) {

        final SyncData<List<DistrictInfo>> data = new SyncData<>();
        data.timestamp = entitySync.getLastSynced();
        data.data = dirty;
        entitySync.setDirty(false);

        proxy.syncDistricts(data).enqueue(new Callback<List<DistrictInfo>>(){
            @Override
            public void onResponse(Call<List<DistrictInfo>> call, Response<List<DistrictInfo>> response) {

                if (response.code() == 200){
                    entitySync.setLastSynced(System.currentTimeMillis());

                    entityRepository.update(entitySync);
                    EntitySync e = entityRepository.getEntity(entitySync.getEntity());

                    List<DistrictInfo> deleted = response.body()
                            .stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return s.isDeleted(); })
                            .collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        districtRepository.syncDelete(deleted.toArray(new DistrictInfo[]{}));

                    List<DistrictInfo> updated = response.body()
                            .stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return !s.isDeleted(); })
                            .collect(Collectors.toList());

                    if(updated!=null && updated.size() > 0)
                        districtRepository.syncUpdate(updated.toArray(new DistrictInfo[]{}));

                }
                if(completion !=null){
                    completion.onReady();
                }

            }
            @Override
            public void onFailure(Call<List<DistrictInfo>> call, Throwable t) {
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
                    entityRepository.update(entitySync);
                    List<AppointmentInfo> deleted = response.body().stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return s.isDeleted(); })
                            .collect(Collectors.toList());
                    if(deleted!=null && deleted.size() > 0)
                        appointmentRepository.delete(deleted.toArray(new AppointmentInfo[]{}));

                    List<AppointmentInfo> updated = response.body().stream()
                            .filter((s) ->{
                                s.setUpdatedAt(new Timestamp(data.timestamp));
                                return s.isDeleted(); })
                            .collect(Collectors.toList());
                    updated.stream().forEach(p-> p.setUpdatedAt(new Timestamp(data.timestamp)));

                    if(updated!=null && updated.size() > 0)
                        appointmentRepository.insert(updated.toArray(new AppointmentInfo[]{}));




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

    public void getPageArchivedEncounters(int page, Completion completion) {
        proxy.getPageArchivedEncounters(page, 10).enqueue(new Callback<List<EncounterInfo>>() {
            @Override
            public void onResponse(Call<List<EncounterInfo>> call,
                                   Response<List<EncounterInfo>> response) {

                if (response.code() == 200 && response.body().size() > 0){
                    database.encounterDAO().insert(response.body().toArray(new EncounterInfo[]{}));
                }
                if(completion!= null){
                    completion.onReady();
                }
            }

            @Override
            public void onFailure(Call<List<EncounterInfo>> call, Throwable t) {
                t.printStackTrace();
                if(completion!= null){
                    completion.onReady();
                }
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
                                proxy.uploadAvatar(file, uuid).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {

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
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        t.printStackTrace();
                                        uf.setLastTry(System.currentTimeMillis());
                                        uf.setTries(uf.getTries() + 1);
                                        database.fileDAO().update(uf);

                                    }
                                });
                            }
                            if(uf.getType() == 1){
                                proxy.uploadDocument(file, uuid).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {

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
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        t.printStackTrace();
                                        uf.setLastTry(System.currentTimeMillis());
                                        uf.setTries(uf.getTries() + 1);
                                        database.fileDAO().update(uf);

                                    }
                                });
                            }
                            if(uf.getType() == 2){

                                proxy.uploadSignature(file, uuid).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {

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
                                    public void onFailure(Call<Void> call, Throwable t) {
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

    public void sendPrescription(PrescriptionType type, String euuid,  Map<String, String> des,Completion completion) {
        EncounterInfo encounter = encounterRepository.getEncounter(euuid);
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setPatientEmail(des.get("pat"));
        des.remove("pat");
        dto.setEmails(des.values().toArray(new String[]{}));
        dto.setEncounterUuid(encounter.getUuid());
        dto.setPatientUuid(encounter.getPatientUuid());
        dto.setPhysistUuid(encounter.getSupervisor().physicianUuid);
        dto.setPrescriptionType(type.ordinal());

        proxy.generatePrescription(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                completion.onReady();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Log.i("XXXXXXX", "xxe"+t.getMessage());
            }
        });

    }

    private EntitySync getEntity(Class<?> beanClass){
        assert (beanClass !=null);
        String name = beanClass.getSimpleName().toLowerCase();
        EntitySync e = entityRepository.getEntity(name);
        if(e== null){
            e = new EntitySync();
            e.setEntity(name);
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

