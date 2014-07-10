package com.perfectify.eulogio.clockio.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private static final String DATABASE_NAME = "ClockIODB";

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
    private static final String[] APPTIME_COLUMNS = {KEY_ID,KEY_TIME};

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create AppName and AppTime tables
        String CREATE_APPNAME_TABLE = "CREATE TABLE app_info (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "packageName TEXT," +
                "appName TEXT, " +
                "isMonitored INTEGER )";
        String CREATE_APPTIME_TABLE = "CREATE TABLE app_time (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "elapsedTime INTEGER, " +
                "FOREIGN KEY (id) REFERENCES app_info(id))";

        db.execSQL(CREATE_APPNAME_TABLE);
        db.execSQL(CREATE_APPTIME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS app_info");
        db.execSQL("DROP TABLE IF EXISTS app_time");

        // create fresh tables
        this.onCreate(db);
    }

    // CRUD Operations

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
        db.insert(TABLE_APPINFO, null, values);

        // close
        db.close();
    }

    // get an app's info
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
                KEY_ID + " = ?", // selections
                new String[] { String.valueOf(appInfo.getId()) } // selection args
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
}
