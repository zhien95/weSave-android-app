package com.yong.wesave;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Preferences extends WeSaveActivity {
    Spinner preferredDistanceSpinner;
    Button savePreferencebtn;
    EditText newUsername;
    SharedPreferences sharedPreferences;
    public static final String MY_PREFERENCE = "0";
    int selection=0;

    String FUNCTION_1="preferredDistance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_preferences, R.id.content_frame);
        super.replaceTitle(R.string.title_preferences);


        preferredDistanceSpinner = (Spinner) findViewById(R.id.preferredDistanceSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this, R.array.distanceOptions, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        preferredDistanceSpinner.setAdapter(filterAdapter);

        savePreferencebtn = (Button) findViewById(R.id.savePreferencebtn);
        sharedPreferences = getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);

        String selection1=sharedPreferences.getString(FUNCTION_1, "0");
        preferredDistanceSpinner.setSelection(Integer.parseInt(selection1));



        savePreferencebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(FUNCTION_1, String.valueOf(preferredDistanceSpinner.getSelectedItemPosition()));
                editor.commit();
                Preferences.this.finish();
            }
        });

    }

}