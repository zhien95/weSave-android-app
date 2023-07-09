package com.yong.wesave;

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yong.wesave.adapter.ItemAdapter;
import com.yong.wesave.apiobject.Item;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FollowingActivity extends WeSaveActivity {

    // List view
    private ListView lv;

    // Listview Adapter
    ItemAdapter adapter;

    TextView emptyText;
    JSONArray item_json;
    JSONObject json_data;
    Item item;
    Gson gson = new Gson();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_following, R.id.content_frame);
        super.replaceTitle(R.string.title_following_items);

        lv = (ListView) findViewById(R.id.list_view);
        emptyText = (TextView) findViewById(android.R.id.empty);
        item_json = getFollowingItems();

        ArrayList<Item> items = new ArrayList<Item>();
        if (item_json != null) {
            for (int i = 0; i < item_json.length(); i++) {
                try {
                    json_data = item_json.getJSONObject(i);
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(json_data.toString());
                    item = gson.fromJson(mJson, Item.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                items.add(item);
            }

            adapter = new ItemAdapter(this, R.layout.list_following_item, items, true, super.user_id, false);
        }

        if (items.size() == 0)
            emptyText.setVisibility(View.VISIBLE);
        else {
            lv.setAdapter(adapter);
            emptyText.setVisibility(View.GONE);
        }
    }


    public JSONArray getFollowingItems() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", super.user_id));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getfollowingitems", params);
        JSONArray json_item = null;

        if (json != null) {
            try {
                if (json.getString("status").equals("success")) {
                    json_item = json.getJSONArray("data");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json_item;
    }

}