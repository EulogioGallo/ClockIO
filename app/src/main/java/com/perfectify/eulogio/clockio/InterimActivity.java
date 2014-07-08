package com.perfectify.eulogio.clockio;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.perfectify.eulogio.clockio.R;

public class InterimActivity extends Activity {

    public final String RESULT_MESSAGE = "com.perfectify.eulogio.clockio.RESULT";

    //make  bound variables
    clockService myService;
    boolean isBound = false;

    //ServiceConnections for bound service
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override // called when activity binds successfully to service
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            clockService.LocalBinder binder = (clockService.LocalBinder) iBinder;
            myService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_interim);

        //Get MainActivity Intent
        Intent mainActivityIntent = getIntent();
        String appToLaunch = mainActivityIntent.getStringExtra(MainActivity.APP_MESSAGE);

        // intent for returning to this activity when notification is pressed
        Intent interimIntent = new Intent(this, this.getClass());
        PendingIntent pendingInterimIntent = PendingIntent.getActivity(
                this, 0, interimIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create notification that ClockIO will run in background
        // while  the desired app is launched
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentIntent(pendingInterimIntent)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("ClockIO")
                        .setContentText(appToLaunch);

        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        Log.d("NOTIFICATION", "Notification Created: " + appToLaunch);

        // send app info to background service
        Intent mServiceIntent = new Intent(InterimActivity.this, clockService.class);
        mServiceIntent.setData(Uri.parse(appToLaunch));
        bindService(mServiceIntent, mConnection, this.BIND_AUTO_CREATE);

        Log.d("BOUND", (isBound ? "True" : "False"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.interim, menu);
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

    @Override
    public void onNewIntent(Intent intent) {
        // only called when notification is pressed
        // now we want the service info on the running app
        long elapsedTimeBasic = myService.getTimeElapsedBasic();
        Log.d("NEW INTENT", elapsedTimeBasic + "");
        Intent resultIntent = new Intent(this, ResultActivity.class);
        resultIntent.putExtra(RESULT_MESSAGE, elapsedTimeBasic);

        unbindService(mConnection);

        startActivity(resultIntent);
    }
}
