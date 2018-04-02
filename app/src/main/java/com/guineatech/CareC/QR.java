package com.guineatech.CareC;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class QR extends AppCompatActivity {
    private EditText edcode;
    private Button gowifit;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        edcode = findViewById(R.id.edit_bcode);
        gowifit=findViewById(R.id.gowifit);
        if (ActivityCompat.checkSelfPermission(QR.this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(QR.this,new String[]{CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,ACCESS_COARSE_LOCATION},1);
        }

      /*  textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(QR.this, MainActivity.class);
                startActivity(it);
            }
        }); */

      gowifit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if(edcode!=null)
              {
                  Intent it=new Intent();
                  it.putExtra("bcode",edcode.getText().toString());
                  it.setClass(QR.this,wifi.class);
                  startActivity(it);
              }
          }
      });

    }


    public void onScanQrcode(View v){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("掃描二维碼");
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
            } else {
                Toast.makeText(this, "掃描成功，值: " + result.getContents(), Toast.LENGTH_LONG).show();
                edcode.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
