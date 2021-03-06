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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by CAHans on 2018/6/7.
 */

public class wakeme_clock extends AppCompatActivity {
    TimePicker timePicker;
    ImageView iv_ok;
    int h, m, next;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wake_me_up_setting);
        Intent it = this.getIntent();
        next = it.getIntExtra("next", 0);
        timePicker = findViewById(R.id.timePicker);

        iv_ok = findViewById(R.id.iv_ok);
        h = timePicker.getHour();
        m = timePicker.getMinute();
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
                String toast_time = "Time: ";
                int tt = 0;
                if (time / 1000 > 60 * 60) {
                    tt = (int) (time / 1000 * 60);
                    toast_time += (tt / 60) + " hr " + (tt % 60) + " min";
                } else if (time / 1000 > 60) {
                    tt = (int) (time / 1000 * 60);
                    toast_time += tt + " min";
                } else {
                    toast_time = "0 min";
                }
                String tp_clock = "";
                String apm = "AM", tph = "";
                String tpm = "";
                JSONObject jso = new JSONObject();
                try {
                    jso.put("na", "Daulf");

                    if (h > 12) {
                        h -= 12;
                        apm = "PM";
                    }
                    if (h < 10) {
                        tph = "0" + h;
                    } else {
                        tph = String.valueOf(h);
                    }

                    if (m < 10) {
                        tpm = "0" + m;
                    } else {
                        tpm = String.valueOf(m);
                    }
                    jso.put("time", tph + ":" + tpm + " " + apm);
                    jso.put("rep", next);
                    jso.put("altr", 1);
                    if (Frame.clock.getString("clock", "") != "") {
                        tp_clock = Frame.clock.getString("clock", "") + "," + jso.toString();
                    } else {
                        tp_clock = jso.toString();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(wakeme_clock.this, ClockActivity.class);
                intent.putExtra("type", "C");
                intent.putExtra("time", tph + ":" + tpm + " " + apm);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), next, intent, 0);
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, pendingIntent);
                Toast.makeText(getApplicationContext(), "Atrel " + toast_time, Toast.LENGTH_LONG).show();
                //    Log.e("FF","De "+tp_clock+" next "+next);
                Frame.clock.edit()
                        .putInt("tol", next)
                        .putString("clock", tp_clock)
                        .commit();
                finish();
            }
        });
    }
}
