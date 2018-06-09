package com.guineatech.CareC;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.AttachThingPrincipalRequest;
import com.amazonaws.services.iot.model.CreateCertificateFromCsrRequest;
import com.amazonaws.services.iot.model.CreateCertificateFromCsrResult;
import com.amazonaws.services.iot.model.CreateThingRequest;
import com.amazonaws.services.iot.model.CreateThingResult;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

/**
 * Created by CAHans on 2018/5/31.
 */

public class pkcert extends AsyncTask<Void, Void, Intent> {
    private static String[] keycert = new String[2];
    private String deviceid;
    private int f;
    private Context vc;

    pkcert(String id, Context v, int flag) {
        deviceid = id;
        vc = v;
        f = flag;
    }

    @Override
    protected Intent doInBackground(Void... voids) {

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair keyPair = kpg.generateKeyPair();


            PrivateKey priv = keyPair.getPrivate();
            byte[] privBytes = priv.getEncoded();

            PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(privBytes);
            ASN1Encodable encodable = pkInfo.parsePrivateKey();
            ASN1Primitive primitive = encodable.toASN1Primitive();

            keycert[0] = Base64.encodeToString(primitive.getEncoded(), 0);
            // then create the CSR (uses SpongyCastle (BouncyCastle))
            String csrPemString = CsrHelper.generateCsrPemString(keyPair);

            // now create the create certificate request using that CSR
            CreateCertificateFromCsrRequest request = new CreateCertificateFromCsrRequest();
            request.setSetAsActive(true);
            request.setCertificateSigningRequest(csrPemString);

            // submit the request
            CreateCertificateFromCsrResult result = AppHelper.iotClient.createCertificateFromCsr(request);
            keycert[1] = result.getCertificatePem().toString();
            keycert[0] = "-----BEGIN RSA PRIVATE KEY----- \n" + keycert[0] + "-----END RSA PRIVATE KEY-----\n";


       /*    AWSIotKeystoreHelper.saveCertificateAndPrivateKey(deviceid,
                    result.getCertificatePem(), keyPair.getPrivate(),  vc.getFilesDir().getPath(),
                   deviceid, AppHelper.serial);*/

         /*   if(f==1)
            {
                FileWriter fw = new FileWriter( "/data/data/com.guineatech.CareC/ac8cert.pem", false);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(keycert[1]);
                bw.close();
                FileWriter ew = new FileWriter("/data/data/com.guineatech.CareC/ac8pr.key", false);
                BufferedWriter rr = new BufferedWriter(ew);
                rr.write(keycert[0]);
                rr.close();
            }
            else*/
            String IOT_thing_r = "IOT_thing_r";
            if (f == 0 && deviceid != null) {

                Intent r = new Intent();
                r.setAction(IOT_thing_r);
                r.putExtra("IOT", "STEP_1");
                vc.sendBroadcast(r);
                AttachPrincipalPolicyRequest policyAttachRequest = new AttachPrincipalPolicyRequest();
                policyAttachRequest.setPolicyName(AppHelper.AWS_IOT_POLICY_NAME);
                policyAttachRequest.setPrincipal(result.getCertificateArn());
                AppHelper.iotClient.attachPrincipalPolicy(policyAttachRequest);
                CreateThingRequest ct = new CreateThingRequest().withThingName(deviceid);
                CreateThingResult cresult = AppHelper.iotClient.createThing(ct);
                AttachThingPrincipalRequest attachThingPrincipalRequest = new AttachThingPrincipalRequest();
                attachThingPrincipalRequest.setThingName(deviceid);
                attachThingPrincipalRequest.setPrincipal(result.getCertificateArn());
                AppHelper.iotClient.attachThingPrincipal(attachThingPrincipalRequest);
                Intent i = new Intent();
                i.setAction(IOT_thing_r);
                i.putExtra("IOT", "STEP_2");
                return i;
            }
        } catch (Exception e) {
            Log.e("Tag", "No " + e.toString());
            Intent i = new Intent();
            i.setAction("IOT_thing_r");
            i.putExtra("IOT", "Eorro");
            i.putExtra("msg", e.toString());
            return i;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Intent o) {
        super.onPostExecute(o);
        if (o != null) {
            vc.sendBroadcast(o);
            Log.e("D", "Step1");
        }

    }


}





