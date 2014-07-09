package com.perfectify.eulogio.clockio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import java.util.concurrent.TimeUnit;

public class FinalsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get intent from service
        Intent resultIntent = getIntent();
        long timeElapsedBasic = resultIntent.getLongExtra(clockService.TIME_MESSAGE, 1000001);
        String time = String.format("%d min, %d sec",
                TimeUnit.NANOSECONDS.toMinutes(timeElapsedBasic),
                TimeUnit.NANOSECONDS.toSeconds(timeElapsedBasic) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(timeElapsedBasic))
        );

        TextView resultTime = new TextView(this);
        resultTime.setTextSize(12);
        resultTime.setText("Time: " + time);

        setContentView(resultTime);
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
