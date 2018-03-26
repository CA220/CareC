package com.guineatech.CareC;

/**
 * Created by CAHans on 2018/3/11.
 */

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.KeyStore;


//繼承android.app.Service
public class backgroundservice extends Service {

    String[] deviceid;
    private Handler handler = new Handler();
    private Runnable mqttconn;

    public static void mqttsub(String topic) {
        try {


            AppHelper.mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {

                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {
                            try {

                                String[] tops = topic.split("/");
                                String type = tops[1];


                                String message = new String(data, "UTF-8");
                                JSONObject meg = new JSONObject(message);
                                Log.e("log", meg.getString("message"));

                            } catch (Exception e) {
                                Log.e("log", "Message encoding error.", e);
                            }

                        }
                    });
        } catch (Exception e) {
            Log.e("log", "Subscription error.", e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        SharedPreferences spa = getSharedPreferences("userhas", 0);
        String tp = spa.getString("device", "");
        Log.e("log", tp);
        Log.e("log", "123");

        try {
            JSONArray jsa = new JSONArray(tp);
            deviceid = new String[jsa.length()];
            for (int i = 0; i < jsa.length(); i++) {
                deviceid[i] = jsa.getJSONObject(i).getString("deviceid") + "/#";
                Log.e("log", deviceid[i]);
            }
        } catch (Exception e) {
            Log.e("log", e.toString());
        }

       KeyStore clientKeyStore= AWSIotKeystoreHelper.getIotKeystore(Mainpage.serial, getFilesDir().getPath(),Mainpage.serial,Mainpage.serial);
        AppHelper.mqttManager.connect(clientKeyStore,new AWSIotMqttClientStatusCallback() {
            @Override
            public void onStatusChanged(final AWSIotMqttClientStatus status,
                                        final Throwable throwable) {

                handler.postDelayed(mqttconn=new Runnable() {
                    public void run() {
                        //log目前時間

                        //log目前時間

                        if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connecting) {


                        } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected) {

                            for (int i = 0; i < deviceid.length; i++) {
                                mqttsub(deviceid[i]);
                            }

                        }else  if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting) {
                            if (throwable != null) {

                            }

                        } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost) {
                            if (throwable != null) {

                            }

                        } else {

                        }


                    }
                }, 1000);

            }
        });
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {

        try {
            for (int i = 0; i < deviceid.length; i++) {
                AppHelper.mqttManager.unsubscribeTopic(deviceid[i]);
                Log.e("log", deviceid[i]);
            }
            AppHelper.mqttManager.disconnect();
        } catch (Exception e) {
            Log.e("log", "Disconnect error.", e);
        }
        handler.removeCallbacks(mqttconn);
        super.onDestroy();
    }

}