package com.perfectify.eulogio.clockio.signinPreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;


import com.perfectify.eulogio.clockio.R;

/**
 * Created by Eulogio on 8/7/2014.
 */
public class SigninPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.signin);

        // change dialogue of already signed in
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        if (!sp.getString("signin", "").isEmpty()) {
            getPreferenceScreen().findPreference("signin").setTitle("Sign In as Different User");
        }
    }
}
