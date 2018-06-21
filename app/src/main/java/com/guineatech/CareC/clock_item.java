package com.guineatech.CareC;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by CAHans on 2018/6/10.
 */

public class clock_item extends BaseAdapter {
    private JSONArray tol;
    private LayoutInflater inflater;

    public clock_item(JSONArray data, LayoutInflater inflater) {

        this.tol = data;
        this.inflater = inflater;

    }

    @Override
    public int getCount() {
        return tol.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return tol.getJSONObject(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        clock_item.ViewClock holder;
        //當ListView被拖拉時會不斷觸發getView，為了避免重複加載必須加上這個判斷
        if (view == null) {

            view = inflater.inflate(R.layout.listview_wakeup, null);
            holder = new clock_item.ViewClock();
            holder.rl_clock = view.findViewById(R.id.rl_clock);
            holder.time = view.findViewById(R.id.tv_time);
            holder.more = view.findViewById(R.id.iv_more);
            holder.nick = view.findViewById(R.id.tv_nick);
            holder.pr = view.findViewById(R.id.iv_pr);
            holder.sw_clock = view.findViewById(R.id.sw_clokn);

            holder.pr.setImageResource(R.drawable.logo_xxxhdpi);
            holder.more.setImageResource(R.drawable.more_xxxhdpi);


            //view.setTag(holder);
        } else {
            holder = (clock_item.ViewClock) view.getTag();
        }
        try {
            holder.sw_clock.setChecked(true);
            holder.nick.setText(tol.getJSONObject(i).getString("na"));
            holder.time.setText(tol.getJSONObject(i).getString("time"));
            holder.rl_clock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    static class ViewClock {
        RelativeLayout rl_clock;
        TextView time, nick;
        ImageView pr, more;
        Switch sw_clock;
    }
}
