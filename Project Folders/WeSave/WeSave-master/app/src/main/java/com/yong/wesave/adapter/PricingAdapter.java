package com.yong.wesave.adapter;

/**
 * Created by Yong on 19/1/2017.
 * <p>
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */
/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import android.text.Spannable;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.yong.wesave.GPSTracker;
import com.yong.wesave.PricingActivity;
import com.yong.wesave.R;
import com.yong.wesave.apiobject.Pricing;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.PricingHolder;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PricingAdapter extends ArrayAdapter<Pricing> {

    AQuery androidAQuery;
    int resource, sorting;
    String user_id;
    String response;
    Context context;
    Date pStart, pEnd;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat dateFormatter = new SimpleDateFormat("d MMM", Locale.ENGLISH);
    DateFormat dateFormatterYear = new SimpleDateFormat("d MMM yy", Locale.ENGLISH);
    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();
    private ArrayList<Pricing> pricings;
    LocationManager locManager;

    //Initialize adapter
    public PricingAdapter(Context context, int resource, ArrayList<Pricing> pricings, String user_id, int choice) {
        super(context, resource, pricings);
        this.resource = resource;
        this.user_id = user_id;
        this.context = context;
        this.pricings = pricings;
        this.sorting = choice;
        this.androidAQuery = new AQuery(context);

        if (choice == 3) try {
            sortByDistance();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        PricingHolder pricingHolder;
        LinearLayout pricingView;
        final int price_id, item_id;
        String store_name, item_name, info, image, original_price, promo_price, promo_qty, promo_start, promo_end,
                username, createdat, has_promo;
        Double lat, lng;
        //TextView pricetxt = null, promotxt = null, datetxt = null, user = null, storename = null, timestamp = null;
        //ImageView store = null;
        //Get the current alert object
        //final Pricing pr = getItem(position);

        //Inflate the view
        if (view == null) {
            pricingView = new LinearLayout(getContext());
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(resource, pricingView, true);
            pricingHolder = new PricingHolder(view);
            view.setTag(pricingHolder);
        } else {
            pricingHolder = (PricingHolder) view.getTag();
            pricingHolder.pricetxt.setText("");
            pricingHolder.promotxt.setText("");
            pricingHolder.datetxt.setText("");
            pricingHolder.user.setText("");
            pricingHolder.name.setText("");
            pricingHolder.timestamp.setText("");
            pricingHolder.entry.setBackgroundColor(Color.TRANSPARENT);

            if (sorting == 5) { //list of contributed pricings
                pricingHolder.name2.setText("");
                pricingHolder.info.setText("");
            }

            int imageResource = getContext().getResources().getIdentifier("@drawable/default_item_image", null, getContext().getPackageName());
            Drawable res = getContext().getResources().getDrawable(imageResource);
            pricingHolder.image.setImageDrawable(res);
        }

        price_id = pricings.get(position).getId();
        store_name = String.valueOf(pricings.get(position).getStoreName());
        item_name = String.valueOf(pricings.get(position).getItemName());
        info = String.valueOf(pricings.get(position).getItemInfo());
        item_id = pricings.get(position).getItemId();
        image = String.valueOf(pricings.get(position).getImage());
        original_price = String.valueOf(pricings.get(position).getOriginalPrice());
        promo_price = String.valueOf(pricings.get(position).getPromoPrice());
        promo_qty = String.valueOf(pricings.get(position).getPromoQty());
        promo_start = String.valueOf(pricings.get(position).getPromoStart());
        promo_end = String.valueOf(pricings.get(position).getPromoEnd());
        username = String.valueOf(pricings.get(position).getUsername());
        createdat = String.valueOf(pricings.get(position).getCreatedat());
        has_promo = String.valueOf(pricings.get(position).getHasPromo());
        lat = pricings.get(position).getLat();
        lng = pricings.get(position).getLng();

        GPSTracker gpsTracker = new GPSTracker(context);
        double currentLat=0,currentLng=0;
        if (gpsTracker.getIsGPSTrackingEnabled()) {
            currentLat = gpsTracker.latitude;
            currentLng = gpsTracker.longitude;
        }

        //Set distance
        Location location = getLastKnownLocation(); //current location
        //Location location = getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double startLat = currentLat;
        double startLong = currentLng;
        double endLat = lat; //store
        double endLong = lng;

        float[] distance = new float[3];
        location.distanceBetween(startLat, startLong, endLat, endLong, distance);
        //location.distanceBetween(1.345234, 103.704731,1.3483,103.6831,distance);

        //for (int i = 0; i < distance.length; i++)
        //      System.out.println("distance " + i + ": " + distance[i]);

        if ((int) distance[0] < 1000)
            pricingHolder.distance.setText(Math.round(distance[0]) + " m");
        else
            pricingHolder.distance.setText(Math.round(distance[0] / 1000) + " km");

        if (sorting == 5) {
            androidAQuery.id(pricingHolder.image).image(Constants.API_BASE_URL_ITEM_IMAGES + image,
                    true, true, pricingHolder.image.getWidth(), R.drawable.default_item_image);

            pricingHolder.name.setText(item_name);
            pricingHolder.info.setText(info);
            pricingHolder.name2.setText(store_name);

            pricingHolder.entry.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //if(!getItemDetails){
                    //   Intent resultIntent = new Intent();
                    //   resultIntent.putExtra("item_id", Integer.toString(item.id));
                    //   ((Activity)context).setResult(Activity.RESULT_OK, resultIntent);
                    //   ((Activity)context).finish();
                    //}else {
                    Intent intent = new Intent(context, PricingActivity.class);
                    intent.putExtra("item_id", item_id);
                    context.startActivity(intent);
                    //}
                }
            });

        } else {
            //Set Store Image
            String suffix = "";
            if (store_name.toLowerCase().contains("giant"))
                suffix = "3";
            else if (store_name.toLowerCase().contains("prime"))
                suffix = "5";
            else if (store_name.toLowerCase().contains("fairprice"))
                suffix = "2";
            else if (store_name.toLowerCase().contains("cold"))
                suffix = "1";
            else if (store_name.toLowerCase().contains("isetan"))
                suffix = "4";
            else if (store_name.toLowerCase().contains("sheng"))
                suffix = "6";
            if (suffix != "") {
                String uri = "@drawable/store_" + suffix;
                int imageResource = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());
                Drawable res = getContext().getResources().getDrawable(imageResource);
                //pricingHolder.image.setImageBitmap(decodeSampledBitmapFromResource(getContext().getResources(), imageResource, 100, 100));

                pricingHolder.image.setImageDrawable(res);
            }

            pricingHolder.name.setText(store_name);

            // Pricing feedback
            final ListPopupWindow listPopupWindow;
            final String[] feedback = {"Helpful", "Outdated"};
            listPopupWindow = new ListPopupWindow(getContext());
            listPopupWindow.setAdapter(new ArrayAdapter(
                    getContext(), R.layout.list_feedback, feedback));
            listPopupWindow.setAnchorView(pricingHolder.entry);
            listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
            listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
            listPopupWindow.setModal(true);
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ServerRequest sr = new ServerRequest();
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("price_id", Integer.toString(price_id)));
                    params.add(new BasicNameValuePair("user_id", user_id));

                    String curFeedback = getFeedback(params);
                    String feedbacktxt = feedback[position];

                    if (feedbacktxt.equals("Helpful")) {
                        params.add(new BasicNameValuePair("feedback", "1"));
                        if (curFeedback.equals("0")) {
                            sr.getJSON(Constants.API_BASE_URL_API + "pricingfeedback", params);
                        } else if (curFeedback.equals("-1")) {
                            sr.getJSON(Constants.API_BASE_URL_API + "updatefeedback", params);
                        }
                    } else if (feedbacktxt.equals("Outdated")) {
                        params.add(new BasicNameValuePair("feedback", "-1"));
                        if (curFeedback.equals("0")) {
                            sr.getJSON(Constants.API_BASE_URL_API + "pricingfeedback", params);
                        } else if (curFeedback.equals("1")) {
                            sr.getJSON(Constants.API_BASE_URL_API + "updatefeedback", params);
                        }
                    }

                    listPopupWindow.dismiss();
                    Context context = getContext();
                    CharSequence text = "Thank you for your feedback";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });

            pricingHolder.entry.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    listPopupWindow.show();
                }
            });
        }

        //Set Promo and Price
        if (has_promo.equals("1")) {
            pricingHolder.pricetxt.setText("S$" + original_price, TextView.BufferType.SPANNABLE);
            Spannable spannable = (Spannable) pricingHolder.pricetxt.getText();
            spannable.setSpan(STRIKE_THROUGH_SPAN, 0, pricingHolder.pricetxt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            pricingHolder.promotxt.setText(promo_qty + " for S$" + promo_price);

            try {
                pStart = inputFormat.parse(promo_start);
                pEnd = inputFormat.parse(promo_end);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date todayDate = null;
            try {
                todayDate = sdf.parse(sdf.format(new Date()));
                pEnd = sdf.parse(sdf.format(pEnd));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (pEnd.before(todayDate)) {
                pricingHolder.datetxt.setText("Promo date: Expired");
                if (sorting != 5) {
                    pricingHolder.entry.setBackgroundColor(Color.LTGRAY);
                }
            } else {
                pricingHolder.datetxt.setText("Promo date: " + dateFormatter.format(pStart) + " - " + dateFormatterYear.format(pEnd));
            }
        } else {
            pricingHolder.pricetxt.setText("S$" + original_price);
        }

        //Set Username and posted date
        pricingHolder.user.setText(username);
        long x = Math.round(Double.parseDouble(createdat));
        pricingHolder.timestamp.setText(DateUtils.getRelativeTimeSpanString(x, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));

        return view;
    }

    public String getFeedback(List<NameValuePair> params) {
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getfeedback", params);

        if (json != null) {
            try {
                if (json.getString("status").equals("success") && json.getJSONObject("data") != null) {
                    return json.getJSONObject("data").getString("feedback");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "0";
    }

    private Location getLastKnownLocation() {
        //locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locManager.getProviders(true);
        Location bestLocation = null;
        Location l;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                l = null;
            } else {
                l = locManager.getLastKnownLocation(provider);
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

    public void sortByDistance() throws ParseException {
        ArrayList<Pricing> nex = new ArrayList<Pricing>(), ex = new ArrayList<Pricing>();
        Double startLat, startLong, lat, lng;
        Location location = getLastKnownLocation(); //current location
        startLat = location.getLatitude();
        startLong = location.getLongitude();

        //split into 2 lists, expired and non-expired
        Date todayDate = null;
        try {
            todayDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < pricings.size(); i++) {
            Pricing p = pricings.get(i);
            if (p.has_promo.equals("1")) {
                Date d = inputFormat.parse(p.promo_end);
                d = sdf.parse(sdf.format(d));
                if (d.before(todayDate)) {
                    ex.add(p);
                } else {
                    nex.add(p);
                }
            } else
                nex.add(p);
        }

        for (int i = 0; i < nex.size(); i++) {
            lat = nex.get(i).getLat();
            lng = nex.get(i).getLng();

            float[] distance = new float[3];
            location.distanceBetween(startLat, startLong, lat, lng, distance);
            nex.get(i).setDistance(distance[0]);
        }

        Collections.sort(nex, new Comparator<Pricing>() {
            public int compare(Pricing o1, Pricing o2) {
                return o1.getDistance().compareTo(o2.getDistance());
            }
        });

        for (int i = 0; i < ex.size(); i++) {
            lat = ex.get(i).getLat();
            lng = ex.get(i).getLng();

            float[] distance = new float[3];
            location.distanceBetween(startLat, startLong, lat, lng, distance);
            ex.get(i).setDistance(distance[0]);
        }

        Collections.sort(ex, new Comparator<Pricing>() {
            public int compare(Pricing o1, Pricing o2) {
                return o1.getDistance().compareTo(o2.getDistance());
            }
        });

        pricings.clear();
        pricings.addAll(nex);
        pricings.addAll(ex);
    }
}
