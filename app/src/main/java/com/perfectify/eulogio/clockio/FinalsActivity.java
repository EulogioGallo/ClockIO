package com.perfectify.eulogio.clockio;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.perfectify.eulogio.clockio.Models.AppTime;
import com.perfectify.eulogio.clockio.Models.SQLiteHelper;
import com.perfectify.eulogio.clockio.appList.appList;
import com.perfectify.eulogio.clockio.appTimeList.appTimeList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FinalsActivity extends Activity {
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finals);

        SQLiteHelper db = new SQLiteHelper(this);

        List<String> appNames = new ArrayList<String>();
        List<Long> appTimes = new ArrayList<Long>();

        for(AppTime appTime : db.getAllAppTime()) {
            appNames.add(appTime.getPackageName());
            appTimes.add(appTime.getElapsedTime());
        }

        appTimeList adapter = new appTimeList(this, appNames, appTimes);

        lv = (ListView) findViewById(R.id.appTimeListView);
        lv.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.finals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
