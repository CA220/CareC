package com.guineatech.CareC;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by CAHans on 2018/3/23.
 */

public class viewitem extends BaseAdapter {
    private JSONArray ElementsData;
    private LayoutInflater inflater;

    public viewitem(JSONArray data, LayoutInflater inflater) {

        this.ElementsData = data;
        this.inflater = inflater;

    }

    @Override
    public int getCount() {
        return ElementsData.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return ElementsData.getJSONObject(i);
        } catch (JSONException e) {
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
        ViewHolder holder;
        //當ListView被拖拉時會不斷觸發getView，為了避免重複加載必須加上這個判斷
        if (view == null) {

            holder = new ViewHolder();

            view = inflater.inflate(R.layout.listv, null);
            holder.Name = view.findViewById(R.id.user);
            holder.chart = view.findViewById(R.id.cht);
            holder.peo = view.findViewById(R.id.peo);
            holder.bed = view.findViewById(R.id.bed);
            holder.heart = view.findViewById(R.id.heart);
            holder.brd = view.findViewById(R.id.brd);
            holder.sleep = view.findViewById(R.id.sleep);
            holder.dot = view.findViewById(R.id.dot);
            holder.rlBorder = view.findViewById(R.id.llBorder);

            holder.bed.setImageResource(R.drawable.bed);
            holder.heart.setImageResource(R.drawable.heart);
            holder.brd.setImageResource(R.drawable.breath);
            holder.sleep.setImageResource(R.drawable.sleep);
            holder.peo.setImageResource(R.drawable.head_portrait);
            holder.dot.setImageResource(R.drawable.thdot);
            holder.dot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent godt = new Intent();
                    try {
                        godt.putExtra("devicename", ElementsData.getJSONObject(i).getString("nickname"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    godt.setClass(view.getContext(), decive_data.class);
                    view.getContext().startActivity(godt);
                }
            });
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        try {
            holder.Name.setText(ElementsData.getJSONObject(i).getString("nickname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.chart.setText("Owner");
        return view;
    }

    static class ViewHolder {
        RelativeLayout rlBorder;
        TextView Name;
        TextView chart;
        ImageView peo;
        ImageView bed;
        ImageView heart;
        ImageView brd;
        ImageView sleep;
        ImageView dot;

    }
}
