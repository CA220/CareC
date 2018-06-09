package com.guineatech.CareC;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;

/**
 * Created by CAHans on 2018/5/28.
 */

public class wakeme extends Fragment {
    ListView lit;
    LayoutInflater inflaters;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_wakeup, container, false);
        lit = v.findViewById(R.id.list_clock);
        inflaters = inflater;


        ImageView add = v.findViewById(R.id.iv_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), wakeme_clock.class);
                startActivity(intent);

            }
        });


        return v;
    }

    private void list_clock() {
        File file = new File("/data/data/com.guineatech.CareC/shared_prefs", "clock.xml");
    }
}
