package com.guineatech.CareC;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by CAHans on 2018/4/10.
 */

public class FcmMessageService extends FirebaseMessagingService {
    public static String TAG = "FCM";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived:" + remoteMessage.getFrom());
    }
}