package com.perfectify.eulogio.clockio;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.perfectify.eulogio.clockio.appList.appList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {

    // App names and Icons
    List<String> appNames = new ArrayList<String>();
    List<Drawable> appIcons = new ArrayList<Drawable>();
    Map<String, String> apps = new HashMap<String, String>();

    Context context = this;
    public final static String APP_MESSAGE = "com.perfectify.eulogio.clockio.APP";

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

            // get app name and package name
            final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : packageInfo.packageName);
            final String packageName = packageInfo.packageName;
            apps.put(applicationName,  packageName);
            appNames.add(applicationName);

            //find app Icon if available, else get default logo
            Drawable appIcon;
            try {
                appIcon = pm.getApplicationIcon(packageName);
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

        // Set listview listener for launching an app
        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                TextView tableLayoutView = (TextView) view.findViewById(R.id.txt);
                String appToLaunch = apps.get(tableLayoutView.getText().toString());


                //Send to interim activity to manage bound service
                Intent resultIntent = new Intent(context, ResultActivity.class);
                resultIntent.putExtra(APP_MESSAGE, appToLaunch);
                startActivity(resultIntent);

                Toast.makeText(context, "ClockIO running in background", Toast.LENGTH_LONG).show();
            }
        });
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
