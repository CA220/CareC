package com.guineatech.CareC;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static android.Manifest.permission.CALL_PHONE;
/**
 * Created by Administrator on 2018/1/24.
 */

public class ClockActivity extends AppCompatActivity {
    TextView title, time;
    ImageView ring, warning;
    Button bt_close, bt_do;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup_ring);
        Intent i = this.getIntent();


        time = findViewById(R.id.time);
        title = findViewById(R.id.title);
        ring = findViewById(R.id.ring);
        warning = findViewById(R.id.warning);
        bt_close = findViewById(R.id.bt_close);
        bt_do = findViewById(R.id.bt_do);
        Log.e("D", FcmMessageService.tp);/*
        if (FcmMessageService.tp.equals("B")) {
            time.setVisibility(View.INVISIBLE);
            title.setVisibility(View.INVISIBLE);
            ring.setVisibility(View.INVISIBLE);
            warning.setVisibility(View.VISIBLE);

        }*/

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(ClockActivity.this, Sign_Rerister.class);
                startActivity(it);
                finish();
            }
        });
        switch (i.getStringExtra("type")) {
            case "B":
                if (ActivityCompat.checkSelfPermission(ClockActivity.this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    ActivityCompat.requestPermissions(ClockActivity.this, new String[]{CALL_PHONE}, 1);

                }
                time.setVisibility(View.INVISIBLE);
                title.setVisibility(View.INVISIBLE);
                ring.setVisibility(View.INVISIBLE);
                warning.setVisibility(View.VISIBLE);
                bt_do.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ActivityCompat.checkSelfPermission(ClockActivity.this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            ActivityCompat.requestPermissions(ClockActivity.this, new String[]{CALL_PHONE}, 2);

                        } else {
                            Intent intentDial = new Intent("android.intent.action.CALL", Uri.parse("tel:" + "104"));
                            startActivity(intentDial);
                        }

                    }
                });
            case "C":
                try {
                    Log.e("Lc", i.getStringExtra("time"));
                    time.setText(i.getStringExtra("time"));
                    bt_do.setText("Sleepy alarm clock");

                } catch (Exception e) {

                }

            case "CU":
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        int flag = 0;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == -1)
                flag = -1;
        }
        if (requestCode == 2 && flag != -1) {
            Intent intentDial = new Intent("android.intent.action.CALL", Uri.parse("tel:" + "119"));
            startActivity(intentDial);
        }

    }

}

