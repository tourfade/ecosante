package com.kamitsoft.ecosante.client.user.subscription;


import com.kamitsoft.ecosante.client.user.subscription.order.IOrder;
import com.kamitsoft.ecosante.client.user.subscription.order.RedirectResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by tourfade on 18-07-11.
 */

public interface PayDunyaProxy {

    String PD_BASE_URL = "https://app.paydunya.com/api/v1/";

    @Headers({// test env
            "Content-Type: application/json",
            "PAYDUNYA-MASTER-KEY:lwv0dQYf-jRG7-KE21-TKLf-P6zxBJajtz48",
            "PAYDUNYA-PRIVATE-KEY:live_private_38QeUrNsKEltnGjVXkXXe0ViFZj",
            "PAYDUNYA-TOKEN:sydbXIGkqFu8ptM6yXes" })
    @POST("checkout-invoice/create")
    Call<RedirectResponse> createInvoce(@Body IOrder eOrder);


}
