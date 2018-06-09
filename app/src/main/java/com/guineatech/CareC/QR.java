package com.guineatech.CareC;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class QR extends AppCompatActivity {
    String ecode;
    TextView title, id_device;
    Button bt_r, bt_c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_corim);
        if (ActivityCompat.checkSelfPermission(QR.this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(QR.this,new String[]{CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,ACCESS_COARSE_LOCATION},1);
        } else {
            onScanQrcode();
        }
        title = findViewById(R.id.tv_title);
        id_device = findViewById(R.id.tv_title2);
        bt_r = findViewById(R.id.bt_rescan);
        bt_c = findViewById(R.id.bt_confirm);

        bt_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScanQrcode();
            }
        });

        bt_c.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if (ecode != null)
              {
                  Intent it=new Intent();
                  it.putExtra("bcode", ecode);
                  it.setClass(QR.this, addmattre.class);
                  startActivity(it);
                  finish();
              }
          }
      });

    }


    public void onScanQrcode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("掃描二维碼");
        integrator.setCaptureActivity(Capt.class);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "掃描取消！", Toast.LENGTH_LONG).show();
                title.setText("Scan clean");
            } else {
                Toast.makeText(this, "掃描成功，值: " + result.getContents(), Toast.LENGTH_LONG).show();
                title.setText("Success");
                ecode = result.getContents();
                id_device.setText("ID:" + ecode);
                id_device.setVisibility(View.VISIBLE);
                bt_c.setVisibility(View.VISIBLE);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        int flag = 0;
        for (int i = 0; i < grantResults.length; i++)
            if (grantResults[i] == -1)
                flag = -1;
        if (flag != -1)
            onScanQrcode();
        else
            finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

