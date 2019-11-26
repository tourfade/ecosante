package com.kamitsoft.ecosante.client.user.subscription.order;


import com.kamitsoft.ecosante.client.user.subscription.Subscription;
import com.kamitsoft.ecosante.model.UserInfo;

public class CustomData {

   private String email;
   private long id;
   private String phone;
   private String subscription_uuid;



    public CustomData(UserInfo userInfos, Subscription subscription) {
        email = userInfos.getEmail();
        id = userInfos.getUserID();
        phone = userInfos.getMobilePhone();
        subscription_uuid = subscription.uuid;
    }
}
