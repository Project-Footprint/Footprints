package com.footprints.footprints.controllers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.footprints.footprints.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseForgroundService extends FirebaseMessagingService {
    public static boolean isAtMainActivity = false;

    @SuppressLint("WrongConstant")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        /*Log.d("TokenId", "onMessageReceived");*/


        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String clickAction = remoteMessage.getNotification().getClickAction();
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

      /*  String to = remoteMessage.getTo();
        String collapseKey = remoteMessage.getCollapseKey();
        String messageId = remoteMessage.getMessageId();
        String messageType = remoteMessage.getMessageType();
        String color = remoteMessage.getNotification().getColor();
        String sound = remoteMessage.getNotification().getSound();
        String tag = remoteMessage.getNotification().getTag();
        String link = String.valueOf(remoteMessage.getNotification().getLink());*/

        Intent intent = new Intent(clickAction);
        intent.putExtra("isNotification", "true");

        PendingIntent resultPending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("footprints_like_notification", "Like Channel ", NotificationManager.IMPORTANCE_MAX);
            mNotificationManager.createNotificationChannel(mChannel);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            mChannel.enableVibration(true);
            mChannel.setShowBadge(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            Notification.Builder mBuilder =

                    new Notification.Builder(this, "footprints_like_notification")
                            .setSmallIcon(R.drawable.icon_comment)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setColor(Color.WHITE)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setAutoCancel(true);

            mBuilder.setChannelId(mChannel.getId());
            int notificatonId = (int) System.currentTimeMillis();
            mBuilder.setContentIntent(resultPending);
            mNotificationManager.notify(notificatonId, mBuilder.build());
        } else {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.icon_comment)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setSound(sound)
                            .setColor(Color.WHITE)
                            .setAutoCancel(true)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setVibrate(new long[]{Notification.DEFAULT_VIBRATE})
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);


            mBuilder.setContentIntent(resultPending);
            int notificatonId = (int) System.currentTimeMillis();
            mNotificationManager.notify(notificatonId, mBuilder.build());
        }

      /*  Log.d("TokenId", title+" title");
        Log.d("TokenId", body+" body");
        Log.d("TokenId", clickAction+" Click Action");
        Log.d("TokenId", to+" to");
        Log.d("TokenId", collapseKey+" collapseKey");
        Log.d("TokenId", messageId+" messageId");
        Log.d("TokenId", messageType+" messageType");
        Log.d("TokenId", sound+" sound");
        Log.d("TokenId", tag+" tag");
        Log.d("TokenId", link+" link");
        Log.d("TokenId", color+" color");
        Log.d("TokenId", remoteMessage.getData().get("questionTitle")+" questionTitle");
        Log.d("TokenId", remoteMessage.getData().get("checkData")+" checkData");*/


    }
}
