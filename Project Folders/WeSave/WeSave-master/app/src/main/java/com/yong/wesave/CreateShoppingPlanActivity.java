package com.yong.wesave;

/**
 * Created by Yong on 8/1/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yong.wesave.adapter.ExpandableListAdapter;
import com.yong.wesave.apiobject.Item;
import com.yong.wesave.apiobject.Store;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.yong.wesave.common.Constants.API_ADD_STORE;
import static com.yong.wesave.common.Constants.API_BASE_URL_API;

public class CreateShoppingPlanActivity extends WeSaveActivity implements View.OnClickListener {

    LinearLayout addItem;
    Button addPlanBtn;
    Gson gson = new Gson();
    final int ADD_ITEM = 1;
    final int ADD_STORE = 2;
    List<Item> items = new ArrayList<Item>();
    List<Store> stores = new ArrayList<Store>();
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Store>> listStoreChild;
    HashMap<String, List<Item>> listItemChild;
    Double total = 0.00;
    private String storeId, storeName, category, icon, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_create_shopping_plan, R.id.content_frame);
        super.replaceTitle(R.string.title_create_shopping_plan);

        expListView = (ExpandableListView) findViewById(R.id.itemList);
        addPlanBtn = (Button) findViewById(R.id.addPlanButton);

        prepareListData();

        addPlanBtn.setOnClickListener(this);

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listItemChild, listStoreChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub

                return false;
            }
        });

        expListView.expandGroup(0);
        expListView.expandGroup(1);
    }

    @Override
    public void onClick(View v) {
        if (v == addPlanBtn) {
            if (items.size() > 1 && (stores.size() > 1))
                calculatePlan(items, stores);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ITEM) {
            if (resultCode == Activity.RESULT_OK) {
                getItem(items, data.getStringExtra("item_id"));
                listAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == ADD_STORE) {
            if (resultCode == Activity.RESULT_OK) {
                storeId = data.getStringExtra("storeId");
                storeName = data.getStringExtra("storeName");
                category = data.getStringExtra("category");
                icon = data.getStringExtra("icon");
                address = data.getStringExtra("address");
                addstore();
                getStore(stores);
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    private void addstore() {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("store_name", storeName));
        params.add(new BasicNameValuePair("store_id", storeId));
        params.add(new BasicNameValuePair("category", category));
        params.add(new BasicNameValuePair("icon", icon));
        params.add(new BasicNameValuePair("address", address));

        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(API_BASE_URL_API + API_ADD_STORE, params);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listStoreChild = new HashMap<String, List<Store>>();
        listItemChild = new HashMap<String, List<Item>>();

        // Adding child data
        listDataHeader.add("Items");
        listDataHeader.add("Supermarkets");

        // Adding child data
        Item i = new Item();
        items.add(i);
        Store s = new Store();
        stores.add(s);
        listItemChild.put(listDataHeader.get(0), items);
        listStoreChild.put(listDataHeader.get(1), stores);
    }

    public void getItem(List<Item> list, String item_id) {
        if (containsItem(list, Integer.parseInt(item_id))) {
            Context context = getApplicationContext();
            CharSequence text = "Item already added.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("item_id", item_id));
            ServerRequest sr = new ServerRequest();
            JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + Constants.API_GET_ITEM, params);

            if (json != null) {
                try {
                    if (json.getString("status").equals("success")) {
                        JsonParser parser = new JsonParser();
                        JSONObject json_data = json.getJSONObject("data");
                        JsonElement mJson = parser.parse(json_data.toString());
                        Item item = gson.fromJson(mJson, Item.class);
                        item.setQty(1);
                        list.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getStore(List<Store> list) {
        if (containsStore(list, storeId)) {
            Context context = getApplicationContext();
            CharSequence text = "Store already added.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            Store s = new Store();
            s.setId(storeId);
            s.setStoreName(storeName);
            s.setImage(icon);
            s.setType(category);
            list.add(s);
        }
    }

    public void calculatePlan(List<Item> items, List<Store> stores) {
        ArrayList<String> store_names = new ArrayList<String>();
        total = 0.00;
        ArrayList<Item> result = new ArrayList<Item>();
        String store_ids = "";
        for (int i = 1; i < stores.size(); i++) {
            store_ids += ",'" + stores.get(i).id + "'";
        }
        for (Item i : items) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("store_ids", store_ids.substring(1)));
            params.add(new BasicNameValuePair("item_id", Integer.toString(i.id)));
            ServerRequest sr = new ServerRequest();
            JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getminprice", params);
            try {
                if (json.getString("status").equals("success") && json.getJSONArray("data").length() > 0) {
                    JSONObject json_data = json.getJSONArray("data").getJSONObject(0);
                    i.setPrice(json_data.getString("price"));
                    i.setStoreName(json_data.getString("store_name"));
                    result.add(i);
                    total += (i.getQty() * Double.parseDouble(i.price));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (result.size() > 0)
            showResult(result);
        else {
            Context context = getApplicationContext();
            CharSequence text = "Cannot calculate this plan due to limited information in database.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void showResult(ArrayList<Item> res) {
        ArrayList<Item> nameValuePairs = res;
        ArrayList<String> store_names = new ArrayList<String>();
        for (Item i : res) {
            if (!store_names.contains(i.store_name)) {
                store_names.add(i.store_name);
            }
        }

        Intent i = new Intent(CreateShoppingPlanActivity.this, ShoppingPlanResultActivity.class);
        i.putExtra("res", nameValuePairs);
        i.putExtra("stores", store_names);
        i.putExtra("total", String.format("%.2f", total));
        i.putExtra("save", false);
        startActivity(i);

    }

    public Boolean containsItem(List<Item> list, int item_id) {
        for (Item o : list) {
            if (o.getId() == item_id) {
                return true;
            }
        }
        return false;
    }

    public Boolean containsStore(List<Store> list, String store_id) {
        for (Store o : list) {
            if (o.getId() != null) {
                if (o.getId().equals(store_id)) {
                    return true;
                }
            }
        }
        return false;
    }

}