package com.yong.wesave.adapter;

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.yong.wesave.AddSubcategoryActivity;
import com.yong.wesave.AddThirdCategoryActivity;
import com.yong.wesave.CategorizedItemsActivity;
import com.yong.wesave.R;
import com.yong.wesave.SubcategoryActivity;
import com.yong.wesave.ThirdCategoryActivity;
import com.yong.wesave.apiobject.Category;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class CategoryAdapter extends ArrayAdapter<Category> {

    AQuery androidAQuery;
    int resource;
    String response, caller;
    Context context;
    ArrayList<Category> categories;
    int cat_id, subcat_id, third_cat_id;
    String catName, subcatName, thirdCatName;


    //Initialize adapter
    public CategoryAdapter(Context context, int resource, ArrayList<Category> categories, String caller) {
        super(context, resource, categories);
        this.resource = resource;
        this.categories = categories;
        this.context = context;
        this.androidAQuery = new AQuery(context);
        this.caller = caller;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout view;
        //Get the current object
        final Category category = categories.get(position);

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

        TextView categoryName = (TextView) view.findViewById(R.id.name);
        ImageView catImg = (ImageView) view.findViewById(R.id.image);
        LinearLayout entry = (LinearLayout) view.findViewById(R.id.entry);

        String catName = category.getCategoryName();
        categoryName.setText(catName);

        String uri = "@drawable/";
        if (category.getLevel() == 1) {
            uri = uri + "cat_";
        } else if (category.getLevel() == 2) {
            uri = uri + "subcat_";
        } else if (category.getLevel() == 3) {
            uri = uri + "thirdcat_";
        }

        uri = uri + category.getId();

        int imageResource = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());
        if (imageResource != 0) {
            Drawable res = getContext().getResources().getDrawable(imageResource);
            catImg.setImageDrawable(res);
        } else {
            imageResource = getContext().getResources().getIdentifier("@drawable/default_item_image", null, getContext().getPackageName());
            Drawable res = getContext().getResources().getDrawable(imageResource);
            catImg.setImageDrawable(res);
        }

        entry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (caller.equals("ViewCategory")) {
                    if (category.getLevel() == 1) {
                        Intent intent = new Intent(context, SubcategoryActivity.class);
                        intent.putExtra("cat_id", category.getId());
                        intent.putExtra("cat_name", category.getCategoryName());
                        context.startActivity(intent);

                    } else if (category.getLevel() == 2) {
                        Intent intent = new Intent(context, ThirdCategoryActivity.class);
                        intent.putExtra("subcat_id", category.getId());
                        intent.putExtra("subcat_name", category.getCategoryName());
                        context.startActivity(intent);

                    } else if (category.getLevel() == 3) {
                        Intent intent = new Intent(context, CategorizedItemsActivity.class);
                        intent.putExtra("third_cat_id", category.getId());
                        intent.putExtra("third_cat_name", category.getCategoryName());
                        context.startActivity(intent);
                    }
                } else if (caller.equals("AddCategory")) {
                    if (category.getLevel() == 1) {
                        Intent intent = new Intent(context, AddSubcategoryActivity.class);
                        cat_id = category.getId();
                        intent.putExtra("cat_id", cat_id);
                        intent.putExtra("cat_name", category.getCategoryName());
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        context.startActivity(intent);
                        ((Activity) context).finish();

                    } else if (category.getLevel() == 2) {
                        Intent intent = new Intent(context, AddThirdCategoryActivity.class);
                        subcat_id = category.getId();
                        intent.putExtra("subcat_id", subcat_id);
                        intent.putExtra("subcat_name", category.getCategoryName());
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        context.startActivity(intent);
                        ((Activity) context).finish();

                    } else if (category.getLevel() == 3) {
                        Intent data = new Intent();
                        third_cat_id = category.getId();
                        data.putExtra("third_cat_id", third_cat_id);
                        ((Activity) context).setResult(RESULT_OK, data);
                        ((Activity) context).finish();
                    }
                }
            }
        });

        return view;
    }
}