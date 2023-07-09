package com.yong.wesave;
/**
 * Created by Yong on 8/1/2017.
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */
/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yong.wesave.adapter.CommentAdapter;
import com.yong.wesave.adapter.PricingAdapter;
import com.yong.wesave.apiobject.Comment;
import com.yong.wesave.apiobject.Pricing;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.Keyboard;
import com.yong.wesave.util.ServerRequest;
import com.yong.wesave.util.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.yong.wesave.common.Constants.API_GET_COMMENTS;
import static com.yong.wesave.common.Constants.API_INSERT_COMMENT;

public class PricingActivity extends WeSaveActivity implements View.OnClickListener {
  PricingAdapter adapter;
  ListView priceListView;
  ListView commentListView;
  ImageView img;
  TextView name;
  TextView info;
  TextView like_count, view_count;
  Button likeBtn, followBtn, sortByDateBtn, sortByPricingBtn, sortByDistanceBtn, sortByVotesBtn;
  Button addPriceBtn, commentBtn, viewPriceBtn;
  LinearLayout priceSelection;
  //for comment
  SessionManager sesion;
  String username;
  LinearLayout writeCommentLayout;
  EditText writeComment;
  Button commentSubmitBtn;
  ArrayList<Comment> commentArrayList;
  //other stuff
  JSONObject json_data;
  Pricing item;
  Gson gson = new Gson();
  int item_id, creator_id;
  PendingIntent resultPendingIntent;
  Button shareBTN;
  CallbackManager callbackManager;
  ShareDialog shareDialog;
  String url;
  ArrayList<Comment> commentsList;
  JSONObject data;
  Target target = new Target() {
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
      SharePhoto sharePhoto = new SharePhoto.Builder()
              .setBitmap(bitmap)
              .build();
      if (ShareDialog.canShow(SharePhotoContent.class)) {
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(sharePhoto)
                .build();
        shareDialog.show(content);
        
      }
    }
    
    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
    }
    
    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }
  };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FacebookSdk.sdkInitialize(this.getApplicationContext());
    super.replaceContentLayout(R.layout.activity_pricing, R.id.content_frame);
    super.replaceTitle(R.string.title_pricing);
    //for comments views
    sesion = new SessionManager(getApplicationContext());
    HashMap<String, String> user = session.getUserDetails();
    username = user.get(SessionManager.KEY_NAME);
    writeCommentLayout = findViewById(R.id.writeCommentLayout);
    writeComment = findViewById(R.id.writeComment);
    commentSubmitBtn = findViewById(R.id.commentSubmitBtn);
    priceListView = (ListView) findViewById(R.id.pricingList);
    commentListView = findViewById(R.id.commentSection);
    priceSelection = findViewById(R.id.priceSelectionView);
    img = (ImageView) findViewById(R.id.img);
    name = (TextView) findViewById(R.id.tv_item_name);
    info = (TextView) findViewById(R.id.tv_item_info);
    like_count = (TextView) findViewById(R.id.tv_like_count);
    view_count = (TextView) findViewById(R.id.tv_view_count);
    commentBtn = findViewById(R.id.comment);
    viewPriceBtn = findViewById(R.id.viewPrice);
    addPriceBtn = findViewById(R.id.addPriceButton);
    likeBtn = (Button) findViewById(R.id.likeButton);
    followBtn = (Button) findViewById(R.id.subscibeButton);
    sortByDateBtn = (Button) findViewById(R.id.sortByDateBtn);
    sortByPricingBtn = (Button) findViewById(R.id.sortByPricingBtn);
    sortByDistanceBtn = (Button) findViewById(R.id.sortByDistanceBtn);
    sortByVotesBtn = (Button) findViewById(R.id.sortByVotesBtn);
    item_id = getIntent().getIntExtra("item_id", 0);
    setItemDetails(Integer.toString(item_id));
    getPricing(Integer.toString(item_id), 1);
    //init view
    shareBTN = (Button) findViewById(R.id.shareBTN);
    //init FB
    callbackManager = CallbackManager.Factory.create();
    shareDialog = new ShareDialog(this);
    shareBTN.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //create callback
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
          @Override
          public void onSuccess(Sharer.Result result) {
            Toast.makeText(PricingActivity.this, "Share Successful", Toast.LENGTH_SHORT).show();
          }
          
          @Override
          public void onCancel() {
            Toast.makeText(PricingActivity.this, "Share cancel", Toast.LENGTH_SHORT).show();
          }
          
          @Override
          public void onError(FacebookException error) {
            Toast.makeText(PricingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
        //fetch photo from link and convert to bitmap
        Picasso.with(getBaseContext())
                .load(Constants.API_BASE_URL_ITEM_IMAGES + url)
                .into(target);
      }
    });
    greenButton(sortByDateBtn);
//    if (checkFollow(item_id)) {
//      followBtn.setText(R.string.Unfollow);
//      followBtn.setBackgroundResource(R.drawable.btn_back_grey);
//    } else {
//      followBtn.setText(R.string.Follow);
//      followBtn.setBackgroundResource(R.drawable.btn_back_green);
//    }
//    if (checkLike(item_id)) {
//      likeBtn.setText(R.string.Unlike);
//      likeBtn.setBackgroundResource(R.drawable.btn_back_grey);
//    } else {
//      likeBtn.setText(R.string.Like);
//      likeBtn.setBackgroundResource(R.drawable.btn_back_blue);
//    }
    addPriceBtn.setOnClickListener(this);
    commentBtn.setOnClickListener(this);
    viewPriceBtn.setOnClickListener(this);
    likeBtn.setOnClickListener(this);
    followBtn.setOnClickListener(this);
    sortByDateBtn.setOnClickListener(this);
    sortByPricingBtn.setOnClickListener(this);
    sortByDistanceBtn.setOnClickListener(this);
    sortByVotesBtn.setOnClickListener(this);
    commentSubmitBtn.setOnClickListener(this);
    Intent resultIntent = new Intent(getApplicationContext(), Pricing.class);
    resultIntent.putExtra("item_id", item_id);
    resultPendingIntent =
            PendingIntent.getActivity(
                    this,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
    printKeyHash();
  }
  
  private void printKeyHash() {
    try {
      PackageInfo info = getPackageManager().getPackageInfo("com.yong.wesave",
              PackageManager.GET_SIGNATURES);
      for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
        
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      Intent intent = new Intent(PricingActivity.this, PricingActivity.class);
      intent.putExtra("item_id", item_id);
      startActivity(intent);
      finish();
    }
  }
  
  public void getPricing(String item_id, int choice) {
    String method;
    switch (choice) {
      case 1:
        method = "sortByDate";
        break;
      case 2:
        method = "sortByPricing";
        break;
      case 3:
        method = "sortByDistance";
        break;
      case 4:
        method = "sortByVotes";
        break;
      case 5:
        method = "getcontributedpricings";
        break;
      default:
        method = "sortByDate";
        break;
    }
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("item_id", item_id));
    ServerRequest sr = new ServerRequest();
    JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + method, params);
    JSONArray json_item = null, json_item_expired = null;
    ArrayList<Pricing> items = new ArrayList<Pricing>();
    if (json != null) {
      try {
        if (json.getString("status").equals("success")) {
          json_item = json.getJSONArray("data");
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    for (int i = 0; i < json_item.length(); i++) {
      try {
        json_data = json_item.getJSONObject(i);
        JsonParser parser = new JsonParser();
        JsonElement mJson = parser.parse(json_data.toString());
        item = gson.fromJson(mJson, Pricing.class);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      items.add(item);
    }
    if (choice != 3) {
      JSONObject jsonExpired = sr.getJSON(Constants.API_BASE_URL_API + method + "Expired", params);
      if (jsonExpired != null) {
        try {
          if (json.getString("status").equals("success")) {
            json_item_expired = jsonExpired.getJSONArray("data");
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
      for (int i = 0; i < json_item_expired.length(); i++) {
        try {
          json_data = json_item_expired.getJSONObject(i);
          JsonParser parser = new JsonParser();
          JsonElement mJson = parser.parse(json_data.toString());
          item = gson.fromJson(mJson, Pricing.class);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        items.add(item);
      }
    }
    priceListView.destroyDrawingCache();
    priceListView.setVisibility(ListView.INVISIBLE);
    priceListView.setVisibility(ListView.VISIBLE);
    adapter = new PricingAdapter(this, R.layout.list_pricing_promotion, items, super.user_id,
            choice);
    priceListView.setAdapter(adapter);
  }
  
  public void setItemDetails(String item_id) {
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("item_id", item_id));
    params.add(new BasicNameValuePair("user_id", super.user_id));
    ServerRequest sr = new ServerRequest();
    JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getitem", params);
    //set item details
    if (json != null) {
      try {
        if (json.getString("status").equals("success")) {
          int userId = Integer.parseInt(super.user_id);
          data = json.getJSONObject("data");
          name.setText(data.getString("name"));
          info.setText('(' + data.getString("info") + ')');
          creator_id = data.getInt("creator_id");
          url = data.getString("image");
          
          
          int liker_id = data.optInt("liker_id", -1);
          int likeCount = data.optInt("like_count",0);
          like_count.setText(Integer.toString(likeCount));
          int follower_id = data.optInt("follower_id",-1);
          int followerCount = data.optInt("follower_count",0);
          int viewCount = data.optInt("view_count",0);
          view_count.setText(Integer.toString(viewCount));
          if (follower_id != -1) {
            followBtn.setText(R.string.Unfollow);
            followBtn.setBackgroundResource(R.drawable.btn_back_grey);
          } else {
            followBtn.setText(R.string.Follow);
            followBtn.setBackgroundResource(R.drawable.btn_back_green);
          }
          if (liker_id != -1) {
            likeBtn.setText(R.string.Unlike);
            likeBtn.setBackgroundResource(R.drawable.btn_back_grey);
          } else {
            likeBtn.setText(R.string.Like);
            likeBtn.setBackgroundResource(R.drawable.btn_back_blue);
          }
          //set item image
          AQuery androidAQuery = new AQuery(this);
          androidAQuery.id(img).image(Constants.API_BASE_URL_ITEM_IMAGES + url,
                  true, true, img.getWidth(), R.drawable.default_item_image);
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void getComments() {
    commentsList = new ArrayList<>();
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("item_id", Integer.toString(item_id)));
    ServerRequest sr = new ServerRequest();
    JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + API_GET_COMMENTS, params);
    if (json != null) {
      try {
        if (json.getString("status").equals("success")) {
          JSONArray jsonArr = json.getJSONArray("data");
          for (int i = 0; i < jsonArr.length(); i++) {
            try {
              json_data = jsonArr.getJSONObject(i);
              JsonParser parser = new JsonParser();
              JsonElement mJson = parser.parse(json_data.toString());
              Comment comment = gson.fromJson(mJson, Comment.class);
              commentsList.add(comment);
              commentListView.setAdapter(null);
              CommentAdapter commentAdapter = new CommentAdapter(this, R.layout.list_comment,
                      commentsList);
              commentListView.setAdapter(commentAdapter);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void insertComment() {
    String comment = writeComment.getText().toString();
    if (comment.replace(" ", "").length() > 0) {
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("item_id", Integer.toString(item_id)));
      params.add(new BasicNameValuePair("username", username));
      params.add(new BasicNameValuePair("comment", comment));
      ServerRequest sr = new ServerRequest();
      JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + API_INSERT_COMMENT, params);
      String status;
      if (json != null) {
        try {
          status = json.getString("status");
          if (status.equals("success")) {
            writeComment.getText().clear();
            Toast.makeText(getApplicationContext(), "Comment Succesfully Submitted",
                    Toast.LENGTH_SHORT).show();
            writeComment.clearFocus();
            Keyboard.hideKeyboard(PricingActivity.this);
            getComments();
          }
        } catch (Exception e) {
          Toast.makeText(getApplicationContext(), "comment submission failed",
                  Toast.LENGTH_SHORT).show();
          e.printStackTrace();
        }
      }
    } else {
      Toast.makeText(getApplicationContext(), "comment cannot be empty", Toast.LENGTH_SHORT).show();
    }
  }
  
  @Override
  public void onClick(View v) {
    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;
    Toast toast;
    if (v == commentBtn) {
      if (commentsList == null) {
        getComments();
      }
      priceSelection.setVisibility(View.GONE);
      priceListView.setVisibility(View.GONE);
      commentListView.setVisibility(View.VISIBLE);
      writeCommentLayout.setVisibility(View.VISIBLE);
      
    }
    if (v == viewPriceBtn) {
      priceSelection.setVisibility(View.VISIBLE);
      priceListView.setVisibility(View.VISIBLE);
      commentListView.setVisibility(View.GONE);
      writeCommentLayout.setVisibility(View.GONE);
    }
    if (v == commentSubmitBtn) {
      insertComment();
    }
    if (v == addPriceBtn) {
      Intent intent = new Intent(PricingActivity.this, AddPricingActivity.class);
      intent.putExtra("item_id", item_id);
      startActivityForResult(intent, 0);
      
    } else if (v == likeBtn) {
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("item_id", Integer.toString(item_id)));
      params.add(new BasicNameValuePair("user_id", super.user_id));
      ServerRequest sr = new ServerRequest();
      JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "like", params);
      if (json != null) {
        try {
          if (json.getString("status").equals("success")) {
            setLikeAndViewCount(Integer.toString(item_id));
            //send notification to item creator
            int mNotificationId = creator_id;
            super.mBuilder.setContentTitle("WeSave")
                    .setContentText(super.name + " liked your item!")
                    .setContentIntent(resultPendingIntent);
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, super.mBuilder.build());
            //store notification
            storenotification(params, "like", creator_id);
            likeBtn.setText(R.string.Unlike);
            likeBtn.setBackgroundResource(R.drawable.btn_back_grey);
          } else {
            if (removeLikeItem(Integer.toString(item_id))) {
              likeBtn.setText(R.string.Like);
              likeBtn.setBackgroundResource(R.drawable.btn_back_blue);
              setLikeAndViewCount(Integer.toString(item_id));
              toast = Toast.makeText(context, "Item unliked.", duration);
              toast.show();
            }
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    } else if (v == followBtn) {
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("item_id", Integer.toString(item_id)));
      params.add(new BasicNameValuePair("user_id", super.user_id));
      ServerRequest sr = new ServerRequest();
      JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "follow", params);
      if (json != null) {
        try {
          if (json.getString("status").equals("success")) {
            toast = Toast.makeText(context, "Item added into your following list.", duration);
            toast.show();
            //send notification to item creator
            int mNotificationId = creator_id;
            super.mBuilder.setContentTitle("WeSave")
                    .setContentText(super.name + " followed your item!")
                    .setContentIntent(resultPendingIntent);
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, super.mBuilder.build());
            //store notification
            storenotification(params, "follow", creator_id);
            followBtn.setText(R.string.Unfollow);
            followBtn.setBackgroundResource(R.drawable.btn_back_grey);
          } else {
            if (removeFollowingItem(Integer.toString(item_id))) {
              followBtn.setText(R.string.Follow);
              followBtn.setBackgroundResource(R.drawable.btn_back_green);
              toast = Toast.makeText(context, "Item unfollowed.", duration);
              toast.show();
            }
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    } else if (v == sortByDateBtn) {
      getPricing(Integer.toString(item_id), 1);
      toast = Toast.makeText(context, "Sort by date", duration);
      toast.show();
      greenButton(sortByDateBtn);
      transparentButton(sortByPricingBtn);
      transparentButton(sortByDistanceBtn);
      transparentButton(sortByVotesBtn);
      
    } else if (v == sortByPricingBtn) {
      getPricing(Integer.toString(item_id), 2);
      toast = Toast.makeText(context, "Sort by pricing", duration);
      toast.show();
      greenButton(sortByPricingBtn);
      transparentButton(sortByDistanceBtn);
      transparentButton(sortByVotesBtn);
      transparentButton(sortByDateBtn);
      
    } else if (v == sortByDistanceBtn) {
      getPricing(Integer.toString(item_id), 3);
      toast = Toast.makeText(context, "Sort by distance", duration);
      toast.show();
      greenButton(sortByDistanceBtn);
      transparentButton(sortByPricingBtn);
      transparentButton(sortByVotesBtn);
      transparentButton(sortByDateBtn);
      
    } else if (v == sortByVotesBtn) {
      getPricing(Integer.toString(item_id), 4);
      toast = Toast.makeText(context, "Sort by votes", duration);
      toast.show();
      greenButton(sortByVotesBtn);
      transparentButton(sortByDistanceBtn);
      transparentButton(sortByPricingBtn);
      transparentButton(sortByDateBtn);
    }
  }
  
  public void setLikeAndViewCount(String item_id) {
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("item_id", item_id));
    ServerRequest sr = new ServerRequest();
    JSONObject json_like = sr.getJSON(Constants.API_BASE_URL_API + "getlikecount", params);
    JSONObject json_view = sr.getJSON(Constants.API_BASE_URL_API + "getviewcount", params);
    if (json_like != null) {
      try {
        if (json_like.getString("status").equals("success")) {
          like_count.setText(Integer.toString(json_like.getInt("data")));
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    if (json_view != null) {
      try {
        if (json_view.getString("status").equals("success")) {
          view_count.setText(Integer.toString(json_view.getInt("data")));
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }
//
//  public void addView(String item_id) {
//    List<NameValuePair> params = new ArrayList<NameValuePair>();
//    params.add(new BasicNameValuePair("item_id", item_id));
//    params.add(new BasicNameValuePair("user_id", super.user_id));
//    ServerRequest sr = new ServerRequest();
//    sr.getJSON(Constants.API_BASE_URL_API + "view", params);
//  }
//  protected boolean checkFollow(int item_id) {
//    List<NameValuePair> params = new ArrayList<NameValuePair>();
//    params.add(new BasicNameValuePair("item_id", Integer.toString(item_id)));
//    params.add(new BasicNameValuePair("user_id", super.user_id));
//    ServerRequest sr = new ServerRequest();
//    JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "checkfollow", params);
//    if (json != null) {
//      try {
//        System.out.println(json.getJSONObject("data") != null);
//        if (json.getString("status").equals("success") && json.getJSONObject("data") != null) {
//          return true;
//        }
//      } catch (JSONException e) {
//        e.printStackTrace();
//      }
//    }
//    return false;
//  }
//
//  protected boolean checkLike(int item_id) {
//    List<NameValuePair> params = new ArrayList<NameValuePair>();
//    params.add(new BasicNameValuePair("item_id", Integer.toString(item_id)));
//    params.add(new BasicNameValuePair("user_id", super.user_id));
//    ServerRequest sr = new ServerRequest();
//    JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "checklike", params);
//    if (json != null) {
//      try {
//        if (json.getString("status").equals("success") && json.getJSONObject("data") != null) {
//          return true;
//        }
//      } catch (JSONException e) {
//        e.printStackTrace();
//      }
//    }
//    return false;
//  }
  
  public boolean removeFollowingItem(String itemId) {
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("item_id", itemId));
    params.add(new BasicNameValuePair("user_id", super.user_id));
    ServerRequest sr = new ServerRequest();
    JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "removefollowingitem", params);
    if (json != null) {
      try {
        if (json.getString("status").equals("success")) {
          return true;
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return false;
  }
  
  public boolean removeLikeItem(String itemId) {
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("item_id", itemId));
    params.add(new BasicNameValuePair("user_id", super.user_id));
    ServerRequest sr = new ServerRequest();
    JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "removelikeitem", params);
    if (json != null) {
      try {
        if (json.getString("status").equals("success")) {
          return true;
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return false;
  }
  
  public void greenButton(Button btn) {
    //GradientDrawable drawable = new GradientDrawable();
    //drawable.setStroke(5, Color.parseColor("#008000"));
    //drawable.setCornerRadius( 15 );
    //btn.setBackgroundDrawable(drawable);
    btn.setTextColor(Color.parseColor("#008000"));
    btn.setTypeface(btn.getTypeface(), Typeface.BOLD);
  }
  
  public void transparentButton(Button btn) {
    //GradientDrawable drawable = new GradientDrawable();
    //drawable.setStroke(5, Color.BLACK);
    //drawable.setCornerRadius( 15 );
    //btn.setBackgroundDrawable(drawable);
    btn.setTextColor(Color.BLACK);
    btn.setTypeface(btn.getTypeface(), Typeface.NORMAL);
  }
  
  public void storenotification(List<NameValuePair> params, String type, Integer recipient_id) {
    params.add(new BasicNameValuePair("type", type));
    params.add(new BasicNameValuePair("recipient_id", Integer.toString(recipient_id)));
    ServerRequest sr = new ServerRequest();
    JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "storenotification", params);
    if (json != null) {
      try {
        if (json.getString("status").equals("success")) {
          //finish();
        } else {
          System.out.println("Error inserting new notification entry.");
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }
}

