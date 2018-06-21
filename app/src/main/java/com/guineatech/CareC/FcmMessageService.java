package com.guineatech.CareC;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by CAHans on 2018/4/10.
 */

public class FcmMessageService extends FirebaseMessagingService {
    public static String TAG = "FCM";
    public static String tp = "", Did = "";
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived:" + remoteMessage.getFrom());
// Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("R", "Message data payload: " + remoteMessage.getData());
            Did = "";
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                // scheduleJob();
/*

              */

                if (remoteMessage.getData() != null)
                    if (remoteMessage.getData().get("type").equals("CU"))
                {

                 Intent   intent = new Intent(FcmMessageService.this, ClockActivity.class);
                    intent.putExtra("type", remoteMessage.getData().get("type"));
                    tp = remoteMessage.getData().get("type");
                    Did = remoteMessage.getData().get("id");
                   PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);                //時間
                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
                } else if (remoteMessage.getData().get("type").equals("B")) {
                        Intent intent = new Intent(FcmMessageService.this, ClockActivity.class);
                        intent.putExtra("type", remoteMessage.getData().get("type"));

                        tp = remoteMessage.getData().get("type");
                        Did = remoteMessage.getData().get("id");

                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);                //時間
                        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
                    } else {
                    Log.e("F", remoteMessage.getData().size() + "");
                    sendNotification(remoteMessage.getData());
                }


            } else {
                // Handle message within 10 seconds
                // handleNow();
            }
            Log.e("FF", remoteMessage.getData().get("type"));
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e("P", "BYPASS");
            Log.d("R", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }

    private void sendNotification(Map<String,String> messageBody) {
        Log.e("P", "BYPASS");
        Intent intent = new Intent(this, Sign_Rerister.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_xxxhdpi)
                .setContentTitle(messageBody.get("title"))
                .setContentText(messageBody.get("body"))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                ;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        //cott++;
    }
}