package com.guineatech.CareC;


import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

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

            view = inflater.inflate(R.layout.listview_item, null);
            holder.name = view.findViewById(R.id.tv_user);
            holder.character = view.findViewById(R.id.tv_cht);
            holder.userph = view.findViewById(R.id.iv_userph);
            holder.bed = view.findViewById(R.id.iv_bed);
            holder.heart = view.findViewById(R.id.iv_heart);
            holder.breathe = view.findViewById(R.id.iv_breathe);
            holder.iv_caregiver = view.findViewById(R.id.iv_caregiver);
            holder.iv_inbed = view.findViewById(R.id.iv_inbed);
            holder.iv_owner = view.findViewById(R.id.iv_owner);

            holder.rlBorder = view.findViewById(R.id.rt_item);

            holder.bed.setImageResource(R.drawable.ic_bed);
            holder.heart.setImageResource(R.drawable.heart_xxxhdpi);
            holder.breathe.setImageResource(R.drawable.ic_breathe);
            holder.userph.setImageResource(R.mipmap.ic_launcher_round);
            holder.iv_owner.setImageResource(R.drawable.king_xxxhdpi);
            holder.iv_inbed.setImageResource(R.drawable.exit_xxxhdpi);
            holder.iv_caregiver.setImageResource(R.drawable.defend_xxxhdpi);

            holder.rlBorder.setOnClickListener(new View.OnClickListener() {
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
            holder.name.setText(ElementsData.getJSONObject(i).getString("nickname"));
            Log.e("FS", ElementsData.getJSONObject(i).getString("deviceid"));
            FirebaseMessaging.getInstance().subscribeToTopic(ElementsData.getJSONObject(i).getString("deviceid"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.character.setText("Owner-");
        return view;
    }

    static class ViewHolder {
        RelativeLayout rlBorder;
        TextView name;
        TextView character;
        ImageView userph, iv_caregiver, iv_owner, iv_inbed;

        ImageView bed;
        ImageView heart;
        ImageView breathe;


    }
}
