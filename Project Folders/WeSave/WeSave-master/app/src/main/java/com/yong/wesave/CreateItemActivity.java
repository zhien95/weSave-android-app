package com.yong.wesave;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.yong.wesave.common.Constants.API_BASE_URL_API;
import static com.yong.wesave.util.Validation.validateFields;

/**
 * Created by Yong on 12/1/2017.
 */

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

public class CreateItemActivity extends WeSaveActivity implements View.OnClickListener {

    private EditText itemName, itemInfo, itemCategory, itemSubcategory, itemThirdCategory;
    private int cat_id = 0, subcat_id = 0, third_cat_id = 0;
    private String category, subcategory = "Select subcategory", third_category = "Select further category";
    private TextView status;
    private Button submit, cancel;
    private ImageView image;
    String itemnametxt, iteminfotxt, itemimg;
    private Drawable d;
    int item_id;
    private byte[] imgbyte;
    Toast toast;

    int REQUEST_CAMERA = 0, ADD_CATEGORY = 1, ADD_SUBCATEGORY = 2, ADD_THIRDCATEGORY = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_create_product, R.id.content_frame);
        super.replaceTitle(R.string.title_create_item);

        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);
        itemName = (EditText) findViewById(R.id.itemNameEditText);
        itemInfo = (EditText) findViewById(R.id.itemWeightEditText);
        itemCategory = (EditText) findViewById(R.id.itemCategory);
        itemSubcategory = (EditText) findViewById(R.id.itemSubcategory);
        itemThirdCategory = (EditText) findViewById(R.id.itemThirdCategory);
        status = (TextView) findViewById(R.id.statusTextView);
        submit = (Button) findViewById(R.id.submitButton);
        cancel = (Button) findViewById(R.id.cancelbtn);
        image = (ImageView) findViewById(R.id.image);

        third_cat_id = getIntent().getIntExtra("third_cat_id", 0);
        if (category == null) {
            category = "Select category";
        }
        if (subcategory == null) {
            subcategory = "Select subcategory";
        }
        if (third_category == null) {
            third_category = "Select further category";
        }

        itemCategory.setText(category);
        itemSubcategory.setText(subcategory);
        itemThirdCategory.setText(third_category);

        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        image.setOnClickListener(this);
        itemCategory.setOnClickListener(this);
        itemSubcategory.setOnClickListener(this);
        itemThirdCategory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast toast;

        if (v == submit) {
            itemnametxt = itemName.getText().toString();
            iteminfotxt = itemInfo.getText().toString();
            if (itemimg == null) {
                status.setVisibility(View.VISIBLE);
                status.setText("Please submit an image.");
            } else if (!validateFields(itemnametxt) || !validateFields(iteminfotxt)) {
                status.setVisibility(View.VISIBLE);
                status.setText("Please fill in all required fields.");
            } else {
                createItem();
            }
        } else if (v == image) {
            Intent startCustomCameraIntent = new Intent(this, CameraActivity.class);
            startActivityForResult(startCustomCameraIntent, REQUEST_CAMERA);

        } else if (v == itemCategory) {
            Intent intent = new Intent(this, AddCategoryActivity.class);
            startActivityForResult(intent, ADD_CATEGORY);

        } else if (v == itemSubcategory) {
            if (cat_id == 0) {
                toast = Toast.makeText(this.getApplicationContext(), "Please select the main category.", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Intent intent = new Intent(this, AddSubcategoryActivity.class);
                intent.putExtra("cat_id", cat_id);
                //startActivity(intent);

                startActivityForResult(intent, ADD_CATEGORY);
            }

        } else if (v == itemThirdCategory) {
            if (cat_id == 0) {
                toast = Toast.makeText(this.getApplicationContext(), "Please select the main category.", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Intent intent = new Intent(this, AddThirdCategoryActivity.class);
                intent.putExtra("subcat_id", subcat_id);
                //startActivity(intent);
                startActivityForResult(intent, ADD_CATEGORY);
            }
        } else if (v == cancel) {
            Intent intent = new Intent(this, ScanBarcodeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null) {

            itemimg = data.getStringExtra("imagePath");

            Bitmap bitmap = BitmapFactory.decodeFile(itemimg);
            image.setImageBitmap(bitmap);
            d = Drawable.createFromPath(itemimg);

            if (bitmap.getWidth() > bitmap.getHeight())
                image.setRotation(90f);
            image.setBackgroundDrawable(null);
            System.out.println("itemimg " + itemimg);
      

        } else if (requestCode == ADD_CATEGORY && resultCode == RESULT_OK && data != null) {

            third_cat_id = data.getIntExtra("third_cat_id", 0);

            getCategoryName();
            itemCategory.setText(category);
            itemSubcategory.setText(subcategory);
            itemThirdCategory.setText(third_category);
            //getCategory(ca)
            //itemCategory.setText(category);
            //itemCategory.setText("cat_id");

        } /*else if (requestCode == ADD_SUBCATEGORY && resultCode == RESULT_OK && data != null) {
            subcat_id =  data.getStringExtra("subcat_id");
            //itemCategory.setText(category);
            itemSubcategory.setText(getIntent().getIntExtra("subcat_id", 0));

        } else if (requestCode == ADD_THIRDCATEGORY && resultCode == RESULT_OK && data != null) {
            third_cat_id =  data.getStringExtra("third_cat_id");
            //itemCategory.setText(category);
            itemThirdCategory.setText(getIntent().getIntExtra("third_cat_id", 0));
        }*/

    }

    private void createItem() {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", itemnametxt));
        params.add(new BasicNameValuePair("info", iteminfotxt));
        params.add(new BasicNameValuePair("creator_id", super.user_id));
        params.add(new BasicNameValuePair("third_cat_id", String.valueOf(third_cat_id)));

        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(API_BASE_URL_API + "createitem", params);

        if (json != null) {
            try {
                if (json.getString("status").equals("success")) {
                    item_id = json.getInt("data");
                    //Toast toast = Toast.makeText(this.getApplicationContext(), "Uploading...", Toast.LENGTH_SHORT);
                    //toast.show();
                    try {
                        uploadImage(itemimg);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (addBarcodeToDb(json.getString("data"))) {
                        Context context = getApplicationContext();
                        CharSequence text = "Item created. Please add pricing for the item.";
                        int duration = Toast.LENGTH_LONG;
                        toast = Toast.makeText(context, text, duration);
                        toast.show();
                        Intent intent = new Intent(CreateItemActivity.this, PricingActivity.class);
                        intent.putExtra("item_id", item_id);
                        startActivity(intent);
                        finish();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String path) throws ExecutionException, InterruptedException {
        File f = new File(path);
        Future uploading = Ion.with(CreateItemActivity.this)
                .load(API_BASE_URL_API + "uploaditemimg")
                .setMultipartParameter("item_id", Integer.toString(item_id))
                .setMultipartFile("image", f)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {

                    }
                });
        System.out.println(uploading.get());
    }

    public Boolean addBarcodeToDb(String item_id) {
        String barcode = getIntent().getStringExtra("barcode");
        ArrayList<NameValuePair> params_barcode = new ArrayList<NameValuePair>();
        params_barcode.add(new BasicNameValuePair("barcode", barcode));
        params_barcode.add(new BasicNameValuePair("item_id", item_id));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(API_BASE_URL_API + "addbarcode", params_barcode);

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

    public void getCategoryName() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("third_cat_id", String.valueOf(third_cat_id)));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "getcategoryname", params);

        if (json != null) {
            try {
                JSONObject json_obj = json.getJSONObject("data");
                if (json_obj != null) {
                    category = json_obj.getString("category");
                    subcategory = json_obj.getString("subcategory");
                    third_category = json_obj.getString("third_category");
                    cat_id = Integer.parseInt(json_obj.getString("cat_id"));
                    subcat_id = Integer.parseInt(json_obj.getString("subcat_id"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
