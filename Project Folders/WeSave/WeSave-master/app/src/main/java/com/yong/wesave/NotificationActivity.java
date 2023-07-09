package com.yong.wesave;

/**
 * Created by Yong on 8/1/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yong.wesave.adapter.NotificationAdapter;
import com.yong.wesave.apiobject.WeSaveNotification;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends WeSaveActivity {

    public static final int ACTION_REFRESH_WITH_NEW_LIST = 1;
    private LinearLayout loadingLayout;
    private RelativeLayout emptyLayout;
    private ArrayList<WeSaveNotification> notifications;
    private NotificationAdapter adapter;
    private ListView notificationsList;
    private SwipeRefreshLayout swipeContainer;
    private String authKey;
    private int currNotificationCount = 0;


    private BroadcastReceiver notificationsBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int actionCode = intent.getIntExtra("actionCode", 0);
            //System.out.println("action code frag " + actionCode);
            switch (actionCode) {
                case ACTION_REFRESH_WITH_NEW_LIST: {
                    if (notifications == null) {
                        notifications = new ArrayList<WeSaveNotification>();
                    } else {
                        notifications.clear();
                    }

                    System.out.println("action refresh fragment");
                    ArrayList<WeSaveNotification> newNotifications = (ArrayList<WeSaveNotification>) intent
                            .getSerializableExtra("complaints");
                    if (newNotifications == null || newNotifications.isEmpty()) {
                        show(emptyLayout);
                        hide(notificationsList, loadingLayout);
                        return;
                    }

                    show(notificationsList);
                    hide(emptyLayout, loadingLayout);

                    notifications.addAll(newNotifications);
                    adapter = new NotificationAdapter(NotificationActivity.this, R.layout.list_notification, notifications, true);
                    notificationsList.setAdapter(adapter);
                    adapter.hideLoading();
                    break;
                }

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_notifications, R.id.content_frame);
        super.replaceTitle(R.string.title_notification);

        //loadingLayout = (LinearLayout) findViewById(R.id.loadingLayout);
        //emptyLayout = (RelativeLayout) findViewById(R.id.emptyLayout);
        notificationsList = (ListView) findViewById(R.id.list_view);
        //swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        getNotifications();
        // Setup refresh listener which triggers new data loading

        /*swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                //fetchTimelineAsync(0);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        show(emptyLayout);
        hide(loadingLayout);*/

    }
/*
    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.

        client.getHomeTimeline(0, new JsonHttpResponseHandler() {
            public void onSuccess(JSONArray json) {
                // Remember to CLEAR OUT old items before appending in the new ones
                adapter.clear();
                // ...the data has come back, add new items to your adapter...
                adapter.addAll();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });
    }*/

    private void hide(View... vs) {
        for (View v : vs) {
            v.setVisibility(View.GONE);
        }
    }

    private void show(View... vs) {
        for (View v : vs) {
            v.setVisibility(View.VISIBLE);
        }
    }

    public int getCurrNotificationCount() {
        return currNotificationCount;
    }

    public void getNotifications() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("recipient_id", super.user_id));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getnotifications", params);

        JSONArray json_notifications = null;
        ArrayList<WeSaveNotification> notifications = new ArrayList<WeSaveNotification>();

        if (json != null) {
            try {
                if (json.getString("status").equals("success")) {
                    json_notifications = json.getJSONArray("data");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Gson gson = new Gson();
        JSONObject json_data;
        JsonParser parser;
        JsonElement mJson;
        WeSaveNotification notification = null;

        if (json_notifications != null) {
            for (int i = 0; i < json_notifications.length(); i++) {
                try {
                    json_data = json_notifications.getJSONObject(i);
                    parser = new JsonParser();
                    mJson = parser.parse(json_data.toString());
                    notification = gson.fromJson(mJson, WeSaveNotification.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                notifications.add(notification);
            }

            notificationsList.destroyDrawingCache();
            notificationsList.setVisibility(ListView.INVISIBLE);
            notificationsList.setVisibility(ListView.VISIBLE);
            adapter = new NotificationAdapter(this, R.layout.list_notifications, notifications, false);
            notificationsList.setAdapter(adapter);
        }
    }
}

