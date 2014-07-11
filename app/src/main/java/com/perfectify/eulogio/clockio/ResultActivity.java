package com.perfectify.eulogio.clockio;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.perfectify.eulogio.clockio.Models.SQLiteHelper;
import com.perfectify.eulogio.clockio.R;

public class ResultActivity extends Activity {
    private Intent mServiceIntent;

    // create db if not already present
    public SQLiteHelper db = new SQLiteHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get app to launch from mainActivity intent
        Intent  mainActivityIntent = getIntent();
        String appToLaunch = mainActivityIntent.getStringExtra(MainActivity.APP_MESSAGE); //no longer app

        Log.d("???: APP_TO_LAUNCH", appToLaunch);

        // this is null when ResultActivity started from notification
        if (appToLaunch != null) {
            //  intent for returning to this activity when notification is pressed
            Intent thisIntent = new Intent(this, this.getClass());
            PendingIntent pendingThisIntent = PendingIntent.getActivity(
                    this, 0, thisIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create notification  that ClockIO will run in the background
            // while the desired app is  launched

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setContentIntent(pendingThisIntent)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("ClockIO")
                            .setContentText(appToLaunch)
                            .setAutoCancel(true);

            int mNotificationId = 001;
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());

            Log.d("???: NOTIFICATION", "Notification Created: " + appToLaunch);

            // send app info to background service
            mServiceIntent = new Intent(this, clockService.class);
            //mServiceIntent.setData(Uri.parse(appToLaunch));
            startService(mServiceIntent);

            // start home screen
            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result, menu);
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

    // This function will make this activity act differently
    // when notification is pressed
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.stopService(mServiceIntent);
    }
}
