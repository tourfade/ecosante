package com.kamitsoft.ecosante.services;

/**
 * Created by hassa on 06/07/2018.
 */


import com.kamitsoft.ecosante.model.PhysicianInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OracleProxy {
    @GET("api/user/search/{q}")
    Call<List<PhysicianInfo>> search(@Path("q") String key);

}