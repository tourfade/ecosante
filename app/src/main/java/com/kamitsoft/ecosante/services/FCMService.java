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
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.client.patient.PatientActivity;
import com.kamitsoft.ecosante.database.KsoftDatabase;
import com.kamitsoft.ecosante.model.EncounterInfo;
import com.kamitsoft.ecosante.model.EntitySync;
import com.kamitsoft.ecosante.model.repositories.EntityRepository;

import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class FCMService extends FirebaseMessagingService  {

    private LocalBroadcastManager lbm;

    public  static String CAT_PAYMENT_SUCCESS = "paymentsuccess";
    public  static String CAT_PAYMENT_FAILED = "paymentfailed";
    public  static String ACTION_PAYMENT = "payment";
    public  static String ACTION_SYNC_REQUEST = "syncRequest";
    public  static String CAT_SYNC_ALL = "paymentfailed";
    private EntityRepository entityRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        lbm = LocalBroadcastManager.getInstance(getApplicationContext());
        EcoSanteApp app = (EcoSanteApp) getApplication();
        entityRepository = new EntityRepository(app);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if(Boolean.parseBoolean(data.getOrDefault("syncPayment", "false"))){
            Intent msg = new Intent(ACTION_PAYMENT);
            msg.addCategory(CAT_PAYMENT_SUCCESS);
            lbm.sendBroadcast(msg);
            return;
        }

        if(Boolean.parseBoolean(data.getOrDefault("syncRequest", "false"))){
            String entity = data.get("entity");
            entityRepository.setDirty(entity);
            Log.i("XXXX -->en", entity);
        }
        RemoteMessage.Notification remote = remoteMessage.getNotification();
        if(remote!=null && remote.getTitle().trim().length() > 0){
            notify(remote, data);
        }

    }

    @Override
    public void onNewToken(String token) {
        Log.i("XXXXXXX->", token);
    }


    private void notify(RemoteMessage.Notification remNot, Map<String, String> data) {
        String patUUID = data==null? null:data.get("patientUUID");
        Intent intent = new Intent(this, patUUID==null? EcoSanteApp.class:PatientActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, EcoSanteApp.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ksoft)
                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.encounters))
                        .setContentTitle(remNot.getTitle())
                        .setContentText(remNot.getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setVibrate(new long[] { 500, 500})
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0 , notificationBuilder.build());
    }




}