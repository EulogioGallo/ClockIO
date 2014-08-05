package com.perfectify.eulogio.clockio.Models;

/**
 * Created by Eulogio on 7/10/2014.
 */
public class AppTime {

    private int id;
    private String packageName;
    private long elapsedTime;

    public AppTime() {
        super();
        elapsedTime = 0;
    }

    public AppTime(String packageName) {
        super();
        this.packageName  = packageName;
        elapsedTime = 0;
    }

    public AppTime(int id, String packageName, long elapsedTime) {
        super();
        this.id = id;
        this.packageName = packageName;
        this.elapsedTime = elapsedTime;
    }

    // getters & setters
    public int getId() { return this.id; }

    public void setId(int id) { this.id = id; }

    public String getPackageName() { return this.packageName; }

    public long getElapsedTime() { return this.elapsedTime; }

    public void setElapsedTime(long elapsedTime) { this.elapsedTime = elapsedTime; }

    public void zero() { setElapsedTime(0); }

    @Override
    public String toString() {
        return "AppTime [id=" + id + ", packageName=" + packageName + ", elapsedTime=" + elapsedTime + "]";
    }
}
