package com.yong.wesave.adapter;

/**
 * Created by Yong on 19/1/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yong.wesave.R;
import com.yong.wesave.ShoppingPlanResultActivity;
import com.yong.wesave.apiobject.Item;
import com.yong.wesave.apiobject.ShoppingPlan;

import java.util.ArrayList;

public class ShoppingPlanAdapter extends ArrayAdapter<ShoppingPlan> {

    int resource;
    String response;
    Context context;
    ArrayList<ShoppingPlan> plans;

    //Initialize adapter
    public ShoppingPlanAdapter(Context context, int resource, ArrayList<ShoppingPlan> items) {
        super(context, resource, items);
        this.resource = resource;
        this.plans = items;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout pricingView;
        //Get the current object
        final ShoppingPlan plan = getItem(position);

        //Inflate the view
        if (convertView == null) {
            pricingView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, pricingView, true);
        } else {
            pricingView = (LinearLayout) convertView;
        }

        TextView planName = (TextView) pricingView.findViewById(R.id.name);
        TextView planTotal = (TextView) pricingView.findViewById(R.id.total);
        LinearLayout entry = (LinearLayout) pricingView.findViewById(R.id.entry);

        //Set Item details
        planName.setText(plan.plan_name);
        planTotal.setText("Total Price: S$ " + plan.total);
        entry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ArrayList<Item> resList = fromJSON(plan.plan);
                Intent intent = new Intent(context, ShoppingPlanResultActivity.class);
                intent.putExtra("res", resList);
                intent.putExtra("total", plan.total);
                intent.putExtra("saved", true);
                intent.putExtra("planId", Integer.toString(plan.id));
                ((Activity) context).startActivityForResult(intent, 0);
            }
        });

        return pricingView;
    }

    public ShoppingPlan getItem(int position) {
        return plans.get(position);
    }

    public int getCount() {
        return plans.size();
    }

    public ArrayList<Item> fromJSON(String plan) {
        Gson gson = new Gson();
        ArrayList<Item> resList = new ArrayList<Item>();
        String[] json_strings = plan.split(";");
        for (String s : json_strings) {
            Item i = gson.fromJson(s, Item.class);
            resList.add(i);
        }
        return resList;
    }

}