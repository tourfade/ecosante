package com.kamitsoft.ecosante.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.DiskCache;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.model.Act;
import com.kamitsoft.ecosante.model.Analysis;
import com.kamitsoft.ecosante.model.Drug;
import com.kamitsoft.ecosante.model.EntitySync;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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


public class WorkerService extends IntentService {


    private KsoftDatabase database;
    private EcoSanteApp app;
    private Proxy proxy;
    private DiskCache cache;


    public WorkerService() {
        super("Worker Dmi");
    }
    public WorkerService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = (EcoSanteApp) getApplication();
        init(getApplicationContext());
    }
    private void init(Context context){
        database = KsoftDatabase.getInstance(context);
        app = (EcoSanteApp) context.getApplicationContext();
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(300, TimeUnit.SECONDS)
                .connectTimeout(300, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    String h = app.getCurrentUser().getUuid();
                    if( h != null) {
                        Request request = chain
                                .request()
                                .newBuilder()
                                .addHeader("Authorization", h)
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

        cache = new DiskCache(context);
    }
    private Gson getGsonBuilder(){
        return new GsonBuilder() //"2019-06-06 21:25:52"
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();
    }
    private  void updateSync(EntitySync entity){
        entity.setLastSynced(System.currentTimeMillis());
        database.entityDAO().insert(entity);
    }
    private EntitySync getEntity(Class<?> beanClass){
        EntitySync e = database.entityDAO().getEntitySync(beanClass.getSimpleName().toLowerCase());
        if(e== null){
            e = new EntitySync();
            e.setEntity(beanClass.getName());
            e.setLastSynced(0L);
        }

        return e;
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        syncDrugs();
        syncActs();
        syncAnalysis();
        syncFiles();
    }


    public void syncDrugs(){
        EntitySync entity = getEntity(Drug.class);
        proxy.syncDrugs(entity.getLastSynced()).enqueue(new Callback<List<Drug>>() {
            @Override
            public void onResponse(Call<List<Drug>> call, Response<List<Drug>> response) {
                switch (response.code()){
                    case 200:
                        List<Drug> drugs = response.body();
                        database.drugDAO().insert(drugs.toArray(new Drug[]{}));
                        updateSync(entity);

                    break;
                }
            }
            @Override
            public void onFailure(Call<List<Drug>> call, Throwable t) {
                t.printStackTrace();
                Log.i("XXXXX", "syncDrugs error->"+t.getMessage());

            }
        });


    }
    public void syncActs( ){
        EntitySync entity = getEntity(Act.class);
        proxy.syncActs(entity.getLastSynced()).enqueue(new Callback<List<Act>>() {
            @Override
            public void onResponse(Call<List<Act>> call, Response<List<Act>> response) {
                switch (response.code()){
                    case 200:
                        List<Act> acts = response.body();
                        database.actDAO().insert(acts.toArray(new Act[]{}));
                        updateSync(entity);
                        break;
                }
            }
            @Override
            public void onFailure(Call<List<Act>> call, Throwable t) {
                t.printStackTrace();
                Log.i("XXXXX", "syncActs error->"+t.getMessage());

            }
        });
    }
    public void syncAnalysis(){
        EntitySync entity = getEntity(Analysis.class);
        proxy.syncAnalysis(entity.getLastSynced()).enqueue(new Callback<List<Analysis>>() {
            @Override
            public void onResponse(Call<List<Analysis>> call, Response<List<Analysis>> response) {
                switch (response.code()){
                    case 200:
                        List<Analysis> analyses = response.body();
                        database.analysisDAO().insert(analyses.toArray(new Analysis[]{}));
                        updateSync(entity);
                        break;
                }
            }
            @Override
            public void onFailure(Call<List<Analysis>> call, Throwable t) {
                t.printStackTrace();
                Log.i("XXXXX", "syncAnalysis error->"+t.getMessage());

            }
        });
    }


    public void syncFiles(){
        List<UnsyncFile> files = database.fileDAO().allAvatars();
        if(files != null) {
            files.forEach(uf -> {
                File f = cache.getFile(uf.getFkey());
                if (f.exists()) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), f);
                    MultipartBody.Part uuid = MultipartBody.Part.createFormData("uuid", uf.getFkey());
                    MultipartBody.Part file = MultipartBody.Part.createFormData("file", uf.getFkey(), requestFile);


                    proxy.uploadAvatar(file, uuid).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            if (response.code() == 200) {
                                cache.remove(uf.getFkey());
                                database.fileDAO().delete(uf);
                            } else {
                                uf.setLastTry(System.currentTimeMillis());
                                uf.setTries(uf.getTries() + 1);
                                database.fileDAO().update(uf);
                            }

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            uf.setLastTry(System.currentTimeMillis());
                            uf.setTries(uf.getTries() + 1);
                            database.fileDAO().update(uf);

                        }
                    });
                } else {
                    database.fileDAO().delete(uf);
                }
            });
        }

        files = database.fileDAO().allFiles();
        if(files != null) {
            files.forEach(uf -> {
                File f = cache.getFile(uf.getFkey());
                if(f.exists()){
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"),f );
                    MultipartBody.Part uuid = MultipartBody.Part. createFormData("uuid", uf.getFkey());
                    MultipartBody.Part file = MultipartBody.Part.createFormData("file", uf.getFkey(), requestFile);


                    proxy.uploadDocument(file, uuid).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            if (response.code() == 200){
                                cache.remove(uf.getFkey());
                                database.fileDAO().delete(uf);
                            }else{
                                uf.setLastTry(System.currentTimeMillis());
                                uf.setTries(uf.getTries()+1);
                                database.fileDAO().update(uf);
                            }

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            uf.setLastTry(System.currentTimeMillis());
                            uf.setTries(uf.getTries()+1);
                            database.fileDAO().update(uf);

                        }
                    });
                }else {
                    database.fileDAO().delete(uf);
                }
            });
        }
    }



}

