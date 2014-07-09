package com.perfectify.eulogio.clockio;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * Created by Eulogio on 7/5/2014.
 */


public class clockService  extends IntentService {
    public final static String TIME_MESSAGE = "com.perfectify.eulogio.clockio.MESSAGE";
    private Intent launchIntent;
    private long startTimeBasic;
    private long elapsedTimeBasic;
    private boolean isDestroyed = false;

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

    public long getTimeElapsedBasic() {
        return elapsedTimeBasic;
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d("???: SERVICE STARTED", isDestroyed + "");
        //Get data from the incoming Intent
        String appToLaunch = workIntent.getDataString();

        Log.d("???: SERVICE DATA", appToLaunch);


        // Launch selected app
        launchIntent = getPackageManager().getLaunchIntentForPackage(appToLaunch);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchIntent);


        // Keep track of time until notification is pressed
        // TODO: Keep track of active app time
        startTimeBasic = System.nanoTime();
        elapsedTimeBasic = System.nanoTime() - startTimeBasic;
        Log.d("???: TIME ELAPSED", elapsedTimeBasic + "");

        while(!isDestroyed) {// while(isForeground(appToLaunch)) {
            try {
                // 1 second interval for now
                Thread.sleep(1000);
                elapsedTimeBasic = System.nanoTime() - startTimeBasic;
                Log.d("???:  TIME ELAPSED", elapsedTimeBasic + "");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        // When service is destroyed, call final activity
        Intent finalIntent = new Intent(this, FinalsActivity.class);
        finalIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finalIntent.putExtra(TIME_MESSAGE, elapsedTimeBasic);
        startActivity(finalIntent);
        isDestroyed  = true;
        Log.d("???: SERVICE DESTROYED", isDestroyed + "");
        super.onDestroy();
    }
}
