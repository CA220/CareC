package com.guineatech.CareC;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

Button btsub;
    String deid, na;
    //String KEYSTORE_NAME="iot_keystore",CERTIFICATE_ID="default",KEYSTORE_PASSWORD="password";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setdevice);
        Intent it = this.getIntent();
        deid = it.getStringExtra("deviceid");
        na = it.getStringExtra("nickname");
        Log.e("log", na);
        btsub = findViewById(R.id.bt_dataok);
      btsub.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              backgroundservice.mqttsub(deid + "/#");
              Intent intent = new Intent(setDevice.this, Mainpage.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              startActivity(intent);

          }
      });
        new DBadddata().execute();
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


            } else {
                Log.e("log", "Erorr");
            }

        }
    }


}
