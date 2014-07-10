package com.perfectify.eulogio.clockio.Models;

/**
 * Created by Eulogio on 7/10/2014.
 */
public class AppInfo {

    private int id;
    private String packageName;
    private String appName;
    private boolean isMonitored;

    public AppInfo() {}

    public AppInfo(String packageName, String appName, int isMonitored) {
        super();
        this.packageName = packageName;
        this.appName = appName;
        this.isMonitored = false;
    }

    // getters & setters
    @Override
    public String toString() {
        return "AppInfo [id=" + id + ", packageName=" + packageName + ", appName=" + appName
                + ", isMonitored=" + (isMonitored ? " true]" : " false]");
    }

    public int getId() {
        return this.id;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getAppName() {
        return this.appName;
    }

    public boolean getMonitored() {
        return this.isMonitored;
    }

    // only setter we need
    public void setId(int id) {
        this.id = id;
    }
}
