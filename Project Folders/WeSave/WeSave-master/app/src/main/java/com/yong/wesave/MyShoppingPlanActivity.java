package com.yong.wesave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yong.wesave.adapter.ShoppingPlanAdapter;
import com.yong.wesave.apiobject.ShoppingPlan;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyShoppingPlanActivity extends WeSaveActivity {

    // List view
    private ListView lv;
    private Button createBtn;

    // Listview Adapter
    ShoppingPlanAdapter adapter;

    JSONArray json_plans;
    JSONObject json_data;
    ShoppingPlan plan;
    Gson gson = new Gson();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_my_shopping_plan, R.id.content_frame);
        super.replaceTitle(R.string.title_my_plans);

        lv = (ListView) findViewById(R.id.list_view);
        createBtn = (Button) findViewById(R.id.createPlanBtn);

        json_plans = getShoppingPlan();

        ArrayList<ShoppingPlan> plans = new ArrayList<ShoppingPlan>();
        if (json_plans != null) {
            for (int i = 0; i < json_plans.length(); i++) {
                try {
                    json_data = json_plans.getJSONObject(i);
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(json_data.toString());
                    plan = gson.fromJson(mJson, ShoppingPlan.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                plans.add(plan);
            }
        }
        adapter = new ShoppingPlanAdapter(this, R.layout.list_shopping_plan, plans);
        lv.setAdapter(adapter);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyShoppingPlanActivity.this, CreateShoppingPlanActivity.class);
                startActivityForResult(i, 0);
            }
        });
    }


    public JSONArray getShoppingPlan() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", super.user_id));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getshoppingplans", params);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}