package com.guineatech.CareC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by CAHans on 2018/1/24.
 */

public class setDevice extends AppCompatActivity {
    Button bt_conn;
    ImageView line, iv_setting, iv_check, iv_res;
    String deid, na;
    TextView tv_sele, tv_fin;
    int f = 0;
    private BroadcastReceiver counterActionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.e("DD", " " + intent.getStringExtra("IOT"));
            if (intent.getStringExtra("IOT").equals("STEP_1")) {
                line.setImageDrawable(getResources().getDrawable(R.drawable.step2_xxxhdpi));
                Log.e("DD", " " + intent.getStringExtra("IOT"));


            } else if (intent.getStringExtra("IOT").equals("STEP_2")) {
                line.setImageDrawable(getResources().getDrawable(R.drawable.step3_xxxhdpi));
                Log.e("DD", " " + intent.getStringExtra("IOT"));
                //DB
                String IOT_thing_r = "IOT_thing_r";
                Intent i = new Intent();
                i.setAction(IOT_thing_r);
                i.putExtra("IOT", "STEP_3");
                sendBroadcast(i);
            } else if (intent.getStringExtra("IOT").equals("Eorro")) {
                // Toast.makeText(getApplicationContext(),"Error "+intent.getStringExtra("msg"),Toast.LENGTH_LONG);
            } else {
                iv_check.setVisibility(View.VISIBLE);
                fin();
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_dev);
        bt_conn = findViewById(R.id.bt_next);
        iv_setting = findViewById(R.id.iv_setting);
        iv_check = findViewById(R.id.iv_check);
        iv_res = findViewById(R.id.iv_restart);
        tv_sele = findViewById(R.id.tv_title3);
        tv_fin = findViewById(R.id.tv_title5);
        line = findViewById(R.id.iv_step1);
        bt_conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (f == 0) {
                    bt_conn.setVisibility(View.INVISIBLE);
                    iv_setting.setVisibility(View.INVISIBLE);
                    line.setVisibility(View.VISIBLE);
                    tv_sele.setVisibility(View.VISIBLE);
                    new pkcert(deid, getApplicationContext(), 0).execute();
                } else {
                    Intent i = new Intent();
                    i.setAction("DB");
                    i.putExtra("DB", "R");
                    sendBroadcast(i);
                    finish();

                }
            }
        });


        Intent it = this.getIntent();
        deid = it.getStringExtra("deviceid");
        na = it.getStringExtra("nickname");


        //if(wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(bcode, "12345678", 3)))
        //new DBadddata().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter counterActionFilter = new IntentFilter("IOT_thing_r");
        registerReceiver(counterActionReceiver, counterActionFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(counterActionReceiver);
    }

    private void fin() {
        iv_check.setVisibility(View.INVISIBLE);
        line.setVisibility(View.INVISIBLE);
        tv_sele.setVisibility(View.INVISIBLE);
        tv_fin.setVisibility(View.VISIBLE);
        iv_res.setVisibility(View.VISIBLE);


        bt_conn.setVisibility(View.VISIBLE);
        f = 1;
        // new DBadddata().execute();
    }

    private class DBadddata extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... voids) {
            {
                HttpURLConnection urlConnection = null;
                InputStream is = null;
                String result = "";
                try {

                    URL url = new URL("https://gr7yvkqkte.execute-api.us-west-2.amazonaws.com/dev/my-test/adddevice");//php的位置
                    urlConnection = (HttpURLConnection) url.openConnection();//對資料庫打開連結
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("deviceid", deid);
                    jsonParam.put("userid", AppHelper.userid);
                    jsonParam.put("nickname", na);
                    Log.e("log", deid + na);
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

        }

        @Override
        protected void onPostExecute(String devicedata) {
            super.onPostExecute(devicedata);
            if (devicedata.equals(deid + "is succeeded add")) {

                bt_conn.setVisibility(View.VISIBLE);
                f = 1;
            } else {
                Log.e("log", "Erorr");

            }

        }
    }


}
