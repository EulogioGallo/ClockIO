package com.perfectify.eulogio.clockio;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eulogio on 7/30/2014.
 */
public class ScreenshotObserver extends FileObserver {
    private String absolutePath;
    private Context context;
    private List<String> appsToMonitor;

    public ScreenshotObserver(Context context, String path) {
        super(path, ALL_EVENTS);
        this.context = context;
        this.absolutePath = path;
    }

    public void setAppsToMonitor(List<String> appsToMonitor) {
        this.appsToMonitor = appsToMonitor;
    }

    // get current running packagename
    public String getForeground() {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > runningTaskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;

        return componentInfo.getPackageName();
    }

    // get latest modified file in screenshot directory
    public String lastModified(String path) {
        File dir = new File(path);

        File[] files = dir.listFiles();
        if (files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }

        return lastModifiedFile.getName();
    }

    @Override
    public void onEvent(int event, String path) {
        if (path == null) {
            return;
        }

        // a file was created under the monitored directory
        if ((CREATE & event) != 0) {
            Log.d("???: SCREENSHOT!!!", getForeground());
            Log.d("???: SCREENSHOT!!!", lastModified(absolutePath));
            Toast.makeText(context, "Screenshot taken within ClockIO!", Toast.LENGTH_SHORT).show();
        }
    }
}
