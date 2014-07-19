package com.perfectify.eulogio.clockio.appTimeList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.perfectify.eulogio.clockio.FinalsActivity;
import com.perfectify.eulogio.clockio.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Eulogio on 7/18/2014.
 */
public class appTimeList extends ArrayAdapter<String> {
    private final FinalsActivity context;
    private final List<String> packageName;
    private final List<Long> time;
    public appTimeList(FinalsActivity context, List<String> packageName, List<Long> time) {
        super(context, R.layout.app_time_list, packageName);
        this.context = context;
        this.packageName = packageName;
        this.time = time;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.app_time_list, null, true);
        TextView txtPackageName = (TextView) rowView.findViewById(R.id.packageName);
        TextView txtTime = (TextView) rowView.findViewById(R.id.time);

        txtPackageName.setText(packageName.get(position));

        // format time
        String timeString = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(time.get(position)),
                TimeUnit.MILLISECONDS.toSeconds(time.get(position)) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time.get(position)))
        );

        txtTime.setText(timeString);
        return rowView;
    }
}
