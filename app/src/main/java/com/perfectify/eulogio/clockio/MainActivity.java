package com.perfectify.eulogio.clockio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.perfectify.eulogio.clockio.Models.AppInfo;
import com.perfectify.eulogio.clockio.Models.AppTime;
import com.perfectify.eulogio.clockio.Models.SQLiteHelper;
import com.perfectify.eulogio.clockio.appList.appList;
import com.perfectify.eulogio.clockio.signinPreferences.SigninPreferenceActivity;
import com.perfectify.eulogio.clockio.signinPreferences.SigninPreferenceFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MainActivity extends Activity {

    // App names and Icons
    List<String> appNames = new ArrayList<String>();
    List<String> appPackageNames = new ArrayList<String>();
    List<Drawable> appIcons = new ArrayList<Drawable>();
    List<Integer> appCheck = new ArrayList<Integer>();

    // Map to sort these apps before listing them
    Map<String, Integer> appMonitors = new TreeMap<String, Integer>();
    Map<String, Pair<String, Drawable>> appRows = new TreeMap<String, Pair<String, Drawable>>();

    //Get listview item from activity_main.xml
    ListView lv;

    Context context = this;
    public final static String FIRST_CALL = "com.perfectify.eulogio.clockio.APP";
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
                    int flags = PackageManager.GET_META_DATA |
                                PackageManager.GET_SHARED_LIBRARY_FILES |
                                PackageManager.GET_UNINSTALLED_PACKAGES;
                    List<ApplicationInfo> packages = pm.getInstalledApplications(flags);


                    //cycle through packages for app names
                    for (ApplicationInfo packageInfo : packages) {

                        // skip if system package
                        if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                            continue;
                        }

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

                        AppInfo appInfo = db.getAppInfo(packageName);
                        AppTime appTime = db.getAppTime(packageName);

                        // add info to db if not already present
                        if (appInfo == null) {
                            appInfo = new AppInfo(packageName, applicationName, 0);
                            db.addAppInfo(appInfo);
                        }

                        // add time to db if not already present
                        if (appTime == null) {
                            appTime = new AppTime(packageName);
                            db.addAppTime(appTime);
                        } else if (appTime.getElapsedTime() > 0) {
                            appTime.zero();
                        }


                        //find app Icon if available, else get default logo
                        Drawable appIcon;
                        try {
                            appIcon = pm.getApplicationIcon(packageName);
                        } catch (PackageManager.NameNotFoundException nnfe) {
                            appIcon = pm.getDefaultActivityIcon();
                        }

                        // add to sorted map
                        appRows.put(applicationName, Pair.create(packageName, appIcon));
                        appMonitors.put(applicationName, appInfo.getMonitored());
                    }

                    // take map and make lists to add to list adapter
                    for (Map.Entry<String, Pair<String, Drawable>> entry : appRows.entrySet()) {
                        appNames.add(entry.getKey());
                        appPackageNames.add(entry.getValue().first);
                        appIcons.add(entry.getValue().second);
                        appCheck.add(appMonitors.get(entry.getKey()));
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
            appList adapter = new appList((MainActivity) context, appNames, appPackageNames, appIcons, appCheck);

            lv = (ListView) findViewById(R.id.appListView);
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
            Intent signinIntent = new Intent(getApplicationContext(), SigninPreferenceActivity.class);
            startActivityForResult(signinIntent, 0);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startInBackground(View view) {
        // only start service if signed in
        String signInPref = PreferenceManager.getDefaultSharedPreferences(this).getString("signin","");
        Log.d("???:signInPref", signInPref);

        if (!signInPref.isEmpty()) {

            // start service
            Intent resultIntent = new Intent(context, ResultActivity.class);
            resultIntent.putExtra(FIRST_CALL, "Tap this to exit");
            Toast.makeText(this, "ClockIO is running in background", Toast.LENGTH_SHORT).show();
            startActivity(resultIntent);

            finish();

        } else {

            Toast.makeText(this, "Login First!", Toast.LENGTH_SHORT).show();

        }
    }

}
