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
import com.yong.wesave.adapter.ItemAdapter;
import com.yong.wesave.apiobject.Item;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequestGet;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchItemActivity extends WeSaveActivity {

    // List view
    private ListView resultLV;

    // Listview Adapter
    ItemAdapter adapter;

    // Search EditText
    EditText inputSearch;

    JSONArray keywordItem_json;
 
    JSONObject json_data;
    Item item;
    Gson gson = new Gson();

    // ArrayList for Listview
    ArrayList<HashMap<String, String>> productList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_search, R.id.content_frame);
        super.replaceTitle(R.string.title_search_item);
    
        resultLV = (ListView) findViewById(R.id.list_resultView);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //SearchActivity.this.adapter.getFilter().filter(cs.toString());

                if (cs.toString().equals("")) {
                    adapter.clear();
                } else {
                    keywordItem_json = getKeywordItems(cs.toString());
                    
                    ArrayList<Item> items = new ArrayList<Item>();
                    for (int i = 0; i < keywordItem_json.length(); i++) {
                        try {
                            json_data = keywordItem_json.getJSONObject(i);
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = parser.parse(json_data.toString());
                            item = gson.fromJson(mJson, Item.class);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        items.add(item);
                    }

                    adapter = new ItemAdapter(SearchItemActivity.this, R.layout.list_search, items, false, SearchItemActivity.super.user_id, false);
                    resultLV.setAdapter(adapter);
                }
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

    public JSONArray getKeywordItems(String query) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        ServerRequestGet sr = new ServerRequestGet();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "searchitems/" + query, params);
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