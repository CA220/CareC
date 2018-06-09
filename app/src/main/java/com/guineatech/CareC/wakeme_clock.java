package com.guineatech.CareC;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by CAHans on 2018/6/7.
 */

public class wakeme_clock extends AppCompatActivity {
    TimePicker timePicker;
    ImageView iv_ok;
    int h, m;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wake_me_up_setting);

        timePicker = findViewById(R.id.timePicker);

        iv_ok = findViewById(R.id.iv_ok);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hc, int mc) {
                h = hc;
                m = mc;
            }
        });

        iv_ok.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {


                long startime = System.currentTimeMillis();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 这里时区需要设置一下，不然会有8个小时的时间差
                calendar.set(Calendar.MINUTE, m);
                calendar.set(Calendar.HOUR_OF_DAY, h);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long playtime = calendar.getTimeInMillis();
                if (startime > playtime) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    playtime = calendar.getTimeInMillis();
                }
                long time = playtime - startime;
                long firstTime = SystemClock.elapsedRealtime() + time;

                Intent intent = new Intent(wakeme_clock.this, ClockActivity.class);


                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);                //時間
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, pendingIntent);
                Toast.makeText(getApplicationContext(), "Atrel " + time / 1000, Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }
}
