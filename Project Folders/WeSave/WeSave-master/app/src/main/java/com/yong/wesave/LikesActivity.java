package com.yong.wesave;

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yong.wesave.adapter.UserAdapter;
import com.yong.wesave.apiobject.User;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LikesActivity extends WeSaveActivity {

    // List view
    private ListView lv;

    // Listview Adapter
    UserAdapter adapter;

    TextView emptyText;
    JSONArray user_json;
    JSONObject json_data;
    User user;
    Gson gson = new Gson();
    String item_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_likes, R.id.content_frame);
        super.replaceTitle(R.string.title_likes);

        lv = (ListView) findViewById(R.id.list_view);
        emptyText = (TextView) findViewById(android.R.id.empty);
        item_id = String.valueOf(getIntent().getIntExtra("item_id", 0));
        user_json = getLikers();

        ArrayList<User> users = new ArrayList<User>();
        if (user_json != null) {
            for (int i = 0; i < user_json.length(); i++) {
                try {
                    json_data = user_json.getJSONObject(i);
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(json_data.toString());
                    user = gson.fromJson(mJson, User.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                users.add(user);
            }
            //System.out.println("test2 " + users.get(0).getUsername());
            adapter = new UserAdapter(this, R.layout.list_liker, users);
            lv.setAdapter(adapter);
        }
    }


    public JSONArray getLikers() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("item_id", item_id));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getlikers", params);
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