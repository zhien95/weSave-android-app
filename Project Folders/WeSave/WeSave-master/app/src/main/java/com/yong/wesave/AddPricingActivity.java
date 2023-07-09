package com.yong.wesave;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.yong.wesave.apiobject.Pricing;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import static com.yong.wesave.common.Constants.API_ADD_STORE;
import static com.yong.wesave.common.Constants.API_BASE_URL_API;
import static com.yong.wesave.util.Validation.validateFields;

/**
 * Created by Yong on 12/1/2017.
 */

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

public class AddPricingActivity extends WeSaveActivity implements View.OnClickListener {

    private LinearLayout pricing;
    private EditText originalPrice;
    private EditText promoPrice;
    private EditText quantity;
    private EditText promoStartDate;
    private EditText promoEndDate;
    private EditText promotion;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateFormatterLong = new SimpleDateFormat("EEE, MMM d, yyyy");
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private Switch promoSwitch;
    private TextView status;
    private Button addBtn, backBtn;
    private Button incBtn, decBtn;
    private EditText storeSpinner;
    private Calendar c;
    private Date dateStart, dateEnd;
    List<String> list = new ArrayList<String>();
    List<Integer> values = new ArrayList<Integer>();
    private String storeId = "";
    private String storeName, category, icon, address;
    private int item_id, hasPromo = 0;
    private String originalpricetxt, promopricetxt, promoQty, minprice, promoStart, promoEnd, oprice = "", pprice = "", dollar, loc_lat, loc_long;
    int ADD_LOCATION = 0, year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_add_price, R.id.content_frame);
        super.replaceTitle(R.string.title_add_price);

        item_id = getIntent().getIntExtra("item_id", 0);
        storeSpinner = (EditText) findViewById(R.id.storeSpinner);
        originalPrice = (EditText) findViewById(R.id.originalPrice);
        //originalPrice.setRawInputType(Configuration.KEYBOARD_QWERTY);
        promoPrice = (EditText) findViewById(R.id.promoPrice);
        status = (TextView) findViewById(R.id.statusTextView);
        addBtn = (Button) findViewById(R.id.addPriceButton);
        backBtn = (Button) findViewById(R.id.backButton);
        incBtn = (Button) findViewById(R.id.incrementBtn);
        decBtn = (Button) findViewById(R.id.decrementBtn);
        promoStartDate = (EditText) findViewById(R.id.promoStartDate);
        promoEndDate = (EditText) findViewById(R.id.promoEndDate);
        quantity = (EditText) findViewById(R.id.quantity);
        currencyFormat.setCurrency(Currency.getInstance("SGD"));
        promoSwitch = (Switch) findViewById(R.id.promoSwitchButton);
        promoSwitch.setChecked(false);
        pricing = (LinearLayout) findViewById(R.id.pricing);

        promoPrice.setEnabled(false);
        quantity.setEnabled(false);
        quantity.setTextColor(Color.LTGRAY);
        incBtn.setEnabled(false);
        incBtn.setTextColor(Color.LTGRAY);
        decBtn.setEnabled(false);
        decBtn.setTextColor(Color.LTGRAY);
        promoStartDate.setEnabled(false);
        promoEndDate.setEnabled(false);
        pricing.setFocusable(true);
        pricing.setFocusableInTouchMode(true);

        addBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        incBtn.setOnClickListener(this);
        decBtn.setOnClickListener(this);
        storeSpinner.setOnClickListener(this);
        promoStartDate.setOnClickListener(this);
        promoEndDate.setOnClickListener(this);

        // Promotion switch listener
        promoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pricing.requestFocus();
                if (isChecked == true) {
                    hasPromo = 1;
                    promoPrice.setEnabled(true);
                    quantity.setEnabled(true);
                    quantity.setTextColor(Color.BLACK);
                    incBtn.setEnabled(true);
                    incBtn.setTextColor(Color.BLACK);
                    decBtn.setEnabled(true);
                    if (Integer.parseInt(quantity.getText().toString()) > 1)
                        decBtn.setTextColor(Color.BLACK);
                    promoStartDate.setEnabled(true);
                    promoEndDate.setEnabled(true);
                } else {
                    hasPromo = 0;
                    promoPrice.setText("");
                    promoPrice.clearFocus();
                    promoPrice.setEnabled(false);
                    quantity.setEnabled(false);
                    quantity.setTextColor(Color.LTGRAY);
                    incBtn.setEnabled(false);
                    incBtn.setTextColor(Color.LTGRAY);
                    decBtn.setEnabled(false);
                    decBtn.setTextColor(Color.LTGRAY);
                    promoStartDate.setEnabled(false);
                    promoEndDate.setEnabled(false);
                }
            }
        });

        // Price change listener: set input value to currency format
        originalPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    oprice = originalPrice.getText().toString();
                    if (oprice != "" && !oprice.isEmpty()) {
                        dollar = currencyFormat.format(Double.parseDouble(oprice));
                        originalPrice.setText(dollar.replace("SGD", "S$ "));
                    }
                } else {
                    originalPrice.setText(oprice);
                }
            }
        });

        promoPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    pprice = promoPrice.getText().toString();
                    if (pprice != "" && !pprice.isEmpty()) {
                        dollar = currencyFormat.format(Double.parseDouble(pprice));
                        promoPrice.setText(dollar.replace("SGD", "S$ "));
                    }
                } else {
                    promoPrice.setText(pprice);
                }
            }
        });

        // Get and set promotion dates
        Calendar newCalendar = Calendar.getInstance();
        promoStart = dateFormatter.format(newCalendar.getTime());
        promoEnd = dateFormatter.format(newCalendar.getTime());
        promoStartDate.setText(dateFormatterLong.format(newCalendar.getTime()));
        promoEndDate.setText(dateFormatterLong.format(newCalendar.getTime()));

        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                promoStart = dateFormatter.format(newDate.getTime());
                promoStartDate.setText(dateFormatterLong.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                promoEnd = dateFormatter.format(newDate.getTime());
                promoEndDate.setText(dateFormatterLong.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    pricing.requestFocus();
                    //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }*/

    @Override
    public void onClick(View v) {
        if (v == addBtn) {
            pricing.requestFocus();
            //status.setVisibility(View.GONE);
            minprice = originalpricetxt;
            originalpricetxt = originalPrice.getText().toString().replace("S$ ", "");


            if (storeId == "") {
                storeSpinner.setError("Please select a store.");
                //status.setVisibility(View.VISIBLE);
                //status.setText("Please select a store.");

            } else if (!validateFields(oprice)) {
                originalPrice.setError("Please fill in the original price of the item.");
                //status.setVisibility(View.VISIBLE);
                //status.setText("Please fill in the original price of the item.");

            } else if (hasPromo == 1 && !validateFields(pprice)) {
                promoPrice.setError("Please fill in the promotion price of the item.");
            } else if (hasPromo == 1 && promoStart.compareTo(promoEnd) > 0) {
                promoEndDate.setError("Promotion end date is earlier than start date.");
            } else {
                if (hasPromo == 1) {
                    minprice = pprice;
                    promopricetxt = promoPrice.getText().toString().replace("S$ ", "");
                    promoQty = quantity.getText().toString();
                } else {
                    promopricetxt = null;
                    promoQty = "0";
                    promoStart = null;
                    promoEnd = null;
                }
                addPrice();
            }
        } else if (v == storeSpinner) {
            pricing.requestFocus();
            Intent i = new Intent(this, AddLocation.class);
            i.putExtra("nearby", true);
            startActivityForResult(i, ADD_LOCATION);

        } else if (v == backBtn) {
            finish();

        } else if (v == incBtn) {
            pricing.requestFocus();
            int q = Integer.parseInt(quantity.getText().toString()) + 1;
            if (q == 2) {
                decBtn.setEnabled(true);
                decBtn.setTextColor(Color.BLACK);
            }
            quantity.setText(String.valueOf(q));

        } else if (v == decBtn) {
            pricing.requestFocus();
            int q = Integer.parseInt(quantity.getText().toString()) - 1;
            if (q == 1) {
                decBtn.setEnabled(false);
                decBtn.setTextColor(Color.LTGRAY);
            }
            quantity.setText(String.valueOf(q));

        } else if (v == promoStartDate) {
            pricing.requestFocus();
            fromDatePickerDialog.show();

        } else if (v == promoEndDate) {
            pricing.requestFocus();
            toDatePickerDialog.show();
        }
    }

    public void addPrice() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("item_id", Integer.toString(item_id)));
        params.add(new BasicNameValuePair("store_id", storeId));
        params.add(new BasicNameValuePair("original_price", originalpricetxt));
        params.add(new BasicNameValuePair("promo_price", promopricetxt));
        params.add(new BasicNameValuePair("promo_qty", promoQty));
        params.add(new BasicNameValuePair("creator_id", super.user_id));
        params.add(new BasicNameValuePair("promo_start", promoStart));
        params.add(new BasicNameValuePair("promo_end", promoEnd));
        params.add(new BasicNameValuePair("has_promo", Integer.toString(hasPromo)));
        //params.add(new BasicNameValuePair("lat", loc_lat));
        //params.add(new BasicNameValuePair("long", loc_long));

        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + Constants.API_ADD_PRICE, params);

        if (json != null) {
            try {
                if (json.getString("status").equals("success")) {
                    sendNotificationToFollowers();
                    finish();
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Error inserting new pricing entry.";
                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*
        public void addItemsOnSpinner() {


            list.add("-- Supermarket --");
            values.add(-1);
            getAllSupermarkets(list, values);
            list.add("Select a Store");
            values.add(-1);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list){
                @Override
                public boolean isEnabled(int position) {
                    // TODO Auto-generated method stub
                    if (position == 0) {
                        return false;
                    }
                    return true;
                }

                @Override
                public int getCount() {
                    return super.getCount()-1; // you dont display last item. It is used as hint.
                }
            };
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            storeSpinner.setPrompt("Select a Store");
            storeSpinner.setAdapter(dataAdapter);
            storeSpinner.setSelection(dataAdapter.getCount());

            storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    storeId = values.get(arg2);

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });
        }
    */
    public void getAllSupermarkets(List<String> list, List<Integer> values) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("type", "supermarket"));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + Constants.API_GET_ALL_STORES, params);

        if (json != null) {
            try {
                if (json.getString("status").equals("success")) {
                    JSONArray stores = json.getJSONArray("data");
                    for (int i = 0; i < stores.length(); i++) {
                        list.add(stores.getJSONObject(i).getString("store_name"));
                        values.add(stores.getJSONObject(i).getInt("id"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_LOCATION && resultCode == RESULT_OK) {
            storeId = data.getStringExtra("storeId");
            storeName = data.getStringExtra("storeName");
            category = data.getStringExtra("category");
            icon = data.getStringExtra("icon");
            address = data.getStringExtra("address");
            loc_lat = data.getStringExtra("lat");
            loc_long = data.getStringExtra("long");
            storeSpinner.setText(storeName);

            addstore();
        }
    }

    private void addstore() {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("store_name", storeName));
        params.add(new BasicNameValuePair("store_id", storeId));
        params.add(new BasicNameValuePair("category", category));
        params.add(new BasicNameValuePair("icon", icon));
        params.add(new BasicNameValuePair("address", address));
        params.add(new BasicNameValuePair("lat", loc_lat));
        params.add(new BasicNameValuePair("long", loc_long));

        //for (int i = 0; i < params.size(); i++)
        //System.out.println("before add store ---------:" + params);

        ServerRequest sr = new ServerRequest();
        sr.getJSON(API_BASE_URL_API + API_ADD_STORE, params);
    }

    public void sendNotificationToFollowers() {

        // Build WeSaveNotification
        Intent resultIntent = new Intent(getApplicationContext(), Pricing.class);
        resultIntent.putExtra("item_id", item_id);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        super.mBuilder.setContentTitle("WeSave")
                .setContentText("Check out the new promotion deal on one of your following items!")
                .setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Get followers id
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("item_id", Integer.toString(item_id)));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getfollowers", params);
        JSONArray json_followers = null;
        System.out.println(json.toString());
        if (json != null) {
            try {
                if (json.getString("status").equals("success")) {
                    json_followers = json.getJSONArray("data");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Send WeSaveNotification
        if (json_followers != null) {
            for (int i = 0; i < json_followers.length(); i++) {
                try {
                    JSONObject json_obj = json_followers.getJSONObject(i);
                    int follower_id = json_obj.getInt("id");
                    mNotifyMgr.notify(follower_id, super.mBuilder.build());
                    storenotification(params, "promo", follower_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void storenotification(List<NameValuePair> params, String type, Integer recipient_id) {
        params.add(new BasicNameValuePair("type", type));
        params.add(new BasicNameValuePair("user_id", super.user_id));
        params.add(new BasicNameValuePair("recipient_id", Integer.toString(recipient_id)));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "storenotification", params);

        if (json != null) {
            try {
                if (json.getString("status").equals("success")) {
                    //finish();
                } else {
                    System.out.println("Error inserting new notification entry.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}