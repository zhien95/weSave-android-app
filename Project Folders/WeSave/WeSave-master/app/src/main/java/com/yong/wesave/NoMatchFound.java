package com.yong.wesave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Yong on 21/1/2017.
 */

public class NoMatchFound extends WeSaveActivity implements View.OnClickListener {

    private Button create;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_no_match, R.id.content_frame);
        super.replaceTitle(R.string.title_no_match);
        create = (Button) findViewById(R.id.createbtn);

        create.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == create) {
            Intent intent = new Intent(this, CreateItemActivity.class);
            intent.putExtra("barcode", getIntent().getStringExtra("barcode"));
            startActivity(intent);
            finish();
        }
    }


}

