package com.yong.wesave;

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.os.Bundle;
import androidx.appcompat.view.menu.ListMenuItemView;

import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yong.wesave.adapter.CategoryAdapter;
import com.yong.wesave.apiobject.Category;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubcategoryActivity extends WeSaveActivity {

    // List view
    private ListView lv;

    // Listview Adapter
    CategoryAdapter adapter;

    int parent_id;
    JSONArray cat_json;
    JSONObject json_data;
    Category category;
    Gson gson = new Gson();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_subcategory, R.id.content_frame);
        //super.replaceTitle(R.string.title_subcategories);
        getSupportActionBar().setTitle(getIntent().getStringExtra("cat_name"));
        TextView subcategoryLabel = (TextView) findViewById(R.id.subcategoryLabel);
        subcategoryLabel.setText(getIntent().getStringExtra("cat_name"));

        parent_id = getIntent().getIntExtra("cat_id", 0);
        lv = (ListView) findViewById(R.id.list_view);
        cat_json = getCategories();

        ArrayList<Category> categories = new ArrayList<Category>();
        for (int i = 0; i < cat_json.length(); i++) {
            try {
                json_data = cat_json.getJSONObject(i);
                JsonParser parser = new JsonParser();
                JsonElement mJson = parser.parse(json_data.toString());
                category = gson.fromJson(mJson, Category.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            categories.add(category);
        }

        adapter = new CategoryAdapter(this, R.layout.list_subcategory, categories, "ViewCategory");
        lv.setAdapter(adapter);
    }


    public JSONArray getCategories() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("cat_id", String.valueOf(parent_id)));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getsubcategories", params);
        JSONArray json_obj = null;

        if (json != null) {
            try {
                if (json.getString("status").equals("success")) {
                    json_obj = json.getJSONArray("data");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return json_obj;
    }

}