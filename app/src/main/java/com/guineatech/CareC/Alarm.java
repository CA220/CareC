package com.guineatech.CareC;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by CAHans on 2018/5/28.
 */

public class Alarm extends Fragment {
    ListView lit;
    LayoutInflater inflaters;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.activity_alarm, container, false);
        lit = v.findViewById(R.id.list_alarm);
        inflaters = inflater;

        list_alarm();
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void list_alarm() {
        //  File file = new File("/data/data/com.guineatech.CareC/shared_prefs", "clock.xml");
        String deviceid = Frame.sap.getString("device", "");
        if (deviceid != "") {
            try {
                Log.e("S", deviceid);
                JSONArray jsa = new JSONArray(deviceid);
                alarm_item adapter = new alarm_item(jsa, inflaters, getContext());
                lit.setAdapter(adapter);
            } catch (JSONException e) {
                Log.e("Error", e.toString());
            }


        }

        Log.e("S", "");
    }
}
