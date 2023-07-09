package com.yong.wesave;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScanBarcodeActivity extends WeSaveActivity {

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {

            lastText = result.getText();
            barcodeView.setStatusText(result.getText());
            beepManager.playBeepSoundAndVibrate();

            int item_id = barcodeFound(lastText);
            if (item_id != -1) {
                Intent intent = new Intent(ScanBarcodeActivity.this, PricingActivity.class);
                intent.putExtra("item_id", item_id);
                startActivityForResult(intent, 0);
            } else {
                Intent intent = new Intent(ScanBarcodeActivity.this, NoMatchFound.class);
                intent.putExtra("barcode", lastText);
                startActivityForResult(intent, 0);
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_scan_barcode, R.id.content_frame);
        super.replaceTitle(R.string.title_scan_barcode);


        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeSingle(callback);
        beepManager = new BeepManager(this);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(true);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:
                Intent intent = new Intent(ScanBarcodeActivity.this, SearchActivity.class);
                startActivityForResult(intent, 0);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            startActivity(new Intent(ScanBarcodeActivity.this, ScanBarcodeActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public int barcodeFound(String lastText) {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("barcode", lastText));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "findbarcode", params);
        int item_id = -1;

        if (json != null) {
            try {
                if (json.getJSONObject("data") != null) {
                    item_id = json.getJSONObject("data").getInt("item_id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return item_id;
    }


}
