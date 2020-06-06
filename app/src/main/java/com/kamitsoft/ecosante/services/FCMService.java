/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kamitsoft.ecosante.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.EcoSanteActivity;
import com.kamitsoft.ecosante.client.patient.Encounter;
import com.kamitsoft.ecosante.client.patient.PatientActivity;
import com.kamitsoft.ecosante.constant.UserStatusConstant;
import com.kamitsoft.ecosante.constant.UserType;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.repositories.EntityRepository;
import com.kamitsoft.ecosante.model.repositories.UsersRepository;

import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class FCMService extends FirebaseMessagingService  {



    public  static String CAT_PAYMENT_SUCCESS = "paymentsuccess";
    public  static String CAT_PAYMENT_FAILED = "paymentfailed";
    public  static String ACTION_PAYMENT = "payment";
    public  static String ACTION_SYNC_REQUEST = "syncRequest";
    public  static String CAT_SYNC_ALL = "paymentfailed";
    private EntityRepository entityRepository;
    private UsersRepository userRepository;
    private EcoSanteApp app;
    private UserInfo currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        app = (EcoSanteApp) getApplication();
        entityRepository = new EntityRepository(app);
        userRepository = new UsersRepository(app);

    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        currentUser = userRepository.getConnected();
        if(currentUser == null || currentUser.getUuid().equals(data.get("emitter"))){
            return;
        }
        if(Boolean.parseBoolean(data.getOrDefault("syncPayment", "false"))){
            Intent msg = new Intent(ACTION_PAYMENT);
            msg.addCategory(CAT_PAYMENT_SUCCESS);
            return;
        }

        if(Boolean.parseBoolean(data.getOrDefault("syncRequest", "false"))){
            String entity = data.get("entity");
            entityRepository.setDirty(entity);

        }
        if(Boolean.parseBoolean(data.getOrDefault("updateRequest", "false"))){
            String uuid = data.get("uuid");
            int status = Integer.parseInt(data.get("status"));
            userRepository.remoteUpdateStatus(uuid,status);
        }
        RemoteMessage.Notification remote = remoteMessage.getNotification();
        if(remote != null && remote.getTitle().trim().length() > 0){
            notify(remote, data);
        }

    }


    @Override
    public void onNewToken(String token) {
        Log.i("XXXXXXX->", token);
    }

    private void notify(RemoteMessage.Notification remNot, Map<String, String> data) {
        notify(remNot,data , new long[] { 500, 500});
    }
    private void notify(RemoteMessage.Notification remNot, Map<String, String> data,  long[] vibes) {
        FutureTarget<Bitmap> futureTarget = Glide.with(this)
                .asBitmap()
                .load(BuildConfig.AVATAR_BUCKET+remNot.getIcon())
                .submit();
        Bitmap thumb = null;
        try {
             thumb = futureTarget.get();
        }catch (Exception e){
            thumb = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.encounters);
        }

        Intent intent = new Intent(this, EcoSanteActivity.class);

        if(data!=null && data.get("euuid")!=null && EncounterInfo.class.getSimpleName().equalsIgnoreCase(data.get("entity"))){
            intent = new Intent(this, Encounter.class);
            intent.putExtra("euuid", data.get("euuid"));
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, EcoSanteApp.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ksoft)
                        .setLargeIcon(thumb)
                        .setContentTitle(remNot.getTitle())
                        .setContentText(remNot.getBody())
                        .setAutoCancel(true)
                        //.setWhen(remNot.getEventTime())
                        .setSound(defaultSoundUri)
                        .setVibrate(vibes)
                        .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0 , notificationBuilder.build());
    }




}