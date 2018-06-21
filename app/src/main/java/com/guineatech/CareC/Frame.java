package com.guineatech.CareC;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by CAHans on 2018/5/28.
 */

public class Frame extends AppCompatActivity {
    public static SharedPreferences sap, clock;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectecFragment = null;
            int f = 0;
            //  PreferenceFragment selectecFragment=null;
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    selectecFragment = new Mainpage();
                    break;
                case R.id.navigation_alarm:

                    selectecFragment = new Alarm();
                    break;
                case R.id.navigation_wakeme:

                    selectecFragment = new wakeme();
                    break;
                case R.id.navigation_account:
                    selectecFragment = new account_prefe();


                    break;
            }

            getFragmentManager().beginTransaction().replace(R.id.frame, selectecFragment).commit();
            return true;
        }
    };
    private BroadcastReceiver counterActionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.e("DB", " " + intent.getStringExtra("DB"));
            if (intent.getStringExtra("DB").equals("R")) {
                File file = new File("/data/data/com.guineatech.CareC/shared_prefs", "userhas.xml");
                file.delete();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        getFragmentManager().beginTransaction().replace(R.id.frame, new Mainpage()).commit();

        sap = getSharedPreferences("userhas", 0);
        clock = getSharedPreferences("clock", 0);
    }

    protected void onResume() {
        super.onResume();
        IntentFilter counterActionFilter = new IntentFilter("DB");
        registerReceiver(counterActionReceiver, counterActionFilter);
    }
/*
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(counterActionReceiver);
    }*/

    public static class BottomNavigationViewHelper {

        @SuppressLint("RestrictedApi")
        public static void disableShiftMode(BottomNavigationView navigationView) {

            BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);

                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
                    itemView.setShiftingMode(false);
                    itemView.setChecked(itemView.getItemData().isChecked());
                }

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
