package com.guineatech.CareC;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;



public class wifi extends AppCompatActivity {


    private Context context = this;

    private Spinner spinnerWifis;
    private String bcode, name;
    private Button btstep;
    private String wifissid="",wifipwd="",endpoint=AppHelper.CUSTOMER_SPECIFIC_ENDPOINT;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;
    private EditText ed_pwd;
    private  LocationManager status;
    private WifiAdmin wifiAdmin;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_mattress);
        Intent it=this.getIntent();
         bcode=it.getStringExtra("bcode");
        name = it.getStringExtra("nickname");
        title = findViewById(R.id.tv_title2);
        title.setText("ID:" + bcode);

        spinnerWifis = findViewById(R.id.sp_wifi);
        ed_pwd = findViewById(R.id.et_conpasswd);
        btstep = findViewById(R.id.bt_confirm);
        if (ActivityCompat.checkSelfPermission(wifi.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(wifi.this,new String[]{ACCESS_COARSE_LOCATION},1);
        }


//Next step
        btstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    wifissid = spinnerWifis.getSelectedItem().toString();
                    wifipwd = ed_pwd.getText().toString();


                    // new  conndecives().execute();
                    Intent it = new Intent();
                    it.putExtra("deviceid", bcode);
                    it.putExtra("nickname", name);
                    it.putExtra("ssid", wifissid);
                    it.putExtra("pwd", wifipwd);
                    //Log.e("log", nickname.getText().toString());
                    it.setClass(wifi.this, setDevice.class);
                    startActivity(it);
                    finish();
                }


            }
        });
       status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            // showDialogMessage("Location","已開啟", 0);
            wifilist();

        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            showDialogMessage("Location","請開啟定位服務", 2);
        }
    }

    protected void onResume() {
        super.onResume();
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            showDialogMessage("Location","已開啟", 0);
            wifilist();
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            showDialogMessage("Location","請開啟定位服務", 2);

        }
    }

    private void wifilist(){
        wifiAdmin = new WifiAdmin(this);
        wifiAdmin.openWifi();
        wifiAdmin.startScan();
        wifiAdmin.getlist();
        ArrayAdapter<String> ap=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,wifiAdmin.getWifiListstring());
        spinnerWifis.setAdapter(ap);


    }

//跳出的視窗
    private void showDialogMessage(String title, String body, final int Whatis) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               switch (Whatis)
               {
                   case 2:
                       startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面 finish();
                   default:

               }



            }
        });
        userDialog = builder.create();
        userDialog.show();
    }





//連到裝置
    public class conndecives extends AsyncTask<Void,Void,String>
    {
        conndecives()
        {
            try {
                waitDialog.dismiss();
            }catch (Exception e)
            {
                e.toString();
            }
            waitDialog = new ProgressDialog(wifi.this);
            waitDialog.setTitle("Load......");
            waitDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection=null;


            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "---------WebKitFormBoundaryfsjCanVImiBm0CDt";
            String [] a=new String[]{"ssid","password","endpoint"};
            String [] b=new String[]{wifissid,wifipwd,endpoint};
            String [] c=new String[]{"ac8cert.pem","ac8pr.key","cahans.pem"};

            try
            {
                FileReader[] in = new FileReader[]{new FileReader(getFilesDir()+"/ac8cert.pem"),new FileReader(getFilesDir()+"/ac8pr.key") };
                URL url=new URL("http://192.168.1.1/upload.php");//php的位置
                urlConnection=(HttpURLConnection) url.openConnection();//對資料庫打開連結

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setReadTimeout(2000);
                urlConnection.setConnectTimeout(5000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);


                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                wr.writeBytes(lineEnd);


                //----------------------------------------------------

                for(int j=0;j<2;j++) {

                    wr.writeBytes(twoHyphens + boundary + lineEnd);
                    wr.writeBytes("Content-Disposition: form-data; name=\"file"+j+"\"; filename=\""+c[j]+"\"" + lineEnd);
                    wr.writeBytes("Content-Type: text/plain" + lineEnd);

                    wr.writeBytes(lineEnd);
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(in[j]);

                        // do reading, usually loop until end of file reading
                        String mLine = null;

                        while ((mLine = reader.readLine()) != null) {

                            wr.writeBytes(mLine+lineEnd);

                            Log.e("log_tag",mLine+lineEnd);
                        }

                    } catch (IOException e) {
                        //log the exception
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                //log the exception
                            }
                        }
                    }


                }

                //  for(int i=0;i<3;i++)
                {
                    int i=2;
                    wr.writeBytes(twoHyphens + boundary + lineEnd);
                    wr.writeBytes("Content-Disposition: form-data; name=\"file"+i+"\"; filename=\""+c[i]+"\"" + lineEnd);
                    wr.writeBytes("Content-Type: text/plain" + lineEnd);

                    wr.writeBytes(lineEnd);
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(
                                new InputStreamReader(getAssets().open(c[i])));


                        // do reading, usually loop until end of file reading
                        String mLine;
                        while ((mLine = reader.readLine()) != null) {

                            wr.writeBytes(mLine+lineEnd);

                            Log.e("log_tag",mLine);
                        }
                        wr.writeBytes(lineEnd);
                    } catch (IOException e) {
                        //log the exception
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                //log the exception
                            }
                        }
                    }
                    // wr.writeBytes(lineEnd);
                    //inputStream.close();

                }
//----------------------------------------------------
                for(int i=0;i<a.length;i++)
                {
                    wr.writeBytes(twoHyphens + boundary + lineEnd);
                    wr.writeBytes("Content-Disposition: form-data; name=\""+a[i]+"\"" + lineEnd);
                    wr.writeBytes(lineEnd);
                    wr.writeBytes(b[i]);
                    wr.writeBytes(lineEnd);
                    // Log.e("log_tag", twoHyphens + boundary + lineEnd+"Content-Disposition: form-data; name=\""+a[i]+"\"" + lineEnd  +lineEnd+b[i]+lineEnd);
                }

                wr.writeBytes(twoHyphens+boundary+twoHyphens);
                Log.e("log_tag", "pre flush");
                wr.flush();
                //OutputStream output = urlConnection.getOutputStream();
                Log.e("log_tag", "finsih flush");
                wr.close();
                Log.e("log_tag", "close");

                //接通資料庫

                int res = urlConnection.getResponseCode();
                Log.e("log_tag", "response code:"+res);




            }
            catch(Exception e)
            {

                Log.e("log_tag", e.toString());
                if(e.toString().equals("java.io.IOException: unexpected end of stream on Connection{192.168.1.1:80, proxy=DIRECT hostAddress=192.168.1.1 cipherSuite=none protocol=http/1.1} (recycle count=0)"))
                    return "Sucess!!";
                else return "Fail "+e.toString();

            }
            finally {

                urlConnection.disconnect();
            }
            return "Fail";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            try {
                waitDialog.dismiss();
            }catch (Exception e)
            {
                e.toString();
            }
            if(aVoid.equals("Sucess!!"))
            {
               showDialogMessage("Connect Over","Switch Device On N",3);
            }
            else if(aVoid.equals("Fail"))
            {
                Log.e("log",aVoid);
            }
            else
                {
                    Log.e("log",aVoid);
                }

        }
    }


    //點空白取消鍵盤
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (HideInputUtils.isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }




}
