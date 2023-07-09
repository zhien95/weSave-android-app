package com.yong.wesave.adapter;

/**
 * Created by Yong on 19/1/2017.
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */
/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.yong.wesave.R;
import com.yong.wesave.apiobject.Store;
import com.yong.wesave.common.Constants;

import java.util.ArrayList;

public class StoreAdapter extends ArrayAdapter<Store> implements Filterable {

    AQuery androidAQuery;
    int resource;
    String response;
    Context context;
    ArrayList<Store> items;
    ArrayList<Store> nitems;

    //Initialize adapter
    public StoreAdapter(Context context, int resource, ArrayList<Store> items) {
        super(context, resource, items);
        this.resource = resource;
        this.items = items;
        this.nitems = items;
        this.context = context;
        this.androidAQuery = new AQuery(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout pricingView;
        //Get the current object
        final Store pr = getItem(position);

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

        TextView itemName = (TextView) pricingView.findViewById(R.id.name);
        ImageView itemImg = (ImageView) pricingView.findViewById(R.id.image);
        LinearLayout entry = (LinearLayout) pricingView.findViewById(R.id.entry);

        //Set Item details
        androidAQuery.id(itemImg).image(Constants.API_BASE_URL_STORE_IMAGES + pr.image,
                true, true, itemImg.getWidth(), R.drawable.default_item_image);
        itemName.setText(pr.store_name);
        entry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("store_id", pr.id);
                ((Activity) context).setResult(Activity.RESULT_OK, resultIntent);
                ((Activity) context).finish();
            }
        });

        return pricingView;
    }

    public Store getItem(int position) {
        return nitems.get(position);
    }

    public int getCount() {
        return nitems.size();
    }

    @Override
    public Filter getFilter() {
        PlanetFilter planetFilter = null;
        if (planetFilter == null) {
            planetFilter = new PlanetFilter();
        }
        return planetFilter;
    }

    private class PlanetFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            nitems = items;
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = nitems;
                results.count = nitems.size();
            } else {
                ArrayList<Store> nPlanetList = new ArrayList<Store>();

                for (Store p : nitems) {
                    if (p.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                        nPlanetList.add(p);
                }

                results.values = nPlanetList;
                results.count = nPlanetList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            nitems = (ArrayList<Store>) results.values;
            notifyDataSetChanged();
        }
    }
}