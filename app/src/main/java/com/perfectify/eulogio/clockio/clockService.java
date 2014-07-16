package com.perfectify.eulogio.clockio;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.perfectify.eulogio.clockio.Models.SQLiteHelper;

import java.util.List;

/**
 * Created by Eulogio on 7/5/2014.
 */


public class clockService  extends IntentService {
    public final static String TIME_MESSAGE = "com.perfectify.eulogio.clockio.MESSAGE";
    private long startTimeBasic;
    private long elapsedTimeBasic;
    private boolean isDestroyed = false;
    private boolean isMonitored = false;
    List <String> appsToMonitor;
    public SQLiteHelper db = new SQLiteHelper(this);

    private String TAG = "???:" + this.getClass().getSimpleName();
    private WindowManager mWindowManager;
    private View mView;

    public clockService() {
        super("clockService");
    }

    /* used http://stackoverflow.com/questions/21267322/detecting-user-activity-in-android as a reference */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,  flags, startId);

        // creates window  that will detect outside touch
        if (!isDestroyed) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    0, 0, 0, 0,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT
            );
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mView = new View(this);
            mView.setOnTouchListener(new OnTouchListener() {

                // onTouch method to detect only monitored app
                @SuppressLint("DefaultLocale")
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (isMonitored) {
                        String monitoredApp = isForeground(appsToMonitor);
                        Log.d(TAG,  monitoredApp);
                    }

                    return false;
                }
            });
            Log.d(TAG, "add View");
            mWindowManager.addView(mView, params);
        }

        return START_STICKY;
    }

    // get current running packagename
    public String getForeground() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > runningTaskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;

        return componentInfo.getPackageName();
    }

    //checks to see if app specified is running in foreground
    public String isForeground(List <String> appsToMonitor) {
        String foregroundApp = getForeground();
        for (String currentApp : appsToMonitor) {
            if (foregroundApp.equals(currentApp)) {
                isMonitored = true;
                return currentApp;
            }
        }
            isMonitored = false;
            return null;
    }

    public long getTimeElapsedBasic() {
        return elapsedTimeBasic;
    }

    // TODO: TRACK INDIVIDUAL APP TIMES
    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d("???: SERVICE STARTED", isDestroyed + "");

        appsToMonitor = db.getMonitoredApps();

        // Keep track of time until notification is pressed
        // TODO: Keep track of active app time
        startTimeBasic = System.nanoTime();
        elapsedTimeBasic = System.nanoTime() - startTimeBasic;
        Log.d("???: TIME ELAPSED", elapsedTimeBasic + "");

        while(!isDestroyed) {
            //check if the foreground app is on our list of apps to track
            String monitoredApp = isForeground(appsToMonitor);
            if (monitoredApp != null) {
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
    }

    @Override
    public void onDestroy() {
        // take care of windowmanager
        if (mWindowManager != null) {
            if (mView != null) {
                mWindowManager.removeView(mView);
            }
        }

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
