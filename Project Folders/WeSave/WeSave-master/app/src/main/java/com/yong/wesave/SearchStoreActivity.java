package com.yong.wesave;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yong.wesave.adapter.StoreAdapter;
import com.yong.wesave.apiobject.Store;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchStoreActivity extends WeSaveActivity {

    // List view
    private ListView lv;

    // Listview Adapter
    StoreAdapter adapter;

    // Search EditText
    EditText inputSearch;

    JSONArray item_json;
    JSONObject json_data;
    Store item;
    Gson gson = new Gson();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_search, R.id.content_frame);
        super.replaceTitle(R.string.title_search_store);

        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        item_json = getAllSupermarkets();

        ArrayList<Store> items = new ArrayList<Store>();
        for (int i = 0; i < item_json.length(); i++) {
            try {
                json_data = item_json.getJSONObject(i);
                JsonParser parser = new JsonParser();
                JsonElement mJson = parser.parse(json_data.toString());
                item = gson.fromJson(mJson, Store.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            items.add(item);
        }

        adapter = new StoreAdapter(this, R.layout.list_search, items);
        lv.setAdapter(adapter);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                SearchStoreActivity.this.adapter.getFilter().filter(cs.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_done);
        item.setVisible(true);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_done:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public JSONArray getAllSupermarkets() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("type", "supermarket"));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getallstores", params);
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