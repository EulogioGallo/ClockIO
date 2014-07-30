package com.perfectify.eulogio.clockio;

import android.os.FileObserver;
import android.util.Log;

/**
 * Created by Eulogio on 7/30/2014.
 */
public class ScreenshotObserver extends FileObserver {
    private String absolutePath;

    public ScreenshotObserver(String path) {
        super(path, ALL_EVENTS);
        absolutePath = path;
    }

    @Override
    public void onEvent(int event, String path) {
        if (path == null) {
            return;
        }

        // a file was created under the monitored directory
        if ((CREATE & event) != 0) {
            Log.d("???: SCREENSHOT!!!", path);
        }
    }
}
