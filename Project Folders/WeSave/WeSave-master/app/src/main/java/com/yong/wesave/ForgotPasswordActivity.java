package com.yong.wesave;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yong.wesave.asyntask.AsyncSendMail;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.yong.wesave.util.Validation.validateEmail;

/**
 * Created by Yong on 5/1/2017.
 */

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

public class ForgotPasswordActivity extends AppCompatActivity implements OnClickListener {

    TextView status;
    EditText res_email;
    Button cont, cancel;
    String email_res_txt;
    List<NameValuePair> params;
    ServerRequest sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        cont = (Button) findViewById(R.id.resbtn);
        cancel = (Button) findViewById(R.id.cancelbtn);
        res_email = (EditText) findViewById(R.id.email);
        status = (TextView) findViewById(R.id.statusTextView);

        cont.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == cont) {
            email_res_txt = res_email.getText().toString().trim().toLowerCase();
            if (!validateEmail(email_res_txt)) {
                status.setVisibility(View.VISIBLE);
                status.setText("Invalid Email!");
            } else {
                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", email_res_txt));

                ServerRequest sr = new ServerRequest();
                JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "finduser", params);

                if (json != null) {
                    try {
                        JSONObject json_user = json.getJSONObject("data");
                        if (json_user.length() > 0) {
                            int user = json_user.getInt("id"); //change from "count" to "id"
                            if (user > 0) {
                                resetPassword(email_res_txt);
                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                                Context context = getApplicationContext();
                                CharSequence text = "Email has been sent. Please follow the instruction to reset your password.";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                finish();
                            } else {
                                status.setVisibility(View.VISIBLE);
                                status.setText("Email not registered.");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (v == cancel) {
            finish();
        }

    }

    public void resetPassword(String email) {
        String newpass = getSaltString();
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email_res_txt));
        params.add(new BasicNameValuePair("password", newpass));

        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "resetpass", params);

        if (json != null) {
            sendEmail(email, newpass);
        }
    }

    public void sendEmail(String email, String newpass) {

        String emailbody = "Hi,\n\nThe password for your WeSave account with the e-mail address " + email +
                " has successfully been changed. Here is your new password:\n" + newpass + "\n\nRegards,\nWeSave Team";
        String emailsubject = "Your password has been reset";
        String emailsender = "wesaveappinfo@gmail.com";
        try {

            AsyncTask task = new AsyncSendMail();
            task.execute(new String[]{emailsubject, emailbody, emailsender, email});

        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }

    }

    public String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 11) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
