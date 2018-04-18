package com.guineatech.CareC;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by CAHans on 2018/4/10.
 */

public class FcmInstancelIDService extends FirebaseInstanceIdService {
    public static String TAG = "FCM";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Token = " + token);
    }
}
