package com.yong.wesave.adapter;

/**
 * Created by Yong on 7/2/2017.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.yong.wesave.R;
import com.yong.wesave.apiobject.Item;
import com.yong.wesave.common.Constants;

import java.util.HashMap;
import java.util.List;

public class ExpandableListResAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<Item>> _listDataChild;
    AQuery androidAQuery;


    public ExpandableListResAdapter(Context context, List<String> listDataHeader,
                                    HashMap<String, List<Item>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.androidAQuery = new AQuery(context);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {

        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_plan_result_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.name);
        TextView txtQtyAndPrice = (TextView) convertView.findViewById(R.id.info);
        TextView totalPrice = (TextView) convertView.findViewById(R.id.totalPrice);
        TextView psText = (TextView) convertView.findViewById(R.id.ps_text);
        ImageView img = (ImageView) convertView.findViewById(R.id.image);

        final Item childItem = (Item) getChild(groupPosition, childPosition);
        txtListChild.setText(childItem.name);
        txtQtyAndPrice.setText(Integer.toString(childItem.getQty()) + " x S$ " + childItem.price);
        Float total = childItem.getQty() * Float.parseFloat(childItem.price);
        totalPrice.setText("S$ " + String.format("%.2f", total));
        androidAQuery.id(img).image(Constants.API_BASE_URL_ITEM_IMAGES + childItem.image,
                true, true, img.getWidth(), R.drawable.default_item_image);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupTypeCount() {
        return 1;
    }

    @Override
    public int getChildTypeCount() {
        return 1;
    }


}