package com.guineatech.CareC;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by OwO on 2018/3/3.
 */

public class my_profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_my_profile);

        View L1 = findViewById(R.id.LinearLayout1);
        View L2 = findViewById(R.id.LinearLayout2);
        View L3 = findViewById(R.id.LinearLayout3);
        View L4 = findViewById(R.id.LinearLayout4);
        View L5 = findViewById(R.id.LinearLayout5);
        L1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it =new Intent();
                it.setClass(my_profile.this,username.class);
                startActivity(it);

            }
        });
        L2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it =new Intent();
                it.setClass(my_profile.this,username.class);
                startActivity(it);

            }
        });
        L3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it =new Intent();
                it.setClass(my_profile.this,birthday.class);
                startActivity(it);
               
            }
        });
        L4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it =new Intent();
                it.setClass(my_profile.this,district.class);
                startActivity(it);

            }
        });
        L5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it =new Intent();
                it.setClass(my_profile.this,ForgotPassword.class);
                startActivity(it);

            }
        });
    }
}