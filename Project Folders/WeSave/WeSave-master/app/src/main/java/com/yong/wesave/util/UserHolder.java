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

public class UserHolder {
    public LinearLayout entry;
    public TextView name, info;
    public ImageView image;

    public UserHolder(View pricingView) {
        name = (TextView) pricingView.findViewById(R.id.name);
        info = (TextView) pricingView.findViewById(R.id.tv_item_info);
        image = (ImageView) pricingView.findViewById(R.id.image);
        entry = (LinearLayout) pricingView.findViewById(R.id.pricingEntry);
    }
}
