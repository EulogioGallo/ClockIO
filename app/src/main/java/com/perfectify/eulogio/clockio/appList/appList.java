package com.perfectify.eulogio.clockio.appList;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.perfectify.eulogio.clockio.R;

import java.util.List;

/**
 * Custom View that is a list w/icons
 */
public class appList extends ArrayAdapter<String> {
    private final Activity context;
    private final List<String> name;
    private final List<Drawable> image;
    public appList(Activity context, List<String> name, List<Drawable> image) {
        super(context, R.layout.app_list, name);
        this.context = context;
        this.name = name;
        this.image = image;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.app_list, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(name.get(position));
        imageView.setImageDrawable(image.get(position));
        return rowView;
    }
}
