package com.yong.wesave;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.yong.wesave.adapter.ItemAdapter;
import com.yong.wesave.adapter.SliderAdapter;
import com.yong.wesave.apiobject.Category;
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
import java.util.Timer;
import java.util.TimerTask;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

public class HomeActivity extends WeSaveActivity {

    private RecyclerView recommendedRecyclerView, nearestRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mDataset;


    // List view
    private GridView lv;

    // Listview Adapter
    ItemAdapter adapter;

    JSONArray cat_json;
    JSONObject json_data;
    Category category;
    Gson gson = new Gson();

    ViewPager viewPager;
    TabLayout indicator;

    List<Integer> drawableImage;
    String userid;

    JSONArray recommendedItems_json,nearestItems_json;
    Pricing pricingItem,nearestItem;
    ArrayList<Pricing> pricing,nearest;

    Double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_home, R.id.content_frame);
        super.replaceTitle(R.string.app_name);

        //Slider
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (TabLayout) findViewById(R.id.indicator);
        drawableImage = new ArrayList<>();
        drawableImage.add(R.drawable.banner1);
        drawableImage.add(R.drawable.banner2);
        drawableImage.add(R.drawable.banner3);

        viewPager.setAdapter(new SliderAdapter(this, drawableImage));
        indicator.setupWithViewPager(viewPager, true);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new HomeActivity.SliderTimer(), 4000, 6000);

        //recommendation
        recommendedRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recommendedItems_json = getRecommendedItems();
        pricing = new ArrayList<Pricing>();

        for (int i = 0; i < recommendedItems_json.length(); i++) {
            try {
                json_data = recommendedItems_json.getJSONObject(i);

                JsonParser parser = new JsonParser();
                JsonElement mJson = parser.parse(json_data.toString());
                pricingItem = gson.fromJson(mJson, Pricing.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pricing.add(pricingItem);
        }



        recommendedRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recommendedRecyclerView.setLayoutManager(mLayoutManager);

        //Determine item based on number of likes.
        //Price based on lowest original or promo price
        mAdapter = new MainAdapter("recommend",HomeActivity.this, R.layout.list_recommended, pricing, true, HomeActivity.super.user_id, false);
        recommendedRecyclerView.setAdapter(mAdapter);

        //Nearest
        nearestRecyclerView = (RecyclerView)findViewById(R.id.recycler_view1);

        nearestItems_json = getNearestItems();
        nearest = new ArrayList<Pricing>();

        for (int i = 0; i < nearestItems_json.length(); i++) {
            try {
                json_data = nearestItems_json.getJSONObject(i);

                JsonParser parser = new JsonParser();
                JsonElement mJson = parser.parse(json_data.toString());
                nearestItem = gson.fromJson(mJson, Pricing.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nearest.add(nearestItem);
        }


        nearestRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        nearestRecyclerView.setLayoutManager(mLayoutManager);

        //Determine item based on number of likes.
        //Price based on lowest original or promo price
        mAdapter = new MainAdapter("nearest", HomeActivity.this, R.layout.list_recommended, nearest, true, HomeActivity.super.user_id, false);
        nearestRecyclerView.setAdapter(mAdapter);
    }

    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            HomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < drawableImage.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    public JSONArray getRecommendedItems() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", userid));

        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "recommendeditems", params);
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


    public JSONArray getNearestItems() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();


        GPSTracker gpsTracker = new GPSTracker(this);

        if (gpsTracker.getIsGPSTrackingEnabled()) {
            lat = gpsTracker.latitude;
            lng = gpsTracker.longitude;
        }


        params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
        params.add(new BasicNameValuePair("lng", String.valueOf(lng)));

        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "nearestitems", params);
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
