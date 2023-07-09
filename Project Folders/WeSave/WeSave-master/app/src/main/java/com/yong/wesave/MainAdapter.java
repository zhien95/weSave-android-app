package com.yong.wesave;

import android.content.Context;
import android.graphics.Paint;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.yong.wesave.apiobject.Item;
import com.yong.wesave.apiobject.Pricing;
import com.yong.wesave.common.Constants;

import android.content.Intent;

import java.util.ArrayList;

class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private ArrayList<Pricing> mDataset;
    AQuery androidAQuery;
    int resource;
    String response;
    Context context;
    ArrayList<Pricing> items;
    ArrayList<Pricing> nitems;
    Boolean getItemDetails;
    Boolean getContributedItems;
    String user_id;
    String option;
    int count=0;


    //Initialize adapter
    public MainAdapter(String option, Context context, int resource, ArrayList<Pricing> mDataset, Boolean getItemDetails, String user_id, Boolean getContributedItems) {
        //    super(context, resource, mDataset);
        this.option = option;
        this.resource = resource;
        this.mDataset = mDataset;
        this.nitems = mDataset;
        this.context = context;
        this.androidAQuery = new AQuery(context);
        this.getItemDetails = getItemDetails;
        this.getContributedItems = getContributedItems;
        this.user_id = user_id;
    }


    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_recommended, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MainAdapter.ViewHolder holder, final int position) {

            //top 5 recommended items
            holder.mTitle.setText(mDataset.get(position).getItemName());
            androidAQuery.id(holder.mImage).image(Constants.API_BASE_URL_ITEM_IMAGES + mDataset.get(position).getImage(),
                    true, true, holder.mImage.getWidth(), R.drawable.default_item_image);

            if (option.equals("recommend")) {
                holder.mPrice.setText("From $" + mDataset.get(position).getLowestPrice());
                //   holder.mSupermarket.setText(mDataset.get(position).store_name.toString());
            }
            if (option.equals("nearest")) {
                count++;
                if (mDataset.get(position).has_promo.equals("1")) {
                    holder.mPrice.setText("$" + mDataset.get(position).original_price);
                    holder.mPromoPrice.setText("$" + mDataset.get(position).promo_price);
                    holder.mPrice.setPaintFlags(holder.mPromoPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else if (mDataset.get(position).has_promo.equals("0")) {
                    holder.mPrice.setText("$" + mDataset.get(position).original_price);
                    holder.mPromoPrice.setVisibility(View.GONE);

                }
                holder.mSupermarket.setText(mDataset.get(position).store_name);
            }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent myIntent = new Intent(context, PricingActivity.class);
                myIntent.putExtra("item_id", mDataset.get(position).getItemId());
                context.startActivity(myIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public ImageView mImage;
        public TextView mPrice;
        public TextView mSupermarket;
        public TextView mPromoPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.name);
            mImage = (ImageView) itemView.findViewById(R.id.image);
            mPrice = (TextView) itemView.findViewById(R.id.price);
            mPromoPrice = (TextView) itemView.findViewById(R.id.promoPrice);
            mSupermarket = (TextView) itemView.findViewById(R.id.supermarket);

        }
    }
}
