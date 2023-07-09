package com.yong.wesave;

/**
 * Created by Yong on 9/12/2016.
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */
/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.yong.wesave.util.Validation.validateEmail;
import static com.yong.wesave.util.Validation.validateFields;

public class RegisterActivity extends AppCompatActivity implements OnClickListener {

    EditText email, password, cfmPassword;
    Button signUp;
    ServerRequest sr;
    ProgressBar mProgressBar;
    String emailtxt, passwordtxt, cfmPasswordtxt, username, userid;
    List<NameValuePair> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        signUp = (Button) findViewById(R.id.signUpBtn);
        email = (EditText) findViewById(R.id.signUpEmail);
        password = (EditText) findViewById(R.id.signUpPassword);
        cfmPassword = (EditText) findViewById(R.id.signUpCfmPassword);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        signUp.setOnClickListener(this);

        /*Toolbar myToolbar = (Toolbar) findViewById(R.id.back_bar);
        setSupportActionBar(myToolbar);*/
    }

    @Override
    public void onClick(View v) {
        if (v == signUp) {
            emailtxt = email.getText().toString().trim().toLowerCase();
            passwordtxt = password.getText().toString();
            cfmPasswordtxt = cfmPassword.getText().toString();
            int err = 0;
            String err_msg = "";
            if (!validateEmail(emailtxt)) {
                email.setError("Please enter a valid email!");
                err++;
            }
            if (!validateFields(passwordtxt)) {
                password.setError("Password should not be empty!");
                err++;
            } else {
                if (!passwordtxt.equals(cfmPasswordtxt)) {
                    cfmPassword.setError("Passwords do not match. Please try again!");
                    err++;
                }
            }

            if (err == 0) {
                mProgressBar.setVisibility(View.VISIBLE);

                if (findUser(emailtxt) == -1) {
                    registerUser(emailtxt, passwordtxt, false);
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "The email address you have entered is already registered.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        }
    }

    private void loginProcess(String emailtxt, String passwordtxt) {

        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", emailtxt));
        params.add(new BasicNameValuePair("password", passwordtxt));
        params.add(new BasicNameValuePair("username", username));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "login", params);
        if (json != null) {
            try {
                JSONObject json_user = json.getJSONObject("data");
                if (json_user != null) {
                    String pic = "";
                    username = json_user.getString("username");
                    userid = Integer.toString(json_user.getInt("id"));
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerUser(String emailtxt, String passwordtxt, boolean fb) {

        username = emailtxt.substring(0, emailtxt.indexOf('@'));
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", emailtxt));
        params.add(new BasicNameValuePair("password", passwordtxt));
        params.add(new BasicNameValuePair("username", username));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "register", params);

        if (json != null) {
            try {
                if (json.getString("status").equals("success") && !fb) {
                    loginProcess(emailtxt, passwordtxt);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Context context = getApplicationContext();
                CharSequence text = "Register failed. Please try again later.";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }

    private int findUser(String emailtxt) {
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", emailtxt));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "finduser", params);
        int user_id = -1;

        if (json != null) {
            try {
                if (json.getString("status").equals("success") && json.getJSONObject("data") != null) { //edited
                    user_id = json.getJSONObject("data").getInt("id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                /*Context context = getApplicationContext();
                CharSequence text = "Register failed. Please try again later!";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();*/
            }
        }
        return user_id;

    }
}