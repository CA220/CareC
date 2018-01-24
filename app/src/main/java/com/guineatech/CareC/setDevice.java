package com.guineatech.CareC;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateCertificateFromCsrRequest;
import com.amazonaws.services.iot.model.CreateCertificateFromCsrResult;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyPair;

/**
 * Created by CAHans on 2018/1/24.
 */

public class setDevice extends AppCompatActivity {
    private ProgressDialog waitDialog;
    private String wifissid,wifipwd,endpoint="a2hd4hpd193y9c.iot.us-west-2.amazonaws.com";
    String cec,pk;
    String AWS_IOT_POLICY_NAME = "tre-Policy";
    AWSIotMqttManager mqttManager;
    String keystorePath;
    //String KEYSTORE_NAME="iot_keystore",CERTIFICATE_ID="default",KEYSTORE_PASSWORD="password";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setdevice);
        waitDialog = new ProgressDialog(setDevice.this);
        waitDialog.setTitle("Load......");
        waitDialog.show();

        new  CreateCertificateTask().execute();
    }


    public class conndecives extends AsyncTask<Void,Void,String> {
        protected String doInBackground(Void... voids) {
            String result = "";
            HttpURLConnection urlConnection = null;


            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "---------WebKitFormBoundaryfsjCanVImiBm0CDt";
            String[] a = new String[]{"ssid", "password", "endpoint"};
            String[] b = new String[]{wifissid, wifipwd, endpoint};
            String[] c = new String[]{"ac8cert.pem", "ac8pr.key", "cahans.pem"};

            try {
                FileReader[] in = new FileReader[]{new FileReader(getFilesDir() + "/ac8cert.pem"), new FileReader(getFilesDir() + "/ac8pr.key")};
                URL url = new URL("http://192.168.1.1/upload.php");//php的位置
                //  URL url=new URL("http://192.192.140.221/~D10416247/conn/upload.php");//php的位置
                urlConnection = (HttpURLConnection) url.openConnection();//對資料庫打開連結

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setReadTimeout(2000);
                urlConnection.setConnectTimeout(5000);
                // urlConnection.setRequestProperty("User-agent", "IE/6.0");
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                //urlConnection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                wr.writeBytes(lineEnd);


                //----------------------------------------------------

                for (int j = 0; j < 2; j++) {

                    wr.writeBytes(twoHyphens + boundary + lineEnd);
                    wr.writeBytes("Content-Disposition: form-data; name=\"file" + j + "\"; filename=\"" + c[j] + "\"" + lineEnd);
                    wr.writeBytes("Content-Type: text/plain" + lineEnd);

                    wr.writeBytes(lineEnd);
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(in[j]);


                        // do reading, usually loop until end of file reading
                        String mLine;
                        while ((mLine = reader.readLine()) != null) {

                            wr.writeBytes(mLine + lineEnd);

                            Log.e("log_tag", mLine);
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
                    // wr.writeBytes(lineEnd);
                    //inputStream.close();

                }
//----------------------------------------------------


                // wr.writeBytes("ssid=cospace_mid&password=12345678&endpoint=a2hd4hpd193y9c.iot.us-west-2.amazonaws.com");
                //  wr.writeBytes(lineEnd);
                //----------------------------------------------------
                {
                    int i = 2;
                    wr.writeBytes(twoHyphens + boundary + lineEnd);
                    wr.writeBytes("Content-Disposition: form-data; name=\"file" + i + "\"; filename=\"" + c[i] + "\"" + lineEnd);
                    wr.writeBytes("Content-Type: text/plain" + lineEnd);

                    wr.writeBytes(lineEnd);
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(
                                new InputStreamReader(getAssets().open("cahans.pem")));


                        // do reading, usually loop until end of file reading
                        String mLine;
                        while ((mLine = reader.readLine()) != null) {

                            wr.writeBytes(mLine + lineEnd);

                            Log.e("log_tag", mLine);
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
                    // wr.writeBytes(lineEnd);
                    //inputStream.close();

                }
//----------------------------------------------------
                for (int i = 0; i < a.length; i++) {
                    wr.writeBytes(twoHyphens + boundary + lineEnd);
                    wr.writeBytes("Content-Disposition: form-data; name=\"" + a[i] + "\"" + lineEnd);
                    wr.writeBytes(lineEnd);
                    wr.writeBytes(b[i]);
                    wr.writeBytes(lineEnd);
                    // Log.e("log_tag", twoHyphens + boundary + lineEnd+"Content-Disposition: form-data; name=\""+a[i]+"\"" + lineEnd  +lineEnd+b[i]+lineEnd);
                }

                wr.writeBytes(twoHyphens + boundary + twoHyphens);
                Log.e("log_tag", "pre flush");
                wr.flush();
                //OutputStream output = urlConnection.getOutputStream();
                Log.e("log_tag", "finsih flush");
                wr.close();
                Log.e("log_tag", "close");

                //接通資料庫

                int res = urlConnection.getResponseCode();
                Log.e("log_tag", "response code:" + res);
                if (res == 200) {
                    String json;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                    String line;
                    StringBuffer sb = new StringBuffer();

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    reader.close();
                    json = sb.toString();
                    Log.e("log_tag", "Success");
                    return json;
                    //return "Succses";
                } else
                    Log.e("log_tag", " " + res);

                wr.close();

            } catch (Exception e) {
                Log.e("log_tag", e.toString());
            } finally {

                urlConnection.disconnect();
            }
            return null;
        }

    }



    private class CreateCertificateTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Looper.prepare();
                // first generate a Keypair with Private and Public keys
                KeyPair keyPair = AWSIotKeystoreHelper.generatePrivateAndPublicKeys();
                // then create the CSR (uses SpongyCastle (BouncyCastle))
                String csrPemString = CsrHelper.generateCsrPemString(keyPair);

                // now create the create certificate request using that CSR
                CreateCertificateFromCsrRequest request = new CreateCertificateFromCsrRequest();
                request.setSetAsActive(true);
                request.setCertificateSigningRequest(csrPemString);

                // submit the request
                CreateCertificateFromCsrResult result = AppHelper.iotClient.createCertificateFromCsr(request);




                // now save the key and certificate in the keystore - the next
                // time the app starts it will use the stored one

              //  AWSIotKeystoreHelper.saveCertificateAndPrivateKey("default",result.getCertificatePem(), keyPair.getPrivate(),keystorePath,"iot_keystore", "password");
              //  clientKeyStore = AWSIotKeystoreHelper.getIotKeystore("default", keystorePath,"iot_keystore", "password");

                pk= Base64.encodeToString(keyPair.getPrivate().getEncoded(),Base64.DEFAULT);
                cec=result.getCertificatePem().toString();


                AttachPrincipalPolicyRequest policyAttachRequest = new AttachPrincipalPolicyRequest();
                policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
                policyAttachRequest.setPrincipal(result.getCertificateArn());
                AppHelper.iotClient.attachPrincipalPolicy(policyAttachRequest);




                return "Success";
            } catch (Exception e) {

                Toast.makeText(getApplicationContext(),"An error occurred while creating the CSR and calling create certificate API."+e,Toast.LENGTH_LONG).show();

                return "Fail"+e;
            }

        }


        protected void onPostExecute(String result) {

            new  conndecives().execute();
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();

        }
    }
}
