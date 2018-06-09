package com.guineatech.CareC;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by CAHans on 2018/5/28.
 */

public class Alarm extends Fragment {
    ListView lit;
    LayoutInflater inflaters;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.activity_alarm, container, false);
        lit = v.findViewById(R.id.list_clock);
        inflaters = inflater;


        return v;
    }
}
