package com.yong.wesave;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yong.wesave.adapter.ItemAdapter;
import com.yong.wesave.apiobject.Item;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;
import com.yong.wesave.util.ServerRequestGet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchActivity extends WeSaveActivity {
  private int locationPermissionCode = 1;
  // List view
  private ListView resultLV;
  
  // Listview Adapter
  ItemAdapter adapter;
  
  // Search EditText
  EditText inputSearch;
  
  JSONArray keywordItem_json, popularityItem_json, nearestItem_json;
  JSONObject json_data;
  Item item;
  Gson gson = new Gson();
  
  double lat, lng;
  Spinner searchOptionSpinner, filterSpinner;
  
  // ArrayList for Listview
  ArrayList<HashMap<String, String>> productList;
  String query = "";
  
  private ArrayList<Item> data;
  
  private int noOfBtns;
  private Button[] btns;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.replaceContentLayout(R.layout.activity_search, R.id.content_frame);
    super.replaceTitle(R.string.title_search_item);
    
    GPSTracker gpsTracker = new GPSTracker(this);
    
    if (gpsTracker.getIsGPSTrackingEnabled()) {
      lat = gpsTracker.latitude;
      lng = gpsTracker.longitude;
    }
    
    resultLV = (ListView) findViewById(R.id.list_resultView);
    inputSearch = (EditText) findViewById(R.id.inputSearch);
    searchOptionSpinner = (Spinner) findViewById(R.id.searchOptionSpinner);
    filterSpinner = (Spinner) findViewById(R.id.filterSpinner);
    inputSearch.addTextChangedListener(new TextWatcher() {
      
      @Override
      public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
        // When user changed the Text
        //SearchActivity.this.adapter.getFilter().filter(cs.toString());
        String selectedSearchOption = searchOptionSpinner.getSelectedItem().toString();
        String selectedDistanceOption = filterSpinner.getSelectedItem().toString();
        
        LinearLayout ll = (LinearLayout) findViewById(R.id.btnLay);
        ll.removeAllViews();
        
        if (cs.toString().equals("")) {
          adapter.clear();
          if (selectedSearchOption.toLowerCase().equals("by popularity")) {
            getPopularityItems("-1");
            filterSpinner.setVisibility(View.GONE);
          } else if (selectedSearchOption.toLowerCase().equals("by distance")) {
            filterSpinner.setVisibility(View.VISIBLE);
            adapter.clear();
          } else if (selectedSearchOption.toLowerCase().equals("by keyword")) {
            filterSpinner.setVisibility(View.GONE);
            adapter.clear();
          }
        } else {
          
          switch (selectedSearchOption.toLowerCase()) {
            case "by popularity":
              filterSpinner.setVisibility(View.GONE);
              getPopularityItems(cs.toString());
              break;
            
            case "by keyword":
              filterSpinner.setVisibility(View.GONE);
              getKeywordItems(cs.toString());
              break;
            
            case "by distance":
              filterSpinner.setVisibility(View.VISIBLE);
              getNearestItems(lat, lng, cs.toString(), selectedDistanceOption);
              break;
            
          }
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
    
    
    // Create an ArrayAdapter using the string array and a default spinner layout
    ArrayAdapter<CharSequence> searchAdapter = ArrayAdapter.createFromResource(this,
            R.array.searchOptions, android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
    searchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
    searchOptionSpinner.setAdapter(searchAdapter);
    
    // Create an ArrayAdapter using the string array and a default spinner layout
    ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
            R.array.distanceOptions, android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
    filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
    filterSpinner.setAdapter(filterAdapter);
    
    searchOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position,
                                 long id) {
        String selectedSearchOption = searchOptionSpinner.getSelectedItem().toString();
        String selectedDistanceOption = filterSpinner.getSelectedItem().toString();
        
        String searchTerm = inputSearch.getText().toString();
        if (!searchTerm.equals("")) {
          switch (selectedSearchOption.toLowerCase()) {
            case "by popularity":
              filterSpinner.setVisibility(View.GONE);
              getPopularityItems(searchTerm.toString());
              break;
            
            case "by keyword":
              filterSpinner.setVisibility(View.GONE);
              getKeywordItems(searchTerm.toString());
              break;
            
            case "by distance":
              filterSpinner.setVisibility(View.VISIBLE);
              getNearestItems(lat, lng, searchTerm.toString(), selectedDistanceOption);
              break;
            
          }
        } else if (selectedSearchOption.toLowerCase().equals("by popularity")) {
          getPopularityItems("-1");
          filterSpinner.setVisibility(View.GONE);
        } else if (selectedSearchOption.toLowerCase().equals("by distance")) {
          filterSpinner.setVisibility(View.VISIBLE);
          adapter.clear();
        } else if (selectedSearchOption.toLowerCase().equals("by keyword")) {
          filterSpinner.setVisibility(View.GONE);
          adapter.clear();
        } else
          adapter.clear();
        
      }
      
      @Override
      public void onNothingSelected(AdapterView<?> parentView) {
        return;
      }
      
    });
    
    filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position,
                                 long id) {
        //      String selectedSearchOption = searchOptionSpinner.getSelectedItem().toString();
        String selectedDistanceOption = filterSpinner.getSelectedItem().toString();
        String searchTerm = inputSearch.getText().toString();
        
        getNearestItems(lat, lng, searchTerm, selectedDistanceOption.toString());
      }
      
      @Override
      public void onNothingSelected(AdapterView<?> parentView) {
        return;
      }
      
    });
  }
  
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuItem item = menu.findItem(R.id.action_scan);
    item.setVisible(true);
    super.onPrepareOptionsMenu(menu);
    return true;
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.action_scan, menu);
    
    
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      
      case R.id.action_scan:
        finish();
        return true;
      
      default:
        return super.onOptionsItemSelected(item);
      
    }
  }
  
  public JSONArray getAllItems() {
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    ServerRequest sr = new ServerRequest();
    JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getallitems", params);
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
  
  public void getKeywordItems(String query) {
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
    keywordItem_json = json_item;
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
    
    Btnfooter(items.size());
    if (data != null)
      data.clear();
    data = items;
    loadList(0);
    
    CheckBtnBackGroud(0);
  }
  
  public void getPopularityItems(String query) {
    
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    ServerRequestGet sr = new ServerRequestGet();
    
    JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "searchPopularityitems/" + query,
            params);
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
    popularityItem_json = json_item;
    ArrayList<Item> items3 = new ArrayList<Item>();
    for (int i = 0; i < popularityItem_json.length(); i++) {
      try {
        json_data = popularityItem_json.getJSONObject(i);
        JsonParser parser = new JsonParser();
        JsonElement mJson = parser.parse(json_data.toString());
        item = gson.fromJson(mJson, Item.class);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      items3.add(item);
    }
    
    Btnfooter(items3.size());
    if (data != null)
      data.clear();
    
    data = items3;
    loadList(0);
    
    CheckBtnBackGroud(0);
    
  }
  
  
  public void getNearestItems(double lat, double lng, String itemName, String distanceOption) {
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    ServerRequest sr = new ServerRequest();
    params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
    params.add(new BasicNameValuePair("lng", String.valueOf(lng)));
    params.add(new BasicNameValuePair("item", itemName));
    params.add(new BasicNameValuePair("distance", distanceOption));
    JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "searchNearestitems", params);
    
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
    nearestItem_json = json_item;
    
    ArrayList<Item> items2 = new ArrayList<Item>();
    for (int i = 0; i < nearestItem_json.length(); i++) {
      try {
        json_data = nearestItem_json.getJSONObject(i);
        JsonParser parser = new JsonParser();
        JsonElement mJson = parser.parse(json_data.toString());
        item = gson.fromJson(mJson, Item.class);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      items2.add(item);
    }
    Btnfooter(items2.size());
    if (data != null)
      data.clear();
    
    data = items2;
    loadList(0);
    
    CheckBtnBackGroud(0);
  }
  
  
  private void Btnfooter(int noOfRecords) {
    int val = noOfRecords % 10;
    val = val == 0 ? 0 : 1;
    noOfBtns = noOfRecords / 10 + val;
    LinearLayout ll = (LinearLayout) findViewById(R.id.btnLay);
    
    btns = new Button[noOfBtns];
    ll.removeAllViews();
    for (int i = 0; i < noOfBtns; i++) {
      btns[i] = new Button(this);
      btns[i].setText("" + (i + 1));
      
      // ll.removeAllViews();
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
              LayoutParams.WRAP_CONTENT);
      ll.addView(btns[i], lp);
      
      final int j = i;
      btns[j].setOnClickListener(new View.OnClickListener() {
        
        @Override
        public void onClick(View view) {
          loadList(j);
          CheckBtnBackGroud(j);
        }
      });
    }
    
  }
  
  /**
   * Method for Checking Button Backgrounds
   */
  private void CheckBtnBackGroud(int index) {
    // title.setText("Page "+(index+1)+" of "+noOfBtns);
    for (int i = 0; i < noOfBtns; i++) {
      if (i == index) {
        btns[index].setBackgroundDrawable(getResources().getDrawable(R.drawable.box_black));
        btns[i].setTextColor(getResources().getColor(android.R.color.white));
      } else {
        btns[i].setBackgroundColor(getResources().getColor(android.R.color.white));
        btns[i].setTextColor(getResources().getColor(android.R.color.black));
      }
    }
    
  }
  
  /**
   * Method for loading data in listview
   *
   * @param number
   */
  private void loadList(int number) {
    ArrayList<Item> sort = new ArrayList<Item>();
    
    int start = number * 10;
    for (int i = start; i < (start) + 10; i++) {
      if (i < data.size()) {
        sort.add(data.get(i));
      }
    }
    adapter = new ItemAdapter(SearchActivity.this, R.layout.list_search, sort, true,
            SearchActivity.super.user_id, false);
    resultLV.setAdapter(adapter);
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode == locationPermissionCode) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), ScanBarcodeActivity.class);
        startActivity(intent);
      } else {
        Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
      }
    }
  }
}