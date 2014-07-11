package com.perfectify.eulogio.clockio.appList;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.perfectify.eulogio.clockio.MainActivity;
import com.perfectify.eulogio.clockio.Models.AppInfo;
import com.perfectify.eulogio.clockio.R;

import java.util.List;

/**
 * Custom View that is a list w/icons
 */
public class appList extends ArrayAdapter<String> {
    private final MainActivity context;
    private final List<String> name;
    private final List<String> packageName;
    private final List<Drawable> image;
    private final List<Integer> check;
    public appList(MainActivity context, List<String> name, List<String> packageName, List<Drawable> image, List<Integer> check) {
        super(context, R.layout.app_list, name);
        this.context = context;
        this.name = name;
        this.packageName = packageName;
        this.image = image;
        this.check = check;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.app_list, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.check);

        // set checkbox listener
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppInfo test = new AppInfo(
                        packageName.get(position),
                        name.get(position),
                        ( checkBox.isChecked() ? 1 : 0)
                );
                context.db.updateAppInfo(test);

                Log.d("???:checkboxClick", test.toString());
            }
        });

        // show checkmark if checked in db
        if (this.check.get(position) > 0)
            checkBox.setChecked(true);

        txtTitle.setText(name.get(position));
        imageView.setImageDrawable(image.get(position));
        return rowView;
    }
}
