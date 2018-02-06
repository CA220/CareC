package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.List;

public class Mainpage extends AppCompatActivity {
    private FloatingActionButton btnAdd;
    private DrawerLayout drawerLayout;
    private NavigationView navigation_view;
    private Toolbar toolbar;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;
    private String DYNAMODB_TABLE="tusre";
    private ListView mRecyclerView;
    private ProgressDialog waitDialog;
    public String [] keycert=new String[2];
    private String AWS_IOT_POLICY_NAME = "tre-Policy";
    private String keystorePath;
    private String clientId=AppHelper.userid;

    private Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        b=(Button)findViewById(R.id.button3) ;
        btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigation_view = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView=findViewById(R.id.recycler_view);
        dbClient = new AmazonDynamoDBClient(AppHelper.credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        AppHelper.iotcreate();
        keystorePath= getFilesDir().getPath();
        AppHelper.mqttcreate();

        new DBloaddata().execute();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent();
                it.setClass(Mainpage.this,decive_data.class);
                startActivity(it);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 點Button時要做的事寫在這裡..
                try {
                    AppHelper.mqttManager.disconnect();
                }catch (Exception v)
                {

                }

                Intent it=new Intent();
                it.setClass(Mainpage.this,QR.class);
                startActivity(it);
            }
        });

        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);

        // 將drawerLayout和toolbar整合，會出現「三」按鈕
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 選單點擊事件
        navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // 點選時收起選單
                drawerLayout.closeDrawer(GravityCompat.START);

                // 取得選項id
                int id = item.getItemId();

                // 依照id判斷點了哪個項目並做相應事件
                if (id == R.id.action_home) {
                    // 按下「首頁」要做的事
                    Toast.makeText(Mainpage.this, "首頁", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.action_help) {
                    // 按下「使用說明」要做的事
                    Toast.makeText(Mainpage.this, "使用說明", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.action_settings) {
                    // 按下「設定」要做的事
                    Toast.makeText(Mainpage.this, "設定", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (id == R.id.action_about) {
                    // 按下「關於」要做的事
                    Toast.makeText(Mainpage.this, "關於", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        Log.e("log","i");
        new CreateCertificateTask().execute();


    }

    private  class DBloaddata extends AsyncTask<Void,Void,List<Document>>
    {

        @Override
        protected List<Document> doInBackground(Void... voids) {
            dbTable=Table.loadTable(dbClient, DYNAMODB_TABLE);
            return dbTable.query(new Primitive(AppHelper.userid)).getAllResults();
        }

        @Override
        protected void onPostExecute(List<Document> documents) {
            super.onPostExecute(documents);
            mqttconnect();

            if(documents!=null)
            {
                ArrayAdapter<Document> ld = new ArrayAdapter<Document>(Mainpage.this, R.layout.listlayout,R.id.text, documents);

                mRecyclerView.setAdapter(ld);
            }
            try {
                waitDialog.dismiss();
            }
            catch (Exception e) {
                //
            }

        }
    }

    private class CreateCertificateTask extends AsyncTask<Void, Void, String> {
CreateCertificateTask()
{
    waitDialog = new ProgressDialog(Mainpage.this);
    waitDialog.setTitle("Load......");
    waitDialog.show();
}

private String keyandcert()
{
    try {


    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair keyPair = kpg.generateKeyPair();


    PrivateKey priv = keyPair.getPrivate();
    byte[] privBytes = priv.getEncoded();

    PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(privBytes);
    ASN1Encodable encodable = pkInfo.parsePrivateKey();
    ASN1Primitive primitive = encodable.toASN1Primitive();

    keycert[0]= Base64.encodeToString(primitive.getEncoded(),0);
    // then create the CSR (uses SpongyCastle (BouncyCastle))
    String csrPemString = CsrHelper.generateCsrPemString(keyPair);

    // now create the create certificate request using that CSR
    CreateCertificateFromCsrRequest request = new CreateCertificateFromCsrRequest();
    request.setSetAsActive(true);
    request.setCertificateSigningRequest(csrPemString);

    // submit the request
    CreateCertificateFromCsrResult result = AppHelper.iotClient.createCertificateFromCsr(request);
    keycert[1]=result.getCertificatePem().toString();
    keycert[0]="-----BEGIN RSA PRIVATE KEY----- \n"+keycert[0]+"-----END RSA PRIVATE KEY-----\n";

    Log.e("log",keycert[0]);
    Log.e("log",keycert[1]);
 AWSIotKeystoreHelper.saveCertificateAndPrivateKey(AppHelper.userid,
                result.getCertificatePem(), keyPair.getPrivate(), keystorePath,
                AppHelper.userid, AppHelper.userid);

    AttachPrincipalPolicyRequest policyAttachRequest = new AttachPrincipalPolicyRequest();
    policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
    policyAttachRequest.setPrincipal(result.getCertificateArn());
    AppHelper.iotClient.attachPrincipalPolicy(policyAttachRequest);

    FileWriter fw = new FileWriter(getFilesDir()+"/ac8cert.pem", false);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write(keycert[1]);
    bw.close();
    FileWriter ew = new FileWriter(getFilesDir()+"/ac8pr.key", false);
    BufferedWriter rr = new BufferedWriter(ew);
    rr.write(keycert[0]);
    rr.close();
    return "key";

    }catch (Exception e)
    {
        return "key F "+e.toString();
    }

}
        @Override
        protected String doInBackground(Void... voids) {
            try {

                try {

                    File file = new File(keystorePath, "ac8cert.pem");
                    if (file.exists())  {Log.e("log","hi");
                        return "key";
                        } else {
                            //Log.i(LOG_TAG, "Certificate and key NOT found in keystore.");
                            return   keyandcert();
                        }

                } catch (Exception e) {
                    //Log.e(LOG_TAG, "An error occurred accessing the keystore and retrieving cert/key.", e);
                    return e.toString();
                }
            } catch (Exception e) {
                return e.toString();
            }

        }


        protected void onPostExecute(String result) {


            if(result=="key")
            {
               new DBloaddata().execute();
            }
            else
            {try {
                waitDialog.dismiss();
            }catch (Exception e)
            {

            }

Log.e("log",result);
new CreateCertificateTask().execute();
            }
        }
    }


    public  void mqttconnect()
    {
        KeyStore clientKeyStore=AWSIotKeystoreHelper.getIotKeystore(AppHelper.userid,keystorePath,AppHelper.userid,AppHelper.userid);
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
                                    // Log.d(LOG_TAG, "   Topic: " + topic);
                                    Log.e("log", " Message: " + message);

                                    //  tvLastMessage.setText(message);

                                } catch (UnsupportedEncodingException e) {
                                    // Log.e(LOG_TAG, "Message encoding error.", e);
                                }
                            }
                        });
                    }
                });
    }


}
