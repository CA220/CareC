package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateCertificateFromCsrRequest;
import com.amazonaws.services.iot.model.CreateCertificateFromCsrResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

public class Mainpage extends AppCompatActivity {
    private FloatingActionButton btnAdd;

    private AmazonDynamoDBClient dbClient;
    private Table dbTable;
    private String DYNAMODB_TABLE="tusre";
   // private ListView mRecyclerView;
    private ProgressDialog waitDialog;
    public String [] keycert=new String[2];
    private String AWS_IOT_POLICY_NAME = "tre-Policy";
    private String keystorePath;
    private String clientId=AppHelper.userid;
    public static String serial;
    private ImageView b;
    ListView listview;
    LayoutInflater inflater;
    SharedPreferences sap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sap= getSharedPreferences("userhas",0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            serial = Build.SERIAL;
        }else {
            serial=null;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        listview = (ListView) findViewById(R.id.listv_dev);//Device listview
        inflater  = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);//Device listview

          btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);



        AppHelper.iotcreate();
        keystorePath= getFilesDir().getPath();
        AppHelper.mqttcreate();

        //new DBloaddata().execute();
    /*    b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent();
                it.setClass(Mainpage.this,decive_data.class);
                startActivity(it);
            }
        });*/
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 點Button時要做的事寫在這裡..
                try {

                }catch (Exception v)
                {

                }

                Intent it=new Intent();
                it.setClass(Mainpage.this,QR.class);
                startActivity(it);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
              this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
       toggle.syncState();

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it =new Intent();
                it.setClass(Mainpage.this,my_profile.class);
                startActivity(it);

            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Mainpage.this, "TERMS still in development", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Mainpage.this, "HELP and FEEDBACK still in development", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppHelper.mqttManager.disconnect();
                Toast.makeText(Mainpage.this, "LOG OUT still in development", Toast.LENGTH_SHORT).show();
                File file = new File("/data/data/com.guineatech.CareC/shared_prefs","Data.xml");
                file.delete();
                AppHelper.userid=null;
                onBackPressed();
                Intent it=new Intent();
                it.setClass(Mainpage.this,Sign_Rerister.class);
                startActivity(it);
                finish();
            }
        });

        findViewById(R.id.user_ph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Mainpage.this, "USER PH SET still in development", Toast.LENGTH_SHORT).show();
                onBackPressed();

            }
        });
        findViewById(R.id.name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Mainpage.this, "USER name still in development", Toast.LENGTH_SHORT).show();
                onBackPressed();

            }
        });
        new DBloaddata().execute();
        new CreateCertificateTask().execute();


    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//勞USER 有哪一些DEVICE
    private  class DBloaddata extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... voids) {
            File file = new File("/data/data/com.guineatech.CareC/shared_prefs","userhas.xml");
            if(!file.exists()) {
                HttpURLConnection urlConnection = null;
                InputStream is = null;
                String result = "";
                try {

                    URL url = new URL("https://gr7yvkqkte.execute-api.us-west-2.amazonaws.com/dev/my-test/select");//php的位置
                    urlConnection = (HttpURLConnection) url.openConnection();//對資料庫打開連結
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("userid", AppHelper.userid);
                    Log.e("log", AppHelper.userid);
                    wr.writeBytes(jsonParam.toString());
                    wr.flush();
                    wr.close();
                    is = urlConnection.getInputStream();//從database 開啟 stream
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while ((line = bufReader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    is.close();
                    result = builder.toString();
                } catch (Exception e) {
                    e.printStackTrace();

                }


                Log.e("Log", result);
                return result;
            }
            return "";
        }

        @Override
        protected void onPostExecute(String devicedata) {
            super.onPostExecute(devicedata);
            if(devicedata=="")
            {
                String tpjson=sap.getString("device","");
                try {
                    JSONArray jsa=new JSONArray(tpjson);
                    Log.e("log",jsa.getJSONObject(0).toString());
                    Log.e("log",jsa.length()+"");
                    viewitem adapter = new viewitem(jsa,inflater);
                    listview.setAdapter(adapter);
                }catch (Exception e)
                {
                    Log.e("log",e.toString());
                }
            }
            else if(!devicedata.trim().equals("[]")){
                Log.e("Log",devicedata);
                sap.edit()
                        .putString("device",devicedata)
                        .commit();
                try {
                    JSONArray jsa=new JSONArray(devicedata);
                    Log.e("log",jsa.getJSONObject(0).toString());
                    Log.e("log",jsa.length()+"");
                    viewitem adapter = new viewitem(jsa,inflater);
                    listview.setAdapter(adapter);
                }catch (Exception e)
                {
                    Log.e("log",e.toString());
                }

            }
            else
                {
                    Log.e("Log","No data");

                }

        }
    }

    private class CreateCertificateTask extends AsyncTask<Void, Void, String> {
CreateCertificateTask()
{
   // waitDialog = new ProgressDialog(Mainpage.this);
   // waitDialog.setTitle("Load......");
    //waitDialog.show();
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
 AWSIotKeystoreHelper.saveCertificateAndPrivateKey(serial,
                result.getCertificatePem(), keyPair.getPrivate(), keystorePath,
         serial, serial);

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
           //    new DBloaddata().execute();
                try {
                    mqttconnect();
                }catch (Exception e)
                {
                    Log.e("log:",e.toString());
                }

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


    private void refreshdata()
    {
        File file = new File("/data/data/com.guineatech.CareC/shared_prefs","userhas.xml");
        file.delete();
        new  DBloaddata().execute();

    }

    public  void mqttconnect()
    {
Intent mqtts=new Intent(Mainpage.this,backgroundservice.class);
startService(mqtts);

    }





}
