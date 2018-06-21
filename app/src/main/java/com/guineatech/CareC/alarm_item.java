package com.guineatech.CareC;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by CAHans on 2018/6/11.
 */

public class alarm_item extends BaseAdapter {
    private JSONArray tol;
    private LayoutInflater inflater;
    private Context context;

    public alarm_item(JSONArray data, LayoutInflater inflater, Context c) {

        this.tol = data;
        this.inflater = inflater;
        this.context = c;
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
        alarm_item.Viewnotic holder;
        //當ListView被拖拉時會不斷觸發getView，為了避免重複加載必須加上這個判斷
        if (view == null) {

            view = inflater.inflate(R.layout.listview_alarm, null);
            holder = new alarm_item.Viewnotic();
            holder.rl_alarm = view.findViewById(R.id.rl_not);
            holder.cht = view.findViewById(R.id.tv_cht);
            // holder.more=view.findViewById(R.id.iv_more);
            holder.nick = view.findViewById(R.id.tv_nick);
            holder.pr = view.findViewById(R.id.iv_pr);
            holder.sw_alarm = view.findViewById(R.id.sw_alarm);
            holder.bed = view.findViewById(R.id.iv_bed);

            holder.pr.setImageResource(R.drawable.logo_xxxhdpi);
            //  holder.more.setImageResource(R.drawable.more_xxxhdpi);
            holder.bed.setImageResource(R.drawable.caregiver_xxxhdpi);

            view.setTag(holder);
        } else {
            holder = (alarm_item.Viewnotic) view.getTag();
        }
        try {
            holder.sw_alarm.setChecked(true);
            holder.nick.setText(tol.getJSONObject(i).getString("nickname"));

            holder.cht.setText("Owner");
            holder.rl_alarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            holder.sw_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!compoundButton.isChecked()) {
                        try {
                            Toast.makeText(context, tol.getJSONObject(i).getString("nickname") + " notiction is close", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    static class Viewnotic {
        RelativeLayout rl_alarm;
        TextView cht, nick;
        ImageView pr, more, bed;
        Switch sw_alarm;
    }
}
