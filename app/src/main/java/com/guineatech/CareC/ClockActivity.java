package com.guineatech.CareC;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2018/1/24.
 */

public class ClockActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bottom_frame);

        //框
        new AlertDialog.Builder(ClockActivity.this).setTitle("鬧鐘").setMessage("快起床~")
                .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClockActivity.this.finish();
                    }
                }).show();
    }

}

