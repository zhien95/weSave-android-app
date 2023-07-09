package com.yong.wesave.asyntask;

import android.os.AsyncTask;
import android.util.Log;

import com.yong.wesave.util.ServerRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yong on 3/1/2017.
 */
public class AsyncRegisterNewUser extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... args) {

        List<NameValuePair> params;
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("req", args[0]));
        //params.add(new BasicNameValuePair("password", args[1]));

        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON("http://127.0.0.1:3000/api/register", params);

        if (json != null) {
            try {
                String jsonstr = json.getString("response");

                Log.d("Hello", jsonstr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
