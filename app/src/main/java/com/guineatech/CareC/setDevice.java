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
import android.widget.Toast;


import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

/**
 * Created by CAHans on 2018/1/24.
 */

public class setDevice extends AppCompatActivity {
    private ProgressDialog waitDialog;
    private String wifissid="",wifipwd="",endpoint="a2hd4hpd193y9c.iot.us-west-2.amazonaws.com";
    String [] keycert=new String[2];
    String AWS_IOT_POLICY_NAME = "tre-Policy";
    private AlertDialog userDialog;
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


    public class conndecives extends AsyncTask<Void,Void,String>
    {

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
                FileReader [] in = new FileReader[]{new FileReader(getFilesDir()+"/ac8cert.pem"),new FileReader(getFilesDir()+"/ac8pr.key") };
                URL url=new URL("http://192.168.1.1/upload.php");//php的位置
                //  URL url=new URL("http://192.192.140.221/~D10416247/conn/upload.php");//php的位置
                urlConnection=(HttpURLConnection) url.openConnection();//對資料庫打開連結

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setReadTimeout(2000);
                urlConnection.setConnectTimeout(5000);
                // urlConnection.setRequestProperty("User-agent", "IE/6.0");
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
                //urlConnection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");

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
                        reader = new BufferedReader(new StringReader(keycert[0]));


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


            }
            finally {

                urlConnection.disconnect();
            }
            return "Fail";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            waitDialog.dismiss();

        }
    }


//建立金鑰
    private class CreateCertificateTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                //Looper.prepare();
                // first generate a Keypair with Private and Public keys
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(2048);
                KeyPair keyPair = kpg.generateKeyPair();


                PrivateKey priv = keyPair.getPrivate();
                byte[] privBytes = priv.getEncoded();

                PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(privBytes);
                ASN1Encodable encodable = pkInfo.parsePrivateKey();
                ASN1Primitive primitive = encodable.toASN1Primitive();
                //  byte[] privateKeyPKCS1 = primitive.getEncoded();
                keycert[0]=Base64.encodeToString(primitive.getEncoded(),0);
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

                keycert[0]= Base64.encodeToString(keyPair.getPrivate().getEncoded(),Base64.DEFAULT);
                keycert[1]=result.getCertificatePem().toString();
                keycert[0]="-----BEGIN RSA PRIVATE KEY----- \n"+keycert[0]+"-----END RSA PRIVATE KEY-----\n";

                AttachPrincipalPolicyRequest policyAttachRequest = new AttachPrincipalPolicyRequest();
                policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
                policyAttachRequest.setPrincipal(result.getCertificateArn());
                AppHelper.iotClient.attachPrincipalPolicy(policyAttachRequest);




                return "Success";
            } catch (Exception e) {



                return "An error occurred while creating the CSR and calling create certificate API ";
            }

        }


        protected void onPostExecute(String result) {

            new  conndecives().execute();


        }
    }


    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            if(exit)
            {
                Intent it =new Intent();
                it.setClass(setDevice.this,Mainpage.class);
                startActivity(it);
                finish();
            }
                else
                    {

                    }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }
}
