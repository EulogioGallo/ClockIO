package com.perfectify.eulogio.clockio.Models;

/**
 * Created by Eulogio on 7/10/2014.
 */
public class AppTime {

    private int id;
    //private String packageName;
    private long elapsedTime;

    public AppTime() {
        super();
        elapsedTime = 0;
    }

    /*
    public AppTime(String packageName) {
        super();
        this.packageName = packageName;
        elapsedTime = 0;
    }
    */

    // getters & setters
    @Override
    public String toString() {
        return "AppTime [id=" + id + /*", packageName=" + packageName + */", elapsedTime=" + elapsedTime + "]";
    }
}
