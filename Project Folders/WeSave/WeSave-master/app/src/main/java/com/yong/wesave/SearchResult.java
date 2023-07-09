package com.yong.wesave;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.List;

public class SearchResult extends WeSaveActivity {
  // List view
  private ListView resultLV;
  // Listview Adapter
  ItemAdapter adapter;
  // Search EditText
  EditText resultsTitle;
  JSONArray keywordItem_json;
  JSONObject json_data;
  Item item;
  Gson gson = new Gson();
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.replaceContentLayout(R.layout.search_result, R.id.content_frame);
    super.replaceTitle(R.string.title_search_result);
    Intent intent = getIntent();
    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
      String query = intent.getStringExtra(SearchManager.QUERY);
      resultsTitle = (EditText) findViewById(R.id.results_title);
      resultsTitle.setText("The search results for: \"" + query + "\"");
      //Search query in db
      keywordItem_json = getKeywordItems(query);
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
      //set items results list view
      adapter = new ItemAdapter(SearchResult.this, R.layout.list_search, items, true,
              SearchResult.this.user_id, false);
      resultLV = findViewById(R.id.list_resultView);
      resultLV.setAdapter(adapter);
      resultLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          Intent intent = new Intent(getApplicationContext(), PricingActivity.class);
          startActivity(intent);
        }
      });
    }
  }
  
  public JSONArray getKeywordItems(String query) {
    query = query.replace(" ","%20");
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
