package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateCertificateFromCsrRequest;
import com.amazonaws.services.iot.model.CreateCertificateFromCsrResult;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;

/**
 * Created by CAHans on 2018/1/24.
 */

public class setDevice extends AppCompatActivity {
    private ProgressDialog waitDialog;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;
    private String DYNAMODB_TABLE="tusre";
Button btsub;
    //String KEYSTORE_NAME="iot_keystore",CERTIFICATE_ID="default",KEYSTORE_PASSWORD="password";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setdevice);
      /*  waitDialog = new ProgressDialog(setDevice.this);
        waitDialog.setTitle("Load......");
        waitDialog.show();
        */
      btsub= (Button) findViewById(R.id.t);
        dbClient = new AmazonDynamoDBClient(AppHelper.credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
      btsub.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              mqttconnect();
          }
      });

    }

    public  void mqttconnect()
    {
        KeyStore clientKeyStore= AWSIotKeystoreHelper.getIotKeystore(AppHelper.userid, getFilesDir().getPath(),AppHelper.userid,AppHelper.userid);
        AppHelper.mqttManager.connect(clientKeyStore,new AWSIotMqttClientStatusCallback() {
            @Override
            public void onStatusChanged(final AWSIotMqttClientStatus status,
                                        final Throwable throwable) {
                //  Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status == AWSIotMqttClientStatus.Connecting) {
                            //tvStatus.setText("Connecting...");

                        } else if (status == AWSIotMqttClientStatus.Connected) {
                            //tvStatus.setText("Connected");
                            mqttsub("esp8266/sns");

                        } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                            if (throwable != null) {
                                //  Log.e(LOG_TAG, "Connection error.", throwable);
                            }
                            //tvStatus.setText("Reconnecting");
                        } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                            if (throwable != null) {
                                //  Log.e(LOG_TAG, "Connection error.", throwable);
                            }
                            //tvStatus.setText("Disconnected");
                        } else {
                            //tvStatus.setText("Disconnected");
                        }
                    }
                });
            }
        });
    }

    public  void mqttsub(String topic)
    {
        AppHelper.mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                new AWSIotMqttNewMessageCallback() {
                    @Override
                    public void onMessageArrived(final String topic, final byte[] data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String message = new String(data, "UTF-8");
                                    //   Log.d(LOG_TAG, "Message arrived:");
                                     Log.e("log", "   Message: " + message);
                                    // Log.d(LOG_TAG, " Message: " + message);
                                    //  tvLastMessage.setText(message);
                                    if(message.equals("{\"temp\":\"9\"}"))
                                    {

                                        new  DBloaddata().execute();
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    // Log.e(LOG_TAG, "Message encoding error.", e);
                                }
                            }
                        });
                    }
                });
    }


    private  class DBloaddata extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            dbTable=Table.loadTable(dbClient, DYNAMODB_TABLE);
            Document d=new Document();
            d.put("id",AppHelper.userid);
            d.put("Device","ese");
            dbTable.putItem(d);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent it=new Intent();
            it.setClass(setDevice.this,Mainpage.class);
            startActivity(it);
            finish();
        }
    }





}
