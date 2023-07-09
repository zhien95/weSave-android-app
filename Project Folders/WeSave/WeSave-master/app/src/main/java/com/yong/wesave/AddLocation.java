package com.yong.wesave;

/**
 * Created by Yong0156 on 28/2/2017.
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */
/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.yong.wesave.adapter.AddLocationAdapter;
import com.yong.wesave.apiobject.Foursquare;
import com.yong.wesave.apiobject.LocationCategory;
import com.yong.wesave.apiobject.LocationModel;
import com.yong.wesave.apiobject.Venue;
import com.yong.wesave.asyntask.AsyncRequestVenue;

import java.util.ArrayList;
import java.util.List;

public class AddLocation extends WeSaveActivity {

    private List<LocationModel> data;
    private ListView listView;
    private EditText txtAddLocation;
    private AddLocationAdapter adapter;
    private LinearLayout loadingLayout;
    LocationManager mLocationManager;
    String query;
    boolean nearby;

    private BroadcastReceiver venueBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Foursquare fqData = (Foursquare) intent.getSerializableExtra("response");
            for (Venue v : fqData.response.venues) {
                data.add(venueToLocation(v));
            }
            adapter.notifyDataSetChanged();
            show(listView);
            hide(loadingLayout);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_add_location, R.id.content_frame);
        super.replaceTitle(R.string.title_add_location);

        nearby = getIntent().getBooleanExtra("nearby", true);
        data = new ArrayList<LocationModel>();
        loadingLayout = (LinearLayout) findViewById(R.id.loadingLayout);
        txtAddLocation = (EditText) findViewById(R.id.add_location_inputSearch);
        txtAddLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                query = txtAddLocation.getText().toString();
                data.clear();
                adapter.notifyDataSetChanged();
                new AsyncRequestVenue(getApplicationContext(), query, nearby).execute("");
            }
        });

        listView = (ListView) findViewById(R.id.add_location_list_view);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent data = new Intent();
                LocationModel temp = (LocationModel) adapter.getItem(position);
                data.putExtra("locationModel", temp);
                setResult(RESULT_OK, data);
                finish();
            }

        });

        adapter = new AddLocationAdapter(this, data);
        listView.setAdapter(adapter);

        hide(listView);
        show(loadingLayout);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                venueBroadcastReceiver,
                new IntentFilter(Foursquare.class.getSimpleName()));
        new AsyncRequestVenue(getApplicationContext(), query, nearby).execute("");
    }

    private void hide(View v) {
        v.setVisibility(View.GONE);
    }

    private void show(View v) {
        v.setVisibility(View.VISIBLE);
    }

    public static LocationModel venueToLocation(Venue venue) {
        LocationModel temp = new LocationModel();
        temp.setName(venue.name);
        temp.setLat(venue.location.lat);
        temp.setLng(venue.location.lng);
        temp.setDistance(venue.location.distance);
        temp.setAddress(venue.location.address);
        temp.setId(venue.id);

        if (venue.location.cc != null && !venue.location.cc.equals("")) {
            temp.addTags(venue.location.cc);
        }
        if (venue.location.state != null && !venue.location.state.equals("")) {
            temp.addTags(venue.location.state);
        }
        if (venue.location.city != null && !venue.location.city.equals("")) {
            temp.addTags(venue.location.city);
        }
        if (venue.location.country != null
                && !venue.location.country.equals("")) {
            temp.addTags(venue.location.country);
        }
        if (venue.location.address != null
                && !venue.location.address.equals("")) {
            String[] addressParts = venue.location.address.split(",");
            for (String addressPart : addressParts) {
                temp.addTags(addressPart.trim());
            }
        }
        if (venue.location.postalCode != null
                && !venue.location.postalCode.equals("")) {
            temp.addTags(venue.location.postalCode);
        }

        for (LocationCategory c : venue.categories) {
            if (c.primary) {
                temp.setIconURL(c.icon.prefix + "bg_64" + c.icon.suffix);
                temp.setLocationCategory(c.id);
                System.out.println(temp.getName() + temp.getLocationCategory());
                break;
            }
        }

        return temp;
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        Location l;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                l = null;
            } else {
                l = mLocationManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
