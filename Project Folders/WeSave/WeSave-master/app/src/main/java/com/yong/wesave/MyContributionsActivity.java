package com.yong.wesave;

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yong.wesave.adapter.ItemAdapter;
import com.yong.wesave.adapter.PricingAdapter;
import com.yong.wesave.apiobject.Item;
import com.yong.wesave.apiobject.Pricing;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyContributionsActivity extends WeSaveActivity implements View.OnClickListener {

    // List view
    private ListView lv;

    // Listview Adapter
    ItemAdapter adapter;
    PricingAdapter pAdapter;
    Button itemBtn, pricingBtn;
    TextView emptyText;
    JSONArray item_json;
    JSONObject json_data;
    Item item;
    Pricing pricing;
    Gson gson = new Gson();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_my_contributions, R.id.content_frame);
        super.replaceTitle(R.string.title_contribution);

        lv = (ListView) findViewById(R.id.list_view);
        emptyText = (TextView) findViewById(android.R.id.empty);
        itemBtn = (Button) findViewById(R.id.itemBtn);
        pricingBtn = (Button) findViewById(R.id.pricingBtn);

        listContributedItems();
        pricingBtn.setTextColor(Color.GRAY);

        itemBtn.setOnClickListener(this);
        pricingBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == itemBtn) {
            listContributedItems();
            pricingBtn.setTextColor(Color.GRAY);
            itemBtn.setTextColor(Color.parseColor("#00b0e4"));
        } else if (v == pricingBtn) {
            listContributedPricings();
            itemBtn.setTextColor(Color.GRAY);
            pricingBtn.setTextColor(Color.parseColor("#00b0e4"));
        }
    }

    public void listContributedItems() {
        item_json = getContributions("items");
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

        adapter = new ItemAdapter(this, R.layout.list_contributed_item, items, true, super.user_id, true);

        if (items.size() == 0)
            emptyText.setVisibility(View.VISIBLE);
        else {
            lv.setAdapter(adapter);
            emptyText.setVisibility(View.GONE);
        }
    }

    public void listContributedPricings() {
        item_json = getContributions("pricings");
        ArrayList<Pricing> items = new ArrayList<Pricing>();

        for (int i = 0; i < item_json.length(); i++) {
            try {
                json_data = item_json.getJSONObject(i);
                JsonParser parser = new JsonParser();
                JsonElement mJson = parser.parse(json_data.toString());
                pricing = gson.fromJson(mJson, Pricing.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            items.add(pricing);
        }

        pAdapter = new PricingAdapter(this, R.layout.list_contributed_pricing, items, super.user_id, 5);

        if (items.size() == 0)
            emptyText.setVisibility(View.VISIBLE);
        else {
            lv.setAdapter(pAdapter);
            emptyText.setVisibility(View.GONE);
        }
    }

    public JSONArray getContributions(String choice) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", super.user_id));
        ServerRequest sr = new ServerRequest();
        JSONObject json;

        if (choice.equals("items"))
            json = sr.getJSON(Constants.API_BASE_URL_API + "getcontributeditems", params);
        else
            json = sr.getJSON(Constants.API_BASE_URL_API + "getcontributedpricings", params);

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