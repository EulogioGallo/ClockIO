package com.perfectify.eulogio.clockio.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Eulogio on 7/10/2014.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    // Database info
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ClockIODB1";

    // Table info
    // table names
    private static final String TABLE_APPINFO = "app_info";
    private static final String TABLE_APPTIME = "app_time";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PACKAGENAME = "packageName";
    private static final String KEY_APPNAME = "appName";
    private static final String KEY_MONITORED = "isMonitored";
    //
    private static final String KEY_TIME = "elapsedTime";

    private static final String[] APPINFO_COLUMNS = {KEY_ID,KEY_PACKAGENAME,KEY_APPNAME,KEY_MONITORED};
    private static final String[] APPTIME_COLUMNS = {KEY_ID,KEY_PACKAGENAME,KEY_TIME};

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create AppName and AppTime tables
        String CREATE_APPNAME_TABLE = "CREATE TABLE app_info (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "packageName TEXT UNIQUE," +
                "appName TEXT, " +
                "isMonitored INTEGER )";
        String CREATE_APPTIME_TABLE = "CREATE TABLE app_time (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "packageName TEXT UNIQUE," +
                "elapsedTime INTEGER, " +
                "FOREIGN KEY (id) REFERENCES app_info(id))";

        try {
            db.execSQL(CREATE_APPNAME_TABLE);
            db.execSQL(CREATE_APPTIME_TABLE);
        } catch(SQLiteConstraintException sqle) {
            Log.d("???:SQLite", "Already exists: " + sqle.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS app_info");
        db.execSQL("DROP TABLE IF EXISTS app_time");

        // create fresh tables
        this.onCreate(db);
    }

    /************************ CRUD Operations **************************************/

    /*********************** AppInfo ***********************************************/
    // add an app
    public void addAppInfo(AppInfo appInfo) {
        Log.d("???:addAppInfo", appInfo.toString());

        // get reference for writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // create contentvalues for rows
        ContentValues values = new ContentValues();
        values.put(KEY_PACKAGENAME,  appInfo.getPackageName());
        values.put(KEY_APPNAME, appInfo.getAppName());
        values.put(KEY_MONITORED, appInfo.getMonitored());

        // insert
        try {
            db.insert(TABLE_APPINFO, null, values);
        } catch(SQLiteConstraintException sqle) {
            Log.d("???:ConstraintException", sqle.toString());
        }

        // close
        db.close();
    }

    // get an app's info based on id
    public AppInfo getAppInfo(int id) {
        // get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // build query
        Cursor cursor = db.query(
                TABLE_APPINFO, // a. table
                APPINFO_COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null // h. limit
        );

        // if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        //  build AppInfo object
        AppInfo appInfo = new AppInfo(cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)));
        appInfo.setId(Integer.parseInt(cursor.getString(0)));

        Log.d("???:getAppInfo("+id+")", appInfo.toString());

        return appInfo;
    }

    // get an app's info based on package name
    public AppInfo getAppInfo(String packageName) {
        // get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // build query
        Cursor cursor = db.query(
                TABLE_APPINFO, // a. table
                APPINFO_COLUMNS, // b. column names
                " packageName = ?", // c. selections
                new String[] { packageName }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null // h. limit
        );

        // if we got results get the first one
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            //  build AppInfo object
            AppInfo appInfo = new AppInfo(cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)));
            appInfo.setId(Integer.parseInt(cursor.getString(0)));

            Log.d("???:getAppInfo(" + packageName + ")", appInfo.toString());

            return appInfo;
        }

        return null;
    }

    // get  app info for all available apps
    public List<AppInfo> getAllAppInfo() {
        List<AppInfo> appInfos = new LinkedList<AppInfo>();

        // build query
        String query = "SELECT * FROM " + TABLE_APPINFO;

        // get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // traverse results and add to AppInfo list
        AppInfo appInfo = null;
        if (cursor.moveToFirst()) {
            do {
                appInfo = new AppInfo(cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)));
                appInfo.setId(Integer.parseInt(cursor.getString(0)));

                appInfos.add(appInfo);
            } while (cursor.moveToNext());
        }

        Log.d("getAllAppInfo()", appInfos.toString());

        return appInfos;
    }

    // update an app's info
    public int updateAppInfo(AppInfo appInfo) {
        // get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // create contentvalues to add
        ContentValues values = new ContentValues();
        values.put("packageName", appInfo.getPackageName());
        values.put("appName", appInfo.getAppName());
        values.put("isMonitored", appInfo.getMonitored());

        // update row
        int i = db.update(
                TABLE_APPINFO, // table
                values, // column/values
                KEY_PACKAGENAME + " = ?", // selections
                new String[] { appInfo.getPackageName() } // selection args
        );

        db.close();

        return i;
    }

    // delete an app's info
    public void deleteAppInfo(AppInfo appInfo) {
        // get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // delete
        db.delete(
                TABLE_APPINFO, // table name
                KEY_ID + " = ?", // selections
                new String[] { String.valueOf(appInfo.getId())} // selection args
        );

        db.close();
        Log.d("deleteAppInfo", appInfo.toString());
    }

    // return all app packagenames that are set to monitor
    public List<String> getMonitoredApps() {
        List<String> monitoredApps = new LinkedList<String>();

        // build query
        String query = "SELECT packageName FROM " + TABLE_APPINFO + " WHERE isMonitored = 1";

        // get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // traverse results and add to packagenames list
        AppInfo appInfo = null;
        if (cursor.moveToFirst()) {
            do {
                monitoredApps.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        Log.d("getMonitoredApps()", monitoredApps.toString());

        return monitoredApps;
    }

    /*************************** END OF AppInfo *************************************************/

    // add an app's time
    public void addAppTime(AppTime appTime){
        //for logging
        Log.d("addAppTime", appTime.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_PACKAGENAME, appTime.getPackageName()); // get packagename
        values.put(KEY_TIME, appTime.getElapsedTime()); // get time

        // 3. insert
        db.insert(TABLE_APPTIME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }


    // get an app's time based on id
    public AppTime getAppTime(int id) {
        // get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // build query
        Cursor cursor = db.query(
                TABLE_APPTIME, // a. table
                APPTIME_COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null // h. limit
        );

        // if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        //  build AppTime object
        AppTime appTime = new AppTime(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getLong(2));
        Log.d("???:getAppTime("+id+")", appTime.toString() + "");

        return appTime;
    }

    // get an app's time based on packagename
    public AppTime getAppTime(String packageName) {
        // get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // build query
        Cursor cursor = db.query(
                TABLE_APPTIME, // a. table
                APPTIME_COLUMNS, // b. column names
                " packageName = ?", // c. selections
                new String[] { packageName }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null // h. limit
        );

        // if we got results get the first one
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            //  build AppTime object
            AppTime appTime = new AppTime(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getLong(2));
            Log.d("???:getAppTime(" + packageName + ")", appTime.toString() + "");

            return appTime;
        }

        return null;
    }

    // update apptime
    public int updateAppTime(AppTime appTime) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, appTime.getElapsedTime()); // get time

        // 3. updating row
        int i = db.update(TABLE_APPTIME, //table
                values, // column/value
                KEY_PACKAGENAME+" = ?", // selections
                new String[] { String.valueOf(appTime.getPackageName()) }); //selection args

        // 4. close
        db.close();

        return i;

    }
    /*************************** AppTime ******************************************************/


    /************************** END OF AppTime ***************************************************/
}
