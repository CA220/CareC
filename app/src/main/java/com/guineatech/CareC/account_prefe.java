package com.guineatech.CareC;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import java.io.File;

/**
 * Created by CAHans on 2018/5/28.
 */

public class account_prefe extends PreferenceFragment {
    Preference logout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.acount_preferences);
        getPreferenceManager().setSharedPreferencesName("account_set");

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if ("logout".equals(preference.getKey())) {
            File logindata = new File("/data/data/com.guineatech.CareC/shared_prefs", "Data.xml");
            logindata.delete();
            File accf = new File("/data/data/com.guineatech.CareC/shared_prefs", "account_set.xml");
            accf.delete();
            File did = new File("/data/data/com.guineatech.CareC/shared_prefs", "userhas.xml");
            did.delete();
            Intent it = new Intent();
            it.setClass(getActivity(), Sign_Rerister.class);
            startActivity(it);

        }
        return true;// super.onPreferenceTreeClick(preferenceScreen, preference);
    }


}
