package com.yong.wesave.asyntask;

/**
 * Created by Yong0156 on 28/2/2017.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.yong.wesave.R;
import com.yong.wesave.apiobject.Foursquare;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AsyncRequestVenue extends AsyncTask<String, Integer, Foursquare> {

    private final Context context;
    private double lng;
    private double lat;
    LocationManager mLocationManager;
    String query;
    boolean nearby;

    public static final int MAX_QUERY = 20;
    //private static final String FOURSQUARE_ID = "PHQCMDN2X3ARXWF55UFCDT1U4X15B3P4H4TTPQG2OBINB3WB";
    //private static final String FOURSQUARE_SECRET = "FYY3QS45GPUEMK225XIZ5ZOTA0ACN2LPWKNUAO1NOFMKXDF2";
    private static final String FOURSQUARE_ID = "2HOPSGXKKPAFPLRXPXD02U0CXU23AJTQMZVKWIDS3KL1DCQR";
    private static final String FOURSQUARE_SECRET = "PR3FVBQEFCWXAR3M2XLDN4LZ3SNPDMSTJJEOJTG3OTO5KMPN";
    private static final String FOOD_AND_DRINK = "4bf58dd8d48988d1f9941735";
    private static final String SHOP_AND_SERVICES = "4d4b7105d754a06378d81259";
    private static final String GROCERY_STORE = "4bf58dd8d48988d118951735";
    private static final String SUPERMARKET = "52f2ab2ebcbc57f1066b8b46";
    public static final String FOURSQUARE_URL = "https://api.foursquare.com/v2/"; // ll=2.5,112.5&limit=10&client_id=BXNMOEGEZJZVO3FGLBZNXVS3LOABIHHELPIHMZY4DHOWRBDB&client_secret=XOBRGNO2KH0MVJ0LGZYBVNG02KIEOWBFXPU0FSSMEBPULOPH";
    public static final String SEARCH_API_NEARBY = FOURSQUARE_URL
            + "venues/search?categoryId=" + SHOP_AND_SERVICES
            + "&radius=900&ll=%LL%&intent=checkin&limit=" + MAX_QUERY + "&query=%QQ%" + "&client_id="
            + FOURSQUARE_ID + "&client_secret=" + FOURSQUARE_SECRET
            + "&v=%VERSION%";
    public static final String SEARCH_API_SUPERMARKET = FOURSQUARE_URL
            + "venues/search?near=Singapore,Sg&categoryId=" + SUPERMARKET + "," + GROCERY_STORE
            + "&intent=checkin&limit=" + MAX_QUERY + "&query=%QQ%" + "&client_id="
            + FOURSQUARE_ID + "&client_secret=" + FOURSQUARE_SECRET
            + "&v=%VERSION%";

    public AsyncRequestVenue(Context context, String query, boolean nearby) {
        this.context = context;
        this.query = query;
        this.nearby = nearby;


        Location location = getLastKnownLocation();
        lng = location.getLongitude();
        lat = location.getLatitude();

    }

    @Override
    protected void onPostExecute(Foursquare result) {
        super.onPostExecute(result);
        if (result != null) {
            Intent broadcastIntent = new Intent(
                    Foursquare.class.getSimpleName());
            broadcastIntent.putExtra("response", result);
            LocalBroadcastManager.getInstance(context).sendBroadcast(
                    broadcastIntent);
        }
    }

    @Override
    protected Foursquare doInBackground(String... values) {
        Foursquare responseObject = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date now = new Date();
        String finalUrl;
        try {
            if (nearby) {
                finalUrl = SEARCH_API_NEARBY.replaceAll("%LL%", lat + "," + lng)
                        .replaceAll("%VERSION%", format.format(now));
            } else {
                finalUrl = SEARCH_API_SUPERMARKET.replaceAll("%LL%", lat + "," + lng)
                        .replaceAll("%VERSION%", format.format(now));
            }
            if (query != null && !query.equals("")) {
                finalUrl = finalUrl.replaceAll("%QQ%", query);
            } else {
                finalUrl = finalUrl.replaceAll("%QQ%", "");
            }
            System.out.println("finalUrl is " + finalUrl);
            response = httpclient.execute(new HttpGet(finalUrl));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();

                Log.i(context.getString(R.string.app_name), "Receive "
                        + responseString);
                Gson gson = new Gson();
                responseObject = (Foursquare) gson.fromJson(responseString,
                        Foursquare.class);
            } else {
                // Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace(System.out);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        return responseObject;
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        Location l;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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