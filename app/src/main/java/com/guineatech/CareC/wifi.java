package com.guineatech.CareC;



import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;


import android.util.Log;
import android.net.Uri;

import android.net.wifi.WifiManager;

import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import android.widget.Toast;


import static android.Manifest.permission.ACCESS_COARSE_LOCATION;



public class wifi extends AppCompatActivity {

    private Context context = this;
    private WifiManager wifiManager;
    private Spinner spinnerWifis;
    private String bcode;
    private Button btstep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        Intent it=this.getIntent();
         bcode=it.getStringExtra("bcode");
        spinnerWifis = (Spinner)findViewById(R.id.spinner);
        btstep= (Button) findViewById(R.id.bt_step);
        if (ActivityCompat.checkSelfPermission(wifi.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(wifi.this,new String[]{ACCESS_COARSE_LOCATION},1);
        }
        btstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Adapter allwifi= spinnerWifis.getAdapter();
                int c=allwifi.getCount();


                for(int v =0 ;v<c ;) {
                    if(bcode==allwifi.getItem(v).toString()){
                       while (Connect(bcode,"12345678",WifiCipherType.WIFICIPHER_WPA))
                       {
                           try {
// 为了避免程序一直while循环，让它睡个100毫秒在检测……..
                               Thread.currentThread();
                               Thread.sleep(100);
                           } catch (InterruptedException ie) {
                           }
                       }
                    break;
                    }
                    v++;
                }


            }
        });
        openGPS(context);
        IsEnable();
        scan();
    }

    private void IsEnable(){
        //首先取得Wi-Fi服務控制Manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(wifiManager.isWifiEnabled()){

        }else {

            //wifiManager.setWifiEnabled(true);
            wifiManager.setWifiEnabled(true);
            Toast.makeText(wifi.this, "Wi-Fi開啟中", Toast.LENGTH_SHORT).show();
        }


    }

    private void scan() {//搜尋WIFI
        // Register the Receiver in some part os fragment...
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiScanReceive();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();

        // Inside the receiver:
    }


    private void wifiScanReceive(){
        // the result.size() is 0 after update to Android v6.0, same code working in older devices.
        List<ScanResult> scanResultList =  wifiManager.getScanResults();

        int size = scanResultList.size();
        final List<String> dataList = new ArrayList<String>(size);
        Toast.makeText(context,"scan result :" + size,Toast.LENGTH_SHORT).show();

         for(int i = 0 ; i <size  ; i++ )
        {
            //手機目前周圍的Wi-Fi環境
            String SSID  = scanResultList.get(i).SSID ;
          //  int LEVEL = scanResultList.get(i).level;
            String item = String.format(SSID);
            Log.d("wifi",item);
            dataList.add(item);
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter adapter = new ArrayAdapter(context,android.R.layout.simple_spinner_item,dataList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerWifis.setAdapter(adapter);
            }
        });
    }
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public boolean Connect(WifiConfiguration wf) {
        IsEnable();
// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句，即当状态为WIFI_STATE_ENABLING时，让程序在while里面跑
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
// 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }
        boolean bRet = wifiManager.enableNetwork(wf.networkId, true);
        wifiManager.saveConfiguration();
        return bRet;
    }
    public boolean Connect(String SSID, String Password, WifiCipherType Type) {
        IsEnable();
// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
// 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }

        WifiConfiguration wifiConfig =this
                .CreateWifiInfo(SSID, Password, Type);
        int netID = wifiManager.addNetwork(wifiConfig);
        boolean bRet = wifiManager.enableNetwork(netID, true);
        wifiManager.saveConfiguration();
        return bRet;
    }
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    private WifiConfiguration CreateWifiInfo(String SSID, String Password,
                                             WifiCipherType Type) {

        WifiConfiguration config = new WifiConfiguration();

        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        config.SSID = "\"" + SSID + "\"";

        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";

            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WEP) {

            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;

            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);

            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WPA) {

            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;

            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            return null;
        }
        return config;
    }


}
