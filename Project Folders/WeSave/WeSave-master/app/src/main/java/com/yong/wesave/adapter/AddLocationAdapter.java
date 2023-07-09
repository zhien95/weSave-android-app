package com.yong.wesave.adapter;

/**
 * Created by Yong0156 on 28/2/2017.
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */
/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yong.wesave.R;
import com.yong.wesave.apiobject.LocationModel;
import com.yong.wesave.asyntask.AsyncImageLoader;

import java.util.List;

public class AddLocationAdapter extends BaseAdapter {

    Activity activity;
    List<LocationModel> data;

    public AddLocationAdapter(Activity activity, List<LocationModel> data) {
        this.activity = activity;
        this.data = data;
    }

    public static class ViewHolder {
        TextView lblLocationDesc;
        TextView lblLocationName;
        ImageView imgIcon;
        RelativeLayout entry;
        int position;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;
        if (view == null) {
            LayoutInflater inflator = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.list_add_location_entry, null);

            viewHolder = new ViewHolder();
            viewHolder.entry = (RelativeLayout) view
                    .findViewById(R.id.entry);
            viewHolder.lblLocationName = (TextView) view
                    .findViewById(R.id.add_location_name);
            viewHolder.lblLocationDesc = (TextView) view
                    .findViewById(R.id.add_location_description);
            viewHolder.imgIcon = (ImageView) view
                    .findViewById(R.id.add_location_icon);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.position = position;

        final LocationModel location = data.get(position);

        if (location.isCustom())
            viewHolder.lblLocationName.setText("Use as Custom Location");
        else
            viewHolder.lblLocationName.setText(location.getName());

        if (location.isCustom())
            viewHolder.lblLocationDesc.setText(location.getName());
        else {
            if (location.getAddress() != null && !location.getAddress().equals("")) {
                viewHolder.lblLocationDesc.setText(location.getAddress() + " · " + location.getDistance() + "m");
            } else {
                viewHolder.lblLocationDesc.setText(location.getDistance() + "m");
            }
        }


        viewHolder.imgIcon.setImageResource(R.drawable.default_item_image);
        if ((location.getIconURL() != null)
                && !(location.getIconURL().equals(""))) {
            new AsyncImageLoader(activity, viewHolder.imgIcon, 0,
                    location.getIconURL()).execute("");
        }

        viewHolder.entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("storeId", location.getId());
                resultIntent.putExtra("storeName", location.getName());
                resultIntent.putExtra("category", location.getLocationCategory());
                resultIntent.putExtra("icon", location.getIconURL());
                resultIntent.putExtra("address", location.getAddress());
                resultIntent.putExtra("lat", Double.toString(location.getLat()));
                resultIntent.putExtra("long", Double.toString(location.getLng()));

                activity.setResult(Activity.RESULT_OK, resultIntent);
                activity.finish();
            }
        });

        view.invalidate();
        return view;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clearData() {
        // clear the data
        data.clear();
    }
}
