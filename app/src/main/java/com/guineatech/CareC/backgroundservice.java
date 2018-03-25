package com.guineatech.CareC;

/**
 * Created by CAHans on 2018/3/11.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import android.widget.TextView;


//繼承android.app.Service
public class backgroundservice extends Service {
    TextView test;
    private Handler handler = new Handler();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
/*
    @Override
    public void onStart(Intent intent, int startId) {


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
                            //tvStatus.setText("Connecting...");

                        } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected) {
                            //tvStatus.setText("Connected");

                      //      Log.i("time:", new Date().toString());
                        }else  if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting) {
                            if (throwable != null) {
                                //  Log.e(LOG_TAG, "Connection error.", throwable);

                            }
                            //tvStatus.setText("Reconnecting");
                        } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost) {
                            if (throwable != null) {
                                //  Log.e(LOG_TAG, "Connection error.", throwable);
                            }
                            //tvStatus.setText("Disconnected");
                        } else {
                            //tvStatus.setText("Disconnected");
                        }

                      //  Log.i("time:", new Date().toString());
                        handler.postDelayed(this, 1000);
                    }
                }, 1000);

            }
        });
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {

        try {
            AppHelper.mqttManager.disconnect();
        } catch (Exception e) {
            Log.e("log", "Disconnect error.", e);
        }
        handler.removeCallbacks(mqttconn);
        super.onDestroy();
    }

    private Runnable mqttconn ;

    public static void mqttsub(String topic)
    {
        try {


        AppHelper.mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                new AWSIotMqttNewMessageCallback() {
                    @Override
                    public void onMessageArrived(final String topic, final byte[] data) {
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String message = new String(data, "UTF-8");


                                } catch (UnsupportedEncodingException e) {
                                    Log.e(LOG_TAG, "Message encoding error.", e);
                                }
                            }
                        };
                    }
                });
        }
        catch (Exception e) {
            Log.e("log", "Subscription error.", e);
        }
    }
    */
}