package com.perfectify.eulogio.clockio;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.perfectify.eulogio.clockio.appList.appList;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    // App names and Icons
    List<String> appNames = new ArrayList<String>();
    List<Drawable> appIcons = new ArrayList<Drawable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final PackageManager pm = getPackageManager();
        //get a list of installed packages.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        //Get listview item from activity_main.xml
        ListView lv = (ListView) findViewById(R.id.appListView);


        //cycle through packages for app names
        for (ApplicationInfo packageInfo : packages) {

            //Get application name from packagename
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo(packageInfo.packageName, 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }

            // get app name if available, else get package name
            final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : packageInfo.packageName);
            appNames.add(applicationName);

            //find app Icon if available, else get default logo
            Drawable appIcon;
            try {
                appIcon = pm.getApplicationIcon(packageInfo.packageName);
            } catch (PackageManager.NameNotFoundException nnfe) {
                appIcon = pm.getDefaultActivityIcon();
            }
            appIcons.add(appIcon);
        }

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        appList adapter = new appList(this, appNames, appIcons);

        lv.setAdapter(adapter);

// the getLaunchIntentForPackage returns an intent that you can use with startActivity()
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
}
