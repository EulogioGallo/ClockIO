package com.perfectify.eulogio.clockio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.perfectify.eulogio.clockio.Models.AppTime;
import com.perfectify.eulogio.clockio.Models.SQLiteHelper;
import com.perfectify.eulogio.clockio.appTimeList.appTimeList;

import java.util.ArrayList;
import java.util.List;

public class FinalsActivity extends Activity {
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finals);

        SQLiteHelper db = new SQLiteHelper(this);

        List<String> packageNames = new ArrayList<String>();
        List<String> appNames = new ArrayList<String>();
        List<Long> appTimes = new ArrayList<Long>();

        for(AppTime appTime : db.getAllAppTime()) {
            packageNames.add(appTime.getPackageName());
            appNames.add(db.getAppInfo(appTime.getPackageName()).getAppName());
            appTimes.add(appTime.getElapsedTime());
        }

        appTimeList adapter = new appTimeList(this, packageNames, appNames, appTimes);

        lv = (ListView) findViewById(R.id.appTimeListView);
        lv.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // ### Keep this hidden for now
        // getMenuInflater().inflate(R.menu.finals, menu);
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

    public void exitClockIO(View view) {
        finish();
    }
}
