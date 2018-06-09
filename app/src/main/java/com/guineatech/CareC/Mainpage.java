package com.guineatech.CareC;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Mainpage extends Fragment {
    LayoutInflater inflaters;
    private ListView lit;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);
        lit = v.findViewById(R.id.dev_list);
        inflaters = inflater;

        AppHelper.iotcreate();
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), QR.class);
                startActivity(intent);

            }
        });
        //抓DATA
        new DBloaddata().execute();

        return v;

    }








    //勞USER 有哪一些DEVICE
    private  class DBloaddata extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... voids) {
            //不是沒次都勞Data 更新或USER下拉才更新
            File file = new File("/data/data/com.guineatech.CareC/shared_prefs", "userhas.xml");
            if (1 == 1)//!file.exists())

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
            return "";
        }

        @Override
        protected void onPostExecute(String devicedata) {
            super.onPostExecute(devicedata);
            if(devicedata=="")
            {

                String tpjson = Frame.sap.getString("device", "");
                try {
                    JSONArray jsa=new JSONArray(tpjson);
                    Log.e("log",jsa.getJSONObject(0).toString());
                    Log.e("log",jsa.length()+"");
                    viewitem adapter = new viewitem(jsa, inflaters);
                    lit.setAdapter(adapter);
                }catch (Exception e)
                {
                    Log.e("log",e.toString());
                }
            }
            else if(!devicedata.trim().equals("[]")){
                Log.e("Log",devicedata);
                Frame.sap.edit()
                        .putString("device",devicedata)
                        .commit();
                try {
                    JSONArray jsa=new JSONArray(devicedata);
                    Log.e("log",jsa.getJSONObject(0).toString());
                    Log.e("log",jsa.length()+"");
                    viewitem adapter = new viewitem(jsa, inflaters);
                    lit.setAdapter(adapter);
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

}

/*
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
 */
