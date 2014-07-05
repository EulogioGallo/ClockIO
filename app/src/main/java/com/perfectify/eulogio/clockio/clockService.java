package com.perfectify.eulogio.clockio;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Eulogio on 7/5/2014.
 */
public class clockService  extends IntentService {
    public final static String TIME_MESSAGE = "com.perfectify.eulogio.clockio.MESSAGE";
    private long startTimeBasic;
    private long elapsedTimeBasic;

    public clockService() {
        super("clockService");
    }

    //checks to see if app specified is running in foreground
    public boolean isForeground(String appThatLaunched) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > runningTaskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equals(appThatLaunched))
            return true;

        return false;
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        //Get data from the incoming Intent
        String appToLaunch = workIntent.getDataString();

        // Create notification that ClockIO will run in background
        // while  the desired app is launched
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("ClockIO")
                        .setContentText(appToLaunch);

        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        // Launch selected app
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(appToLaunch);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchIntent);


        // Keep track of time until notification is pressed
        // TODO: Keep track of active app time
        startTimeBasic = System.nanoTime();
        elapsedTimeBasic = System.nanoTime() - startTimeBasic;
        Log.d("TIME ELAPSED", elapsedTimeBasic + "");

        while(isForeground(appToLaunch)) {
            try {
                // 10 second interval for now
                Thread.sleep(3000);
                elapsedTimeBasic = System.nanoTime() - startTimeBasic;
                Log.d("TIME ELAPSED", elapsedTimeBasic + "");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        // When service is destroyed, call result activity
        Intent resultIntent = new Intent(this, ResultActivity.class);
        resultIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra(TIME_MESSAGE, elapsedTimeBasic);
        startActivity(resultIntent);
    }
}
