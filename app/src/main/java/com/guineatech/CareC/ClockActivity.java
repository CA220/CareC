package com.guineatech.CareC;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
        Log.e("D", FcmMessageService.tp);
        if (FcmMessageService.tp.equals("B")) {
            time.setVisibility(View.INVISIBLE);
            title.setVisibility(View.INVISIBLE);
            ring.setVisibility(View.INVISIBLE);
            warning.setVisibility(View.VISIBLE);

        }

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(ClockActivity.this, Sign_Rerister.class);
                startActivity(it);
                finish();
            }
        });


    }

}

