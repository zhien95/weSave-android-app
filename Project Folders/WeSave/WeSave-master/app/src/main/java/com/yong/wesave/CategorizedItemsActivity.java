package com.yong.wesave;

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.os.Bundle;
import android.widget.GridView;
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

public class CategorizedItemsActivity extends WeSaveActivity {

    // List view
    private ListView lv;

    // Listview Adapter
    ItemAdapter adapter;

    int third_cat_id;
    TextView emptyText;
    JSONArray item_json;
    JSONObject json_data;
    Item item;
    Gson gson = new Gson();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_categorized_items, R.id.content_frame);
        //super.setTitle(); // to change
        getSupportActionBar().setTitle(getIntent().getStringExtra("third_cat_name"));
        TextView subCategory = (TextView) findViewById(R.id.categoryLabel);
        subCategory.setText(getIntent().getStringExtra("third_cat_name"));

        lv = (ListView) findViewById(R.id.list_view);
        third_cat_id = getIntent().getIntExtra("third_cat_id", 0);
        item_json = getCategorizedItems();

        ArrayList<Item> items = new ArrayList<Item>();
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

        adapter = new ItemAdapter(this, R.layout.list_categorized_item, items, true, super.user_id, false);
        lv.setAdapter(adapter);

    }

    public JSONArray getCategorizedItems() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("cat_id", String.valueOf(third_cat_id)));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getitemsbycategory", params);
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