package com.yong.wesave.adapter;

/**
 * Created by Yong on 19/1/2017.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.yong.wesave.apiobject.User;
import com.yong.wesave.util.UserHolder;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {

    //AQuery androidAQuery;
    int resource;
    Context context;
    ArrayList<User> users;
    //String user_id;

    //Initialize adapter
    public UserAdapter(Context context, int resource, ArrayList<User> users) {
        super(context, resource, users);
        this.resource = resource;
        this.users = users;
        this.context = context;
        //this.androidAQuery = new AQuery(context);
        //this.user_id = user_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        LinearLayout userView;
        UserHolder userHolder;
        String username;
        //Get the current object
        //final User user = users.get(position);


        //Inflate the view
        if (convertView == null) {
            userView = new LinearLayout(getContext());
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(resource, userView, true);
            userHolder = new UserHolder(view);
            view.setTag(userHolder);

        } else {
            userHolder = (UserHolder) view.getTag();
            userHolder.name.setText("");
        }

        //TextView username = (TextView) view.findViewById(R.id.name);
        //TextView itemInfo = (TextView) view.findViewById(R.id.info);
        //ImageView itemImg = (ImageView) view.findViewById(R.id.image);
        //LinearLayout entry = (LinearLayout) view.findViewById(R.id.entry);

        //Set Item details
        //androidAQuery.id(itemImg).image(Constants.API_BASE_URL_ITEM_IMAGES + item.getImage(),
        //       true, true, itemImg.getWidth(), R.drawable.default_item_image);
        //itemImg.setImageBitmap(decodeSampledBitmapFromResource(getContext().getResources(), R.drawable.default_item_image, 100, 100));
        int imageResource = getContext().getResources().getIdentifier("@drawable/default_avatar", null, getContext().getPackageName());
        Drawable res = getContext().getResources().getDrawable(imageResource);
        userHolder.image.setImageDrawable(res);

        username = users.get(position).getUsername();
        userHolder.name.setText(username);
        //itemInfo.setText("(" + user.getInfo() + ")");

        /*entry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!getItemDetails){
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("item_id", Integer.toString(item.id));
                    ((Activity)context).setResult(Activity.RESULT_OK, resultIntent);
                    ((Activity)context).finish();
                }else {
                    Intent intent = new Intent(context, PricingActivity.class);
                    intent.putExtra("item_id", item.getId());
                    context.startActivity(intent);
                }
            }
        });*/

        return view;
    }
}