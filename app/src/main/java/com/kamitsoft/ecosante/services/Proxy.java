package com.kamitsoft.ecosante.services;

/**
 * Created by hassa on 06/07/2018.
 */



import com.kamitsoft.ecosante.model.Act;
import com.kamitsoft.ecosante.model.Analysis;
import com.kamitsoft.ecosante.model.AppointmentInfo;
import com.kamitsoft.ecosante.model.Avatar;
import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.Drug;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.LabInfo;
import com.kamitsoft.ecosante.model.MedicationInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.PhysNursPat;
import com.kamitsoft.ecosante.model.SummaryInfo;
import com.kamitsoft.ecosante.model.SyncData;
import com.kamitsoft.ecosante.model.UserAccountInfo;
import com.kamitsoft.ecosante.model.UserInfo;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Proxy {


    @POST("login")
    Call<UserAccountInfo> login(@Body AuthenticationInfo auth);

    @POST("api/summary/sync/")
    Call<List<SummaryInfo>> syncSummaries(@Body SyncData<List<SummaryInfo>> body);

    @POST("api/encounter/sync/")
    Call<List<EncounterInfo>> syncEncounters(@Body SyncData<List<EncounterInfo>> body);

    @POST("api/document/sync/")
    Call<List<DocumentInfo>> syncDocuments(@Body SyncData<List<DocumentInfo>> body);

    @POST("api/lab/sync/")
    Call<List<LabInfo>> syncLabs(@Body SyncData<List<LabInfo>> body);

    @POST("api/medication/sync/")
    Call<List<MedicationInfo>> syncMedications(@Body SyncData<List<MedicationInfo>> body);

    @POST("api/appointment/sync/")
    Call<List<AppointmentInfo>> syncAppointments(@Body SyncData<List<AppointmentInfo>> body);

    @POST("api/patient/sync/")
    Call<List<PatientInfo>> syncPatients(@Body SyncData<List<PatientInfo>> body);

    @POST("api/user/sync/")
    Call<List<UserInfo>> syncUser(@Body SyncData<List<UserInfo>> body);



    @Multipart
    @POST("api/document/upload")
    Call<String> uploadDocument(@Part MultipartBody.Part file, @Part MultipartBody.Part uuid);
//----------Worker--------------
    @GET("api/data/drugs/sync/{timestamp}")
    Call<List<Drug>> syncDrugs(@Path("timestamp") long timestamp);

    @GET("api/data/analysis/sync/{timestamp}")
    Call<List<Analysis>> syncAnalysis(@Path("timestamp") long timestamp);

    @GET("api/data/acts/sync/{timestamp}")
    Call<List<Act>> syncActs(@Path("timestamp") long timestamp);

    @Multipart
    @POST("api/avatar/upload")
    Call<Void> uploadAvatar(MultipartBody.Part file, MultipartBody.Part uuid);

    @POST("useraccount/sign-up")
    Call<Void>  createAccount(@Body UserAccountInfo accountInfo);
}