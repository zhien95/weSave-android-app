package com.yong.wesave;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yong.wesave.common.Constants.API_BASE_URL_API;
import static com.yong.wesave.common.Constants.API_SEARCH_NEARBY_Stores;
import static com.yong.wesave.util.PermissionHelper.hasAllPermissionsGranted;

/**
 * Done by Zhi En
 */
public class NearbyActivity extends WeSaveActivity implements OnMapReadyCallback {
  private MapView mMapView;
  private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
  private LatLng currLoc;
  private String currAddr;
  private Map<LatLng,String> addrMap = new HashMap<>();
  private int clickCounter = 3;
  private LatLng SINGAPORE = new LatLng(1.290270, 103.851959);
  private int DEFAULT_ZOOM = 11;
  private CameraUpdate currView;
  private List<CameraUpdate> searchViewList = new ArrayList<>();
  private GoogleMap mMap;
  private LocationManager mLocationManager;
  private String geoCodeAPI = "https://maps.googleapis.com/maps/api/geocode/json?address=";
  private String secretKey = "AIzaSyDTheGA9o8lzjMWTUBZdpM3HgGLK3xl_Mk";
  private LocationListener mLocationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      Geocoder geocoder = new Geocoder(getApplicationContext());
      double lat = location.getLatitude();
      double lng = location.getLongitude();
      currLoc = new LatLng(lat, lng);
      try {
         currAddr = geocoder.getFromLocation(lat, lng, 1).get(0).getAddressLine(0);
         //format address to display nicer in marker
         currAddr = currAddr.substring(0, currAddr.lastIndexOf(","))
                + "\n" + currAddr.substring(currAddr.lastIndexOf(",") + 2);
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (ActivityCompat.checkSelfPermission(getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED ||
              ActivityCompat.checkSelfPermission(getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED) {
        if (mMap != null) {
          updateMap(currLoc);
          
        }
      }
      
    }
    
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    
    @Override
    public void onProviderEnabled(String s) {
    }
    
    @Override
    public void onProviderDisabled(String s) {
    }
  };
  private long LOCATION_REFRESH_TIME = 6000; //millisecond
  private float LOCATION_REFRESH_DISTANCE = 100; //meters
  private String[] permissions = {
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_FINE_LOCATION,
  };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.replaceContentLayout(R.layout.activity_nearby, R.id.content_frame);
    super.replaceTitle(R.string.title_supermarket);
    //Get latitude and Longitude
    mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
    //check location permissions
    if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED) {
      mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
              LOCATION_REFRESH_TIME,
              LOCATION_REFRESH_DISTANCE, mLocationListener);
      
    } else {
      ActivityCompat.requestPermissions(this,
              permissions, 1);
    }
  
    Bundle mapViewBundle = null;
    mMapView = findViewById(R.id.map);
    mMapView.onCreate(mapViewBundle);
    mMapView.getMapAsync(this);
    SearchView searchBar = findViewById(R.id.locationSearch);
    Toast.makeText(getApplicationContext(), "Waiting for location service", Toast.LENGTH_LONG).show();
    searchBar.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String s) {
        String addr = s.replace(" ", "+");
        String query = geoCodeAPI + addr + "&key=" + secretKey;
        try {
          ServerRequest sr = new ServerRequest();
          JSONObject data = sr.getJSON(query, null)
                  .getJSONArray("results")
                  .getJSONObject(0);
          JSONObject loc = data.getJSONObject("geometry").getJSONObject("location");
          String formattedAddress = data.getString("formatted_address");
          searchBar.setQuery(formattedAddress, false);
          double lat = Double.parseDouble(loc.getString("lat"));
          double lng = Double.parseDouble(loc.getString("lng"));
          //update search addresses
          LatLng searchLoc = new LatLng(lat, lng);
          addrMap.put(searchLoc,formattedAddress);
          clickCounter = addrMap.size()-1;
          searchBar.clearFocus();
          //update map
          updateMap(searchLoc, false);
          
        } catch (Exception e) {
          Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
          e.printStackTrace();
        }
        return true;
      }
      
      @Override
      public boolean onQueryTextChange(String s) {
        return false;
      }
    });
    Button currLocButton = findViewById(R.id.currentLocationButton);
    currLocButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (currLoc != null && currView != null && mMap != null) {
          mMap.moveCamera(currView);
        } else {
          if (currLoc != null) updateMap(currLoc);
          else
            Toast.makeText(getApplicationContext(), "Can't Find Current Location",
                    Toast.LENGTH_LONG).show();
        }
      }
    });
    Button searchLocButton = findViewById(R.id.searchLocationButton);
    searchLocButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (addrMap.size() != 0 && searchViewList.size() != 0 && mMap != null) {
          if(addrMap.size() == 0) {
            mMap.moveCamera(searchViewList.get(clickCounter));
          }else{
            clickCounter = (clickCounter +1) % addrMap.size();
            mMap.moveCamera(searchViewList.get(clickCounter));
            
          }
        } else {
          Toast.makeText(getApplicationContext(), "No Searched Location", Toast.LENGTH_LONG).show();
        }
      }
    });
  }
  
  @Override
  public void onMapReady(GoogleMap map) {
    mMap = map;
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SINGAPORE, DEFAULT_ZOOM));
    if (currLoc != null) {
      //set current location marker
      updateMap(currLoc);
    }
    
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
    if (mapViewBundle == null) {
      mapViewBundle = new Bundle();
      outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
    }
    mMapView.onSaveInstanceState(mapViewBundle);
  }
  
  private void updateMap(LatLng loc) {
    updateMap(loc, true);
  }
  
  //if using search location bar, set clearMap to false
  private void updateMap(LatLng loc, boolean clearMap) {
    //Clear Map
    if (clearMap) {
      mMap.clear();
      //Mark current location
      mMap.addMarker(new MarkerOptions()
              .title("Current Location")
              .position(loc)
              .icon(bitmapDescriptorFromVector(getApplicationContext(),
                      R.drawable.ic_gps_fixed_blue)).zIndex(1))
              .setSnippet(currAddr);
    } else {
      mMap.addMarker(new MarkerOptions().position(loc)
              .title("Searched Location")
              .snippet(addrMap.get(loc))
              .icon(bitmapDescriptorFromVector(getApplicationContext()
                      , R.drawable.ic_gps_fixed_red)).zIndex(1));
      
    }
    //Green, Orange, Red Icon Generators
    IconGenerator gIconGenerator = new IconGenerator(this);
    gIconGenerator.setStyle(IconGenerator.STYLE_GREEN);
    IconGenerator oIconGenerator = new IconGenerator(this);
    oIconGenerator.setStyle(IconGenerator.STYLE_ORANGE);
    IconGenerator rIconGenerator = new IconGenerator(this);
    rIconGenerator.setStyle(IconGenerator.STYLE_RED);
    BitmapDescriptor icon;
    //Get distance preference
    SharedPreferences sharedPreferences = getSharedPreferences("0", Context.MODE_PRIVATE);
    String distOption = sharedPreferences.getString("preferredDistance", "0");
    //0 = < 1KM, 1 = 1KM - 2KM, 3 = beyond 2KM
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("lat", Double.toString(loc.latitude)));
    params.add(new BasicNameValuePair("lng", Double.toString(loc.longitude)));
    params.add(new BasicNameValuePair("distOption", distOption));
    ServerRequest sr = new ServerRequest();
    JSONObject json = sr.getJSON(API_BASE_URL_API + API_SEARCH_NEARBY_Stores, params);
    System.out.println(json);
    JSONArray json_item;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    builder.include(loc);
    if (json != null) {
      try {
        if (json.getString("status").equals("success")) {
          json_item = json.getJSONArray("data");
          for (int i = 0; i < json_item.length(); i++) {
            JSONObject supermarket = json_item.getJSONObject(i);
            String name = supermarket.getString("store_name");
            double lat = Double.parseDouble(supermarket.getString("lat"));
            double lng = Double.parseDouble(supermarket.getString("lng"));
            LatLng pos = new LatLng(lat, lng);
            String address = supermarket.getString("address");
            address = address.contains(",")? address.substring(0, address.lastIndexOf(","))
                    + "\n" + address.substring(address.lastIndexOf(",") + 2) : address;
            double dist = supermarket.getDouble("distance");
            if (dist <= 1) {
              icon = BitmapDescriptorFactory.fromBitmap(gIconGenerator.makeIcon(dist + "KM"));
            } else if (dist > 1 && dist < 2) {
              icon = BitmapDescriptorFactory.fromBitmap(oIconGenerator.makeIcon(dist + "KM"));
            } else {
              icon = BitmapDescriptorFactory.fromBitmap(rIconGenerator.makeIcon(dist + "KM"));
            }
            mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .icon(icon)
                    .title(name)
                    .snippet(address)
                    .zIndex(0)
            );
            builder.include(pos);
          }
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
      mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker arg0) {
          return null;
        }
        
        @Override
        public View getInfoContents(Marker marker) {
          LinearLayout info = new LinearLayout(getApplicationContext());
          info.setOrientation(LinearLayout.VERTICAL);
          TextView title = new TextView(getApplicationContext());
          title.setTextColor(Color.BLACK);
          title.setGravity(Gravity.CENTER);
          title.setTypeface(null, Typeface.BOLD);
          title.setText(marker.getTitle());
          TextView snippet = new TextView(getApplicationContext());
          snippet.setTextColor(Color.GRAY);
          snippet.setText(marker.getSnippet());
          snippet.setGravity(Gravity.LEFT);
          info.addView(title);
          info.addView(snippet);
          return info;
        }
      });
      LatLngBounds bounds = builder.build();
      int padding = 100; // offset from edges of the map in pixels
      if (clearMap) {
        currView = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(currView);
      } else {
        CameraUpdate searchView = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        searchViewList.add(searchView);
        mMap.moveCamera(searchView);
      }
      
    }
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    mMapView.onResume();
    if (mMap != null && currLoc != null) {
      updateMap(currLoc);
    }
    if (mMap != null && addrMap.size() != 0) {
      for (LatLng key : addrMap.keySet()) {
        updateMap(key, false);
      }
    }
  }
  
  @Override
  protected void onStart() {
    super.onStart();
    mMapView.onStart();
  }
  
  @Override
  protected void onStop() {
    super.onStop();
    mMapView.onStop();
  }
  
  @Override
  protected void onPause() {
    mMapView.onPause();
    super.onPause();
  }
  
  @Override
  protected void onDestroy() {
    mMapView.onDestroy();
    super.onDestroy();
  }
  
  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mMapView.onLowMemory();
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    boolean permissionGranted = hasAllPermissionsGranted(grantResults);
    if (permissionGranted) {
      Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
      
    } else {
      Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
      Intent intent = new Intent(this, HomeActivity.class);
      startActivity(intent);
    }
  }
  
  private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
    Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
    vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight());
    Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    vectorDrawable.draw(canvas);
    return BitmapDescriptorFactory.fromBitmap(bitmap);
  }
  
}
