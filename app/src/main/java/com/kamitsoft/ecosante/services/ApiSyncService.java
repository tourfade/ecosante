package com.kamitsoft.ecosante.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.DiskCache;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.SyncData;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.repositories.EncountersRepository;
import com.kamitsoft.ecosante.model.repositories.PatientsRepository;
import com.kamitsoft.ecosante.model.repositories.UsersRepository;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.http2.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiSyncService extends Service {


    private EncountersRepository encounterRepository;
    private PatientsRepository patientRepository;
    private UsersRepository userRepository;


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



    private LocalBroadcastManager lbm;
    // Object to use as a thread-safe lock
    private static final Object lock = new Object();


    @Override
    public void onCreate() {
        app = (EcoSanteApp) getApplication();
        lbm = LocalBroadcastManager.getInstance(getApplicationContext());
        database = KsoftDatabase.getInstance(getApplicationContext());
        cache = new DiskCache(getApplicationContext());
        init(getApplicationContext());

        initRepositories();
        observ();


    }

    private void observ() {
        encounterRepository.getDirty().observeForever(encounterInfos -> {
            syncUserEncounters(encounterInfos);
        });
        patientRepository.getDirty().observeForever(patients -> {
            syncUserPatients(patients);
            if(patients.size() > 0)
                savePatient(patients.get(0));
        });
        userRepository.getConnectedUser().observeForever(user->{
            if(user != null) {
                init(getApplicationContext());
            }
        });
    }



    private void initRepositories() {
        encounterRepository = new EncountersRepository(app);
        patientRepository = new PatientsRepository(app);
        userRepository = new UsersRepository(app);

    }


    private void syncUserPatients(List<PatientInfo> dirty) {
        if(dirty!=null && dirty.size() == 0){ return;}
        SyncData<List<PatientInfo>> data = new SyncData<>();
        EntitySync entitySycn = getEntity(PatientInfo.class);
        data.timestamp = entitySycn.getLastSynced();
        data.data = dirty;
        proxy.syncPatients(data).enqueue(new Callback<List<PatientInfo>>(){
                    @Override
                    public void onResponse(Call<List<PatientInfo>> call, Response<List<PatientInfo>> response) {
                        Log.i("XXXXX1", response.toString());

                        if (response.code() == 200){



                        }

                    }
                    @Override
                    public void onFailure(Call<List<PatientInfo>> call, Throwable t) {
                        Log.i("XXXXX", t.toString()+"->"+t.getMessage());

                        Toast.makeText(getApplication(), R.string.unknown_error, Toast.LENGTH_LONG).show();


                    }
                });

        EntitySync entity = database.entityDAO().getEntitySync(PatientInfo.class.getSimpleName().toLowerCase());
        updateSync(entity);
    }
    public void syncUserEncounters(List<EncounterInfo> dirty) {
        if(dirty!=null && dirty.size() == 0){ return;}
        for(EncounterInfo ei:dirty){

            ei.setWeight(187);
            ei.setPressusreDiastolic(132);
            encounterRepository.insert(ei);
        }


        EntitySync entity = database.entityDAO().getEntitySync(EncounterInfo.class.getSimpleName().toLowerCase());
        updateSync(entity);

    }
    private EntitySync getEntity(Class<?> beanClass){
        return database.entityDAO().getEntitySync(beanClass.getSimpleName().toLowerCase());
    }
    private  void updateSync(EntitySync entity){
        entity.setLastSynced(System.currentTimeMillis());
        database.entityDAO().insert(entity);
    }
    private void init(Context context){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .readTimeout(300, TimeUnit.SECONDS)
                            .connectTimeout(300, TimeUnit.SECONDS)
                            .addInterceptor(chain -> {
                                String token = app.getConnectionToken();
                                if (token != null) {
                                    Request request = chain
                                            .request()
                                            .newBuilder()
                                            .addHeader("Authorization", token)
                                            .build();
                                    return chain.proceed(request);
                                }
                                return chain.proceed(chain.request());
                            }).build();

        proxy =  new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(getGsonBuilder()))
                .baseUrl(BuildConfig.SERVER_URL)
                .build().create(Proxy.class);


    }

    private Gson getGsonBuilder(){
        return new GsonBuilder() //"2019-06-06 21:25:52"
                .setDateFormat("yyyy-MM-dd HH:mm")
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void login(String account, String email, String password, final CompletionWithData<Boolean> completion ){
        proxy.login(new AuthenticationInfo(account, email, password))
                .enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {



                if (response.code() == 200){
                     app.setConnectionToken(response.headers().get("Authorization"));
                    subscribe(response.body());
                    userRepository.connect(response.body());
                    if(completion != null){
                        completion.onReady(true);
                    }
                }else {

                    if(response.code() == 401){
                        Toast.makeText(getApplication(), R.string.wrong_username_or_password, Toast.LENGTH_LONG).show();
                    }
                    if(completion != null){
                        completion.onReady(false);
                    }
                }

            }
            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                t.printStackTrace();

                Toast.makeText(getApplication(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                if(completion != null){
                    completion.onReady(false);
                }


            }
        });


    }

    public void savePatient(PatientInfo patientInfos){

        proxy.savePatients(patientInfos).enqueue(new Callback<PatientInfo>(){

            @Override
            public void onResponse(Call<PatientInfo> call, Response<PatientInfo> response) {
                Log.i("XXXXXX ok", "-->"+response.body());
            }

            @Override
            public void onFailure(Call<PatientInfo> call, Throwable t) {
                Log.i("XXXXXX err", "-->"+t.getMessage());
            }
        } );

    }
    public void subscribe(UserInfo userInfo) {
        switch (UserType.typeOf(userInfo.getUserType())){
            case PHYSIST:
            case UNDETERMINATED:
                Log.d("XXXXXX", "111");
                FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseChannels.NURSES_OF+userInfo.getAccountID());
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(FirebaseChannels.PHYSISTS_OF+userInfo.getAccountID())
                        .addOnCompleteListener(task -> {
                            String msg = "Ecoute des medecins";
                            if (!task.isSuccessful()) {
                                msg = "Echec de l'Ecoute des medecins";;
                            }
                            Log.d("XXXXXX", msg);

                        });
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(FirebaseChannels.PHYSIST+userInfo.getUuid())
                        .addOnCompleteListener(task -> {
                            String msg = "Ecoute du canal du medecin";
                            if (!task.isSuccessful()) {
                                msg = "Echec de Ecoute du canal du medecin";;
                            }
                            Log.d("XXXXXX", msg);

                        });;
                break;
            case NURSE:
                Log.d("XXXXXX", "222");
                FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseChannels.PHYSISTS_OF+userInfo.getAccountID());
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseChannels.NURSES_OF+userInfo.getAccountID());
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseChannels.NURSE+userInfo.getUuid());
                break;
            case ADMIN:
                Log.d("XXXXXX", "333");
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseChannels.PHYSISTS_OF+userInfo.getAccountID());
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseChannels.NURSES_OF+userInfo.getAccountID());
                break;

        }

    }

    public void unSubscribe(int account) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseChannels.NURSES_OF+account);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseChannels.PHYSISTS_OF+account);


    }

    public void uploadFile(final String key, final Completion lamda){
        File f = cache.getFile(key);
        if(!f.exists()){
            if(lamda != null){
                lamda.onReady();
            }
            return;
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"),f );
        MultipartBody.Part uuid = MultipartBody.Part. createFormData("uuid", key);
        MultipartBody.Part file = MultipartBody.Part.createFormData("file", key, requestFile);


        proxy.uploadAvatar(file, uuid).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200){
                    cache.remove(key);
                }
                if(lamda != null){
                    lamda.onReady();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if(lamda != null){
                    lamda.onReady();
                }

            }
        });

    }



}

