package com.perfectify.eulogio.clockio;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.perfectify.eulogio.clockio.Models.SQLiteHelper;

import java.util.List;

/**
 * Created by Eulogio on 7/5/2014.
 */


public class clockService  extends IntentService implements OnTouchListener {
    public final static String TIME_MESSAGE = "com.perfectify.eulogio.clockio.MESSAGE";
    private Intent launchIntent;
    private long startTimeBasic;
    private long elapsedTimeBasic;
    private boolean isDestroyed = false;
    private boolean isMonitored = false;
    public SQLiteHelper db = new SQLiteHelper(this);

    private String TAG = "???:" + this.getClass().getSimpleName();
    private WindowManager mWindowManager;
    private LinearLayout touchLayout;

    public clockService() {
        super("clockService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // create linear layout
        touchLayout = new LinearLayout(this);
        // set layout width to 30px and height is equal to full screen
        LayoutParams lp = new LayoutParams(30, LayoutParams.MATCH_PARENT);
        touchLayout.setLayoutParams(lp);
        // set color if you want layout visible on screen
        touchLayout.setBackgroundColor(Color.CYAN);
        // set on touch listener
        touchLayout.setOnTouchListener(this);

        // fetch window manager object
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // set layout parameter of window manager
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                30, // width of layout 30px
                WindowManager.LayoutParams.MATCH_PARENT, // height is equal to fullscreen
                WindowManager.LayoutParams.TYPE_PHONE, // Type Ohone, These are non-application windows providing user interaction with the phone (in particular incoming calls).
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // this window won't ever get key input focus
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        Log.d(TAG, "add View");

        mWindowManager.addView(touchLayout, mParams);
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

        List <String> appsToMonitor = db.getMonitoredApps();

        // Keep track of time until notification is pressed
        // TODO: Keep track of active app time
        startTimeBasic = System.nanoTime();
        elapsedTimeBasic = System.nanoTime() - startTimeBasic;
        Log.d("???: TIME ELAPSED", elapsedTimeBasic + "");

        while(!isDestroyed) {
            //check if the foreground app is on our list of apps to track
            String monitoredApp = isForeground(appsToMonitor);
            Log.d("???: MONITORED APP", monitoredApp + " is monitored");
            Log.d("???: ISMONITORED", isMonitored + "");
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
            if (touchLayout != null) {
                mWindowManager.removeView(touchLayout);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if ((event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) && isMonitored) {
            Log.d(TAG,  "Action:" + event.getAction() + "\t X:" + event.getRawX() + "\t Y:" + event.getRawY());
        }

        return true;
    }
}
