package com.yong.wesave.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.yong.wesave.PricingActivity;
import com.yong.wesave.R;
import com.yong.wesave.apiobject.WeSaveNotification;
import com.yong.wesave.common.Constants;

import java.util.ArrayList;

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

public class NotificationAdapter extends ArrayAdapter<WeSaveNotification> {

    private boolean includeLoadMore;
    private int imageDimension;
    int resource;
    String response;
    Context context;
    ArrayList<WeSaveNotification> notifications;
    AQuery androidAQuery;

    public NotificationAdapter(Context context, int resource, ArrayList<WeSaveNotification> notifications, boolean includeLoadMore) {
        super(context, resource, notifications);
        this.resource = resource;
        this.notifications = notifications;
        this.context = context;
        this.includeLoadMore = includeLoadMore;
        this.androidAQuery = new AQuery(context);
    }

    @Override
    public int getCount() {
        if (includeLoadMore) {
            return notifications.size() + 1;
        } else {
            return notifications.size();
        }
    }

    @Override
    public WeSaveNotification getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LinearLayout view;
        //Get the current object
        final WeSaveNotification notification = getNotification(position);

        //Inflate the view
        if (convertView == null) {
            view = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, view, true);
        } else {
            view = (LinearLayout) convertView;
        }

        //TextView itemName = (TextView) view.findViewById(R.id.name);
        TextView info = (TextView) view.findViewById(R.id.info);
        ImageView itemImg = (ImageView) view.findViewById(R.id.image);
        LinearLayout entry = (LinearLayout) view.findViewById(R.id.entry);
        TextView createdAt = (TextView) view.findViewById(R.id.tv_duration);

        //Set Item details
        androidAQuery.id(itemImg).image(Constants.API_BASE_URL_ITEM_IMAGES + notification.getItemImage(),
                true, true, itemImg.getWidth(), R.drawable.default_item_image);

        //String username = getUsername(notification.getUserID());

        if (notification.getType().equals("follow")) {
            info.setText(notification.getUsername() + " followed your item!");
        } else if (notification.getType().equals("like")) {
            info.setText(notification.getUsername() + " liked your item!");
        } else if (notification.getType().equals("promo")) {
            info.setText("Check out the new promotion deal on one of your following items!");
        }

        long x = Math.round(Double.parseDouble(notification.getCreatedat()));
        createdAt.setText(DateUtils.getRelativeTimeSpanString(x, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));

        entry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, PricingActivity.class);
                intent.putExtra("item_id", notification.getItemID());
                context.startActivity(intent);
                //}
            }
        });

        return view;
    }

    public void hideLoading() {
        includeLoadMore = false;
        notifyDataSetChanged();
    }

    public WeSaveNotification getNotification(int position) {
        return notifications.get(position);
    }

    /*public String getUsername (int user_id){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getuserinfo", params);
        JSONArray json_item = null;
        String name = "";

        if (json != null) {
            try {
                if (json.getString("status").equals("success") && json.getJSONObject("data") != null ) {
                    name = json.getJSONObject("data").getString("username");
                }
            }catch (JSONException e) {
                    e.printStackTrace();
            }
        }

        return name;
    }*/

}
