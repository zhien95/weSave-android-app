package com.yong.wesave;

/**
 * Created by Yong on 8/1/2017.
 */

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.yong.wesave.util.Validation.validateFields;

public class ChangePasswordActivity extends WeSaveActivity implements View.OnClickListener {

    private EditText oldpass;
    private EditText newpass;
    private EditText cfmpass;
    private TextView status;
    private Button submit;
    String oldpasstxt, newpasstxt, cfmpasstxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.replaceContentLayout(R.layout.activity_change_password, R.id.content_frame);
        super.replaceTitle(R.string.title_change_password);
        oldpass = (EditText) findViewById(R.id.oldPasswordEditText);
        newpass = (EditText) findViewById(R.id.newPasswordEditText);
        cfmpass = (EditText) findViewById(R.id.cfmPasswordEditText);
        status = (TextView) findViewById(R.id.statusTextView);
        submit = (Button) findViewById(R.id.resbtn);


        submit.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == submit) {
            oldpasstxt = oldpass.getText().toString();
            newpasstxt = newpass.getText().toString();
            cfmpasstxt = cfmpass.getText().toString();
            if (!validateFields(oldpasstxt) || !validateFields(newpasstxt) || !validateFields(cfmpasstxt)) {
                status.setVisibility(View.VISIBLE);
                status.setText("Please fill in all required fields.");
            } else if (!passwordCorrect(oldpasstxt)) {
                status.setVisibility(View.VISIBLE);
                status.setText("Incorrect password.");
            } else if (!cfmpasstxt.equals(newpasstxt)) {
                status.setVisibility(View.VISIBLE);
                status.setText("Passwords do not match.");
            } else {
                resetPassword();
            }
        }
    }

    public Boolean passwordCorrect(String oldpasstxt) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", super.email));
        params.add(new BasicNameValuePair("password", oldpasstxt));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "login", params);
        if (json != null) {
            try {
                JSONObject json_user = json.getJSONObject("data");
                if (json_user.length() > 0) {
                    int user = json_user.getInt("id"); //"count" to "id"
                    if (user > 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void resetPassword() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", super.email));
        params.add(new BasicNameValuePair("password", newpasstxt));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "resetpass", params);
        if (json != null) {
            try {
                if (json.getString("status").equals("success")) {
                    logoutProcess();
                    Context context = getApplicationContext();
                    CharSequence text = "Password has been changed. Please log in again with the new password.";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}

