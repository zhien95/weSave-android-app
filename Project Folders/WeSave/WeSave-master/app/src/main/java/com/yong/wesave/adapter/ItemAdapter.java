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
import android.widget.Toast;

import com.androidquery.AQuery;
import com.yong.wesave.FollowerActivity;
import com.yong.wesave.LikesActivity;
import com.yong.wesave.PricingActivity;
import com.yong.wesave.R;
import com.yong.wesave.apiobject.Item;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> implements Filterable {

    AQuery androidAQuery;
    int resource;
    String response;
    Context context;
    ArrayList<Item> items;
    ArrayList<Item> nitems;
    Boolean getItemDetails;
    Boolean getContributedItems;
    String user_id;

    //Initialize adapter
    public ItemAdapter(Context context, int resource, ArrayList<Item> items, Boolean getItemDetails, String user_id, Boolean getContributedItems) {
        super(context, resource, items);
        this.resource = resource;
        this.items = items;
        this.nitems = items;
        this.context = context;
        this.androidAQuery = new AQuery(context);
        this.getItemDetails = getItemDetails;
        this.getContributedItems = getContributedItems;
        this.user_id = user_id;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout view;
        //Get the current object
        final Item item = getItem(position);

        //Inflate the view
        if (convertView == null) {
            view = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, view, true);
        } else {
            view = (LinearLayout) convertView;
        }

        TextView itemName = (TextView) view.findViewById(R.id.name);
        TextView itemInfo = (TextView) view.findViewById(R.id.info);
        ImageView itemImg = (ImageView) view.findViewById(R.id.image);
        LinearLayout entry = (LinearLayout) view.findViewById(R.id.entry);
        ImageView deleteBtn = (ImageView) view.findViewById(R.id.deleteBtn);
        TextView createdAt = (TextView) view.findViewById(R.id.tv_duration);

        if (getContributedItems) {
            TextView likecount = (TextView) view.findViewById(R.id.tv_likes);
            TextView followcount = (TextView) view.findViewById(R.id.tv_follows);
            likecount.setText(item.getLikeCount() + " likes");
            followcount.setText(item.getFollowCount() + " follows");

            likecount.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (Integer.parseInt(item.getLikeCount()) > 0) {
                        Intent intent = new Intent(context, LikesActivity.class);
                        intent.putExtra("item_id", item.getId());
                        context.startActivity(intent);
                    }
                }
            });

            followcount.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context, FollowerActivity.class);
                    intent.putExtra("item_id", item.getId());
                    context.startActivity(intent);
                }
            });
        }
        //Set Item details
        androidAQuery.id(itemImg).image(Constants.API_BASE_URL_ITEM_IMAGES + item.getImage(),
                true, true, itemImg.getWidth(), R.drawable.default_item_image);
        //itemImg.setImageBitmap(decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.default_item_image, 100, 100));

        itemName.setText(item.getName());
        itemInfo.setText("(" + item.getInfo() + ")");

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String d = "";
        if (createdAt != null) {
            try {
                d = sdf.format(inputFormat.parse(item.getCreatedAt()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            createdAt.setText(d);
        }

        entry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!getItemDetails) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("item_id", Integer.toString(item.id));
                    ((Activity) context).setResult(Activity.RESULT_OK, resultIntent);
                    ((Activity) context).finish();
                    
                } else {
                    Intent intent = new Intent(context, PricingActivity.class);
                    intent.putExtra("item_id", item.getId());
                    context.startActivity(intent);
                }
            }
        });

        if (deleteBtn != null) {
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    removeFollowingItem(Integer.toString(item.getId()));
                }
            });
        }
        return view;
    }

    public Item getItem(int position) {
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
                ArrayList<Item> nPlanetList = new ArrayList<Item>();

                for (Item p : nitems) {
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
            nitems = (ArrayList<Item>) results.values;
            notifyDataSetChanged();
        }
    }

    public void removeFollowingItem(String itemId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("item_id", itemId));
        params.add(new BasicNameValuePair("user_id", user_id));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "removefollowingitem", params);

        if (json != null) {
            try {
                if (json.getString("status").equals("success")) {
                    ((Activity) context).finish();
                    Intent intent = ((Activity) context).getIntent();
                    context.startActivity(intent);
                    CharSequence text = "Item removed from following list.";
                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}