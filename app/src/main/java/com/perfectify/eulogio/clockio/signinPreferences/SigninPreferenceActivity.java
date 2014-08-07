package com.perfectify.eulogio.clockio.signinPreferences;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Eulogio on 8/7/2014.
 */
public class SigninPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SigninPreferenceFragment()).commit();
    }
}
