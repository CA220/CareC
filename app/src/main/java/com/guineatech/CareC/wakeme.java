package com.guineatech.CareC;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

/**
 * Created by CAHans on 2018/5/28.
 */

public class wakeme extends Fragment {
    ListView lit;
    LayoutInflater inflaters;
    int tol_next;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_wakeup, container, false);
        lit = v.findViewById(R.id.list_clock);
        inflaters = inflater;


        ImageView add = v.findViewById(R.id.iv_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("next", tol_next);
                intent.setClass(getActivity(), wakeme_clock.class);
                startActivity(intent);

            }
        });
        list_clock();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        list_clock();
    }

    private void list_clock() {
        //  File file = new File("/data/data/com.guineatech.CareC/shared_prefs", "clock.xml");
        int tol = Frame.clock.getInt("tol", 0);
        if (tol != 0) {
            try {
                Log.e("S", Frame.clock.getString("clock", ""));
                JSONArray jsa = new JSONArray("[" + Frame.clock.getString("clock", "") + "]");
                clock_item adapter = new clock_item(jsa, inflaters);
                lit.setAdapter(adapter);
            } catch (JSONException e) {
                Log.e("Error", e.toString());
            }


        }
        tol_next = tol + 1;
        Log.e("S", tol_next + "");
    }
}
