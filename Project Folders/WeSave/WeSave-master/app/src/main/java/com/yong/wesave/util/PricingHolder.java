package com.yong.wesave.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yong.wesave.R;

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

public class PricingHolder {
    public LinearLayout entry;
    public TextView pricetxt, promotxt, datetxt, user, name, name2, info, timestamp, distance;
    public ImageView image;

    public PricingHolder(View pricingView) {
        pricetxt = (TextView) pricingView.findViewById(R.id.tv_price);
        promotxt = (TextView) pricingView.findViewById(R.id.tv_promotion);
        datetxt = (TextView) pricingView.findViewById(R.id.tv_promotion_date);
        user = (TextView) pricingView.findViewById(R.id.tv_user_name);
        name = (TextView) pricingView.findViewById(R.id.name);
        info = (TextView) pricingView.findViewById(R.id.tv_item_info);
        name2 = (TextView) pricingView.findViewById(R.id.storeName);
        image = (ImageView) pricingView.findViewById(R.id.image);
        timestamp = (TextView) pricingView.findViewById(R.id.tv_duration);
        distance = (TextView) pricingView.findViewById(R.id.tv_distance);
        entry = (LinearLayout) pricingView.findViewById(R.id.pricingEntry);
    }
}
