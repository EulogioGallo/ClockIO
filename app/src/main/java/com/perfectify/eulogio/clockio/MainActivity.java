package com.perfectify.eulogio.clockio;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.perfectify.eulogio.clockio.Models.AppInfo;
import com.perfectify.eulogio.clockio.Models.AppTime;
import com.perfectify.eulogio.clockio.Models.SQLiteHelper;
import com.perfectify.eulogio.clockio.appList.appList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {

    // App names and Icons
    List<String> appNames = new ArrayList<String>();
    List<String> appPackageNames = new ArrayList<String>();
    List<Drawable> appIcons = new ArrayList<Drawable>();
    List<Integer> appMonitors = new ArrayList<Integer>();
    //Get listview item from activity_main.xml
    ListView lv;

    Context context = this;
    public final static String APP_MESSAGE = "com.perfectify.eulogio.clockio.APP";
    private ProgressDialog progressDialog;

    // create db if not already present
    public SQLiteHelper db = new SQLiteHelper(this);

    /*************************************************************************************************************/
    // Extend AsyncTask to do DB loading in background
    private class LoadProgress extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            // Create progress dialog
            progressDialog = ProgressDialog.show(MainActivity.this, "Loading...", "Please wait...", false, false);
        }

        // background code to be executed
        @Override
        protected Void doInBackground(Void... params) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            try {
                // get current thread's token
                synchronized (this) {
                    final PackageManager pm = getPackageManager();
                    //get a list of installed packages.
                    List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);


                    //cycle through packages for app names
                    for (ApplicationInfo packageInfo : packages) {

                        //Get application name from packagename
                        ApplicationInfo ai;
                        try {
                            ai = pm.getApplicationInfo(packageInfo.packageName, 0);
                        } catch (final PackageManager.NameNotFoundException e) {
                            ai = null;
                        }

                        // get app name and package name
                        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : packageInfo.packageName);
                        final String packageName = packageInfo.packageName;

                        // add info to db if not already present
                        if (db.getAppInfo(packageName) == null)
                            db.addAppInfo(new AppInfo(packageName, applicationName, 0));

                        // add time to db if not already present
                        if (db.getAppTime(packageName) == null)
                            db.addAppTime(new AppTime(packageName));


                        //find app Icon if available, else get default logo
                        Drawable appIcon;
                        try {
                            appIcon = pm.getApplicationIcon(packageName);
                        } catch (PackageManager.NameNotFoundException nnfe) {
                            appIcon = pm.getDefaultActivityIcon();
                        }
                        appIcons.add(appIcon);
                    }

                    // cycle through updated db for correct info
                    for( AppInfo appInfo : db.getAllAppInfo()) {
                        appNames.add(appInfo.getAppName());
                        appPackageNames.add(appInfo.getPackageName());
                        appMonitors.add(appInfo.getMonitored());

                        Log.d("???:CycledApps", appInfo.toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        // set view after executing background code
        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            setContentView(R.layout.activity_main);
            // This is the array adapter, it takes the context of the activity as a
            // first parameter, the type of list view as a second parameter and your
            // array as a third parameter.
            appList adapter = new appList((MainActivity) context, appNames, appPackageNames, appIcons, appMonitors);

            lv = (ListView) findViewById(R.id.appListView);
            Log.d("???:LISTVIEW", lv.toString());
            lv.setAdapter(adapter);
            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        }
    }
    /***************************************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LoadProgress().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startInBackground(View view) {
        // start service
        Intent  resultIntent = new Intent(context, ResultActivity.class);
        resultIntent.putExtra(APP_MESSAGE, "Tap here to exit");
        Toast.makeText(this, "ClockIO is running in background", Toast.LENGTH_SHORT).show();
        startActivity(resultIntent);
    }

}
