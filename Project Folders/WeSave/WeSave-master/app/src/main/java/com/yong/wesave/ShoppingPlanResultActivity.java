package com.yong.wesave;

/**
 * Created by Yong on 8/1/2017.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yong.wesave.adapter.ExpandableListResAdapter;
import com.yong.wesave.apiobject.Item;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ShoppingPlanResultActivity extends WeSaveActivity implements View.OnClickListener {

    Button savePlanBtn;
    TextView totalPrice;
    ExpandableListResAdapter listAdapter;
    ExpandableListView expListView;
    List<Item> resList;
    ArrayList<String> stores = new ArrayList<String>();
    HashMap<String, List<Item>> listItemChild;
    String planName;
    Dialog d;
    Boolean saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_shopping_plan_result, R.id.content_frame);
        super.replaceTitle(R.string.title_shopping_plan);

        resList = getIntent().getParcelableArrayListExtra("res");
        saved = getIntent().getBooleanExtra("saved", false);
        //stores = getIntent().getStringArrayListExtra("stores");

        expListView = (ExpandableListView) findViewById(R.id.resList);
        savePlanBtn = (Button) findViewById(R.id.savePlanBtn);
        totalPrice = (TextView) findViewById(R.id.total);
        // backBtn = (Button) findViewById(R.id.backBtn);

        totalPrice.setText("Total Price: S$ " + getIntent().getStringExtra("total"));
        prepareListData();


        if (saved) {
            savePlanBtn.setText("Delete Plan");
        }

        savePlanBtn.setOnClickListener(this);

        listAdapter = new ExpandableListResAdapter(this, stores, listItemChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub

                return false;
            }
        });

        d = new Dialog(ShoppingPlanResultActivity.this);
        d.setTitle("Save Plan As");
        d.setContentView(R.layout.dialog_save);
        Button b1 = (Button) d.findViewById(R.id.button1);
        final EditText input = (EditText) d.findViewById(R.id.name);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString() != "") {
                    planName = input.getText().toString();
                    d.dismiss();
                    save();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == savePlanBtn) {
            if (saved) {
                removePlan();
                Context context = getApplicationContext();
                CharSequence text = "Plan removed from your list.";
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toast.show();
                setResult(0);
                finish();
            } else {
                d.show();
            }
        }
    }

    private void prepareListData() {
        listItemChild = new HashMap<String, List<Item>>();

        for (Item i : resList) {
            if (listItemChild.containsKey(i.store_name)) {
                listItemChild.get(i.store_name).add(i);
            } else {
                List<Item> list = new ArrayList<Item>();
                list.add(i);
                stores.add(i.store_name);
                listItemChild.put(i.store_name, list);
            }
        }
    }

    public void save() {
        Gson gson = new Gson();
        String jsonInString = "";
        Boolean processedFirst = false;
        for (Item i : resList) {
            if (!processedFirst) {
                jsonInString += gson.toJson(i);
                processedFirst = true;
            } else {
                jsonInString += ";" + gson.toJson(i);
            }
        }


        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", super.user_id));
        params.add(new BasicNameValuePair("plan", jsonInString));
        params.add(new BasicNameValuePair("plan_name", planName));
        params.add(new BasicNameValuePair("total", getIntent().getStringExtra("total")));

        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "saveplan", params);

        if (json != null) {
            try {
                if (json.getString("status").equals("success")) {
                    Context context = getApplicationContext();
                    CharSequence text = "Shopping plan saved.";
                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                    toast.show();
                    Intent i = new Intent(ShoppingPlanResultActivity.this, MyShoppingPlanActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Error saving shopping plan.";
                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void removePlan() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("plan_id", getIntent().getStringExtra("planId")));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "removeplan", params);
    }

}