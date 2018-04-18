package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.AttachThingPrincipalRequest;
import com.amazonaws.services.iot.model.CreateCertificateFromCsrRequest;
import com.amazonaws.services.iot.model.CreateCertificateFromCsrResult;
import com.amazonaws.services.iot.model.CreateThingRequest;
import com.amazonaws.services.iot.model.CreateThingResult;

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
    public static String serial;
    static boolean frist = true;
    public String[] keycert = new String[2];
    ListView listview;
    LayoutInflater inflater;
    SharedPreferences sap;
    int[] image = {R.drawable.ic_add_black_24dp, R.drawable.ic_bed_80_40};
    private FloatingActionButton btnAdd;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;
    private String DYNAMODB_TABLE="tusre";
   // private ListView mRecyclerView;
    private ProgressDialog waitDialog;
    private String AWS_IOT_POLICY_NAME = "tre-Policy";
    private String keystorePath;
    private String clientId=AppHelper.userid;
    private AdapterViewFlipper bgim;
    private RadioGroup rg;
    private View ln_main, ln_wake, ln_help, ln_acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Decive DATA 放的地方
        sap= getSharedPreferences("userhas",0);

        //手機的唯一值Serial 識別用
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            serial = Build.SERIAL;
        }else {
            serial=null;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_frame);
        listview = findViewById(R.id.listv_dev);//Device listview
        inflater  = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);//Device listview
        //Account Page
        TextView name = findViewById(R.id.name);
        name.setText(AppHelper.userid);
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File logindata = new File("/data/data/com.guineatech.CareC/shared_prefs", "Data.xml");

                logindata.delete();
                mqttdisconnect();
                Intent it=new Intent();
                it.setClass(Mainpage.this, Sign_Rerister.class);
                startActivity(it);
                finish();
            }
        });
        //
        btnAdd = findViewById(R.id.btnAdd);

        //底部按鈕
        ln_main = findViewById(R.id.ln_main);
        ln_wake = findViewById(R.id.ln_wake);
        ln_help = findViewById(R.id.ln_help);
        ln_acc = findViewById(R.id.ln_acc);
        rg = findViewById(R.id.rgHomeMenu);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = radioGroup.findViewById(i);
                String rbtext = rb.getText().toString();

                ln_main.setVisibility(View.GONE);
                ln_wake.setVisibility(View.GONE);
                ln_help.setVisibility(View.GONE);
                ln_acc.setVisibility(View.GONE);
                if (rbtext.equals("Home")) {
                    Log.e("log", rbtext);
                    ln_main.setVisibility(View.VISIBLE);
                } else if (rbtext.equals("Help")) {
                    Log.e("log", rbtext);
                    ln_help.setVisibility(View.VISIBLE);
                } else if (rbtext.equals("Wakeme")) {
                    Log.e("log", rbtext);
                    ln_wake.setVisibility(View.VISIBLE);
                } else if (rbtext.equals("Account")) {
                    Log.e("log", rbtext);
                    ln_acc.setVisibility(View.VISIBLE);
                }
            }
        });

        //helppage



        //IOT連接
        AppHelper.iotcreate();
        keystorePath = getFilesDir().getPath();
        AppHelper.mqttcreate();


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


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bgim = findViewById(R.id.image_background);
        // 创建一个BaseAdapter对象，该对象负责提供Gallery所显示的列表项
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return image.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            // 该方法的返回的View就是代表了每个列表项
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // 创建一个ImageView
                ImageView imageView = new ImageView(Mainpage.this);
                imageView.setImageResource(image[position]);
                // 设置ImageView的缩放类型
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                // 为imageView设置布局参数
              /*  imageView.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));*/
                return imageView;
            }
        };
        bgim.setAdapter(adapter);

        //抓金鑰和DATA
        new DBloaddata().execute();
        new CreateCertificateTask().execute();


    }


    private void refreshdata() {
        File file = new File("/data/data/com.guineatech.CareC/shared_prefs", "userhas.xml");
        file.delete();
        new DBloaddata().execute();

    }

    public void mqttconnect() {
        if (frist) {
            frist = false;
            Intent mqtts = new Intent(Mainpage.this, backgroundservice.class);
            startService(mqtts);
        } else {
        }
    }

    private void mqttdisconnect() {
        Intent mqtts = new Intent(Mainpage.this, backgroundservice.class);
        stopService(mqtts);
    }


    //勞USER 有哪一些DEVICE
    private  class DBloaddata extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... voids) {
            //不是沒次都勞Data 更新或USER下拉才更新
          /*  File file = new File("/data/data/com.guineatech.CareC/shared_prefs","userhas.xml");
            if(!file.exists())*/
            {
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
            //           return "";
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
        CreateThingRequest ct = new CreateThingRequest().withThingName("Tw0002");
        CreateThingResult cresult = AppHelper.iotClient.createThing(ct);
        AttachThingPrincipalRequest attachThingPrincipalRequest = new AttachThingPrincipalRequest();
        attachThingPrincipalRequest.setThingName("Tw0002");
        attachThingPrincipalRequest.setPrincipal(result.getCertificateArn());
        AppHelper.iotClient.attachThingPrincipal(attachThingPrincipalRequest);
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





}
