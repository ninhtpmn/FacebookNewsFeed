package com.example.ninh.facenewsprt3;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ninh on 22/06/2015.
 */
public class MyArrayAdapter extends ArrayAdapter<Item> {
    Context context = null;
    ArrayList<Item> myArray;
    int layoutId;

    public MyArrayAdapter(Context context, int layoutId, ArrayList<Item> Array) {
        super(context, layoutId, Array);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = Array;
    }

    public View getView(int position, View convertView, ViewGroup parent) {


        View row = convertView;
        RecordHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutId, parent, false);

            holder = new RecordHolder();
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.time = (TextView) row.findViewById(R.id.time);
            holder.icon = (ImageView) row.findViewById(R.id.icon);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        holder.title.setText(myArray.get(position).getTitle());
        holder.time.setText(myArray.get(position).getTime());
        holder.icon.setImageBitmap(myArray.get(position).getImage());


        return row;
    }


    static class RecordHolder {
        TextView title;
        TextView time;
        ImageView icon;

    }

}