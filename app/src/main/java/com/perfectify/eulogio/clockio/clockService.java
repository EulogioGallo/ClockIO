package com.perfectify.eulogio.clockio;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.perfectify.eulogio.clockio.Models.AppInfo;
import com.perfectify.eulogio.clockio.Models.AppTime;
import com.perfectify.eulogio.clockio.Models.SQLiteHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Eulogio on 7/5/2014.
 */


public class clockService  extends IntentService {
    public final static String TIME_MESSAGE = "com.perfectify.eulogio.clockio.MESSAGE";
    private long tempTime = 0;

    // is service destroyed
    private boolean isDestroyed = false;
    // is the current app monitored
    private boolean isMonitored = false;
    // has a monitored app been touched
    private boolean isTouched = false;

    List<String> appsToMonitor = new ArrayList<String>();
    public SQLiteHelper db = new SQLiteHelper(this);

    private String TAG = "???:" + this.getClass().getSimpleName();

    // components to detect global touch
    private WindowManager mWindowManager;
    private View mView;

    // fileobserver  for screenshots
    private ScreenshotObserver  screenshotObserver;

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
                    // getting the monitored app also resets the isMonitored flag
                    String monitoredApp = isForeground(appsToMonitor);

                    if (isMonitored) {
                        isTouched = true;

                        // add time
                        try {
                            long start = System.currentTimeMillis();
                            Thread.sleep(1000);
                            long end = System.currentTimeMillis() - start;

                            // update time in db
                            addTime(monitoredApp, end);
                            Log.d("???:ACTIVE", end + "");

                            // reset time to be tracked
                            tempTime = 0;

                        } catch(InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    }

                    return false;
                }
            });

            mWindowManager.addView(mView, params);

            // see if screenshot directory is present
            File screenshots = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Screenshots/");

            // monitor screenshot directory
            if (screenshots.exists()) {
                screenshotObserver = new ScreenshotObserver(this, screenshots.getAbsolutePath());
            }
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

    // get most recent app on stack (not foreground)
    public String getAppOnStack() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > runningTaskInfo = am.getRunningTasks(2);

        ComponentName componentInfo = runningTaskInfo.get(1).topActivity;

        return componentInfo.getPackageName();
    }

    //checks to see if app specified is running in foreground
    public String isForeground(List<String> appsToMonitor) {
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

    // adds time to AppTime table
    public void addTime(String packageName, long time) {
        Log.d("???:addTime", packageName + " - " + time);
        AppTime appToUpdate = db.getAppTime(packageName);
        appToUpdate.setElapsedTime(appToUpdate.getElapsedTime() + time);
        db.updateAppTime(appToUpdate);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d("???: SERVICE STARTED", isDestroyed + "");

        // initialize time for monitored apps
        for (String appToMonitor : db.getMonitoredApps()) {
            appsToMonitor.add(appToMonitor);
        }

        // set appsToMonitor in ScreenshotObserver
        if (screenshotObserver != null) {
            screenshotObserver.setAppsToMonitor(appsToMonitor);
        }

        while(!isDestroyed) {
            if (isForeground(appsToMonitor) != null) {

                // monitor screenshot directory if monitored app in foreground
                if (screenshotObserver != null) {
                    screenshotObserver.startWatching();
                }

                // if touched recently, keep tracking time
                if (isTouched) {
                    try {
                        long start = System.currentTimeMillis();
                        Thread.sleep(1000);
                        long end = System.currentTimeMillis() - start;
                        tempTime += end;
                        Log.d("???:TEMPTIME", tempTime + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // check for 15sec of inactivity
                if (tempTime >= 15000) {
                    addTime(getForeground(), tempTime);
                    Log.d("???:INACTIVE", tempTime + "");
                    tempTime = 0;
                    isTouched = false;
                }
            // if we moved to another app that isn't monitored
            // add the time we had to the appropriate app
            } else if (tempTime > 0) {
                addTime(getAppOnStack(), tempTime);
                tempTime = 0;
            }

            // stop monitoring screenshots if no monitored app is in foreground
            if (screenshotObserver != null) {
                screenshotObserver.stopWatching();
            }
        }

        // make sure time is added once service is destroyed
        if (tempTime > 0) {
            addTime(getAppOnStack(), tempTime);
            tempTime = 0;
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

        // stop watching screenshot directory
        if (screenshotObserver != null) {
            screenshotObserver.stopWatching();
        }

        // When service is destroyed, call final activity
        Intent finalIntent = new Intent(this, FinalsActivity.class);
        finalIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(finalIntent);
        isDestroyed  = true;
        Log.d("???: SERVICE DESTROYED", isDestroyed + "");
        super.onDestroy();
    }
}
