package com.yong.wesave.adapter;

/**
 * Created by Yong on 7/2/2017.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidquery.AQuery;
import com.yong.wesave.AddLocation;
import com.yong.wesave.R;
import com.yong.wesave.SearchItemActivity;
import com.yong.wesave.apiobject.Item;
import com.yong.wesave.apiobject.Store;
import com.yong.wesave.asyntask.AsyncImageLoader;
import com.yong.wesave.common.Constants;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<Store>> _listDataChild;
    private HashMap<String, List<Item>> _listItemChild;
    AQuery androidAQuery;

    private static final int GROUP_ITEM = 0;
    private static final int GROUP_STORE = 1;
    private static final int CHILD_ITEM = 0;
    private static final int CHILD_STORE = 1;
    private static final int CHILD_ADD_ITEM = 2;
    private static final int CHILD_ADD_STORE = 3;
    private static final int RESULT_ADD_ITEM = 1;
    private static final int RESULT_ADD_STORE = 2;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Item>> listChildItem,
                                 HashMap<String, List<Store>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._listItemChild = listChildItem;
        this.androidAQuery = new AQuery(context);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {

        int groupType = getGroupType(groupPosition);
        switch (groupType) {
            case GROUP_ITEM:
                return this._listItemChild.get(this._listDataHeader.get(groupPosition))
                        .get(childPosititon);
            case GROUP_STORE:
                return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                        .get(childPosititon);
            default:
                return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                        .get(childPosititon);
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        int childType = getChildType(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_shopping_plan_item, null);
        }


        LinearLayout entry = (LinearLayout) convertView.findViewById(R.id.entry);
        TextView txtListChild = (TextView) convertView.findViewById(R.id.name);
        TextView txtQty = (TextView) convertView.findViewById(R.id.qty);
        ImageView img = (ImageView) convertView.findViewById(R.id.image);
        ToggleButton addButton = (ToggleButton) convertView.findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        switch (childType) {
            case CHILD_ITEM:
                final Item childItem = (Item) getChild(groupPosition, childPosition);
                txtListChild.setText(childItem.name);
                txtQty.setText("x" + Integer.toString(childItem.getQty()));
                androidAQuery.id(img).image(Constants.API_BASE_URL_ITEM_IMAGES + childItem.image,
                        true, true, img.getWidth(), R.drawable.default_item_image);
                addButton.setChecked(true);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _listItemChild.get("Items").remove(childPosition);
                        notifyDataSetChanged();
                    }
                });
                final Dialog d = new Dialog(_context);
                d.setTitle("NumberPicker");
                d.setContentView(R.layout.dialog_quantity);
                Button b1 = (Button) d.findViewById(R.id.button1);
                Button b2 = (Button) d.findViewById(R.id.button2);
                final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
                np.setMaxValue(100); // max value 100
                np.setMinValue(1);   // min value 0
                np.setWrapSelectorWheel(false);
                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                    }
                });
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        childItem.setQty(np.getValue());
                        d.dismiss();
                        notifyDataSetChanged();
                    }
                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss(); // dismiss the dialog
                    }
                });

                entry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.show();
                    }
                });

                break;
            case CHILD_STORE:
                Store childStore = (Store) getChild(groupPosition, childPosition);
                txtListChild.setText(childStore.store_name);

                //Set Store Image
                img.setImageResource(R.drawable.default_item_image);
                if ((childStore.image != null) && !(childStore.image.equals(""))) {
                    new AsyncImageLoader((Activity) _context, img, 0, childStore.image).execute("");
                }
                addButton.setChecked(true);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _listDataChild.get("Supermarkets").remove(childPosition);
                        notifyDataSetChanged();
                    }
                });
                break;
            case CHILD_ADD_ITEM:
                img.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
                txtListChild.setText("add item");
                entry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(_context, SearchItemActivity.class);
                        ((Activity) _context).startActivityForResult(intent, RESULT_ADD_ITEM);
                    }
                });
                break;
            case CHILD_ADD_STORE:
                img.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
                txtListChild.setText("add store");
                entry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(_context, AddLocation.class);
                        intent.putExtra("nearby", false);
                        ((Activity) _context).startActivityForResult(intent, RESULT_ADD_STORE);
                    }
                });
                break;

        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int groupType = getGroupType(groupPosition);
        switch (groupType) {
            case GROUP_ITEM:
                return this._listItemChild.get(this._listDataHeader.get(groupPosition))
                        .size();
            case GROUP_STORE:
                return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                        .size();
            default:
                return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                        .size();
        }

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
        return 2;
    }

    @Override
    public int getGroupType(int groupPosition) {
        switch (groupPosition) {
            case 0:
                return GROUP_ITEM;
            case 1:
                return GROUP_STORE;
            default:
                return GROUP_STORE;
        }
    }

    @Override
    public int getChildTypeCount() {
        return 4;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        switch (groupPosition) {
            case 0:
                if (childPosition == 0)
                    return CHILD_ADD_ITEM;
                else
                    return CHILD_ITEM;
            case 1:
                if (childPosition == 0)
                    return CHILD_ADD_STORE;
                else
                    return CHILD_STORE;
            default:
                return CHILD_STORE;
        }
    }

}