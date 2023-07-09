package com.yong.wesave.asyntask;

import android.os.AsyncTask;
import android.util.Log;

import com.yong.wesave.common.Constants;
import com.yong.wesave.util.GMailSender;

/**
 * Created by Yong on 8/1/2017.
 */

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

public class AsyncSendMail extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... args) {

        try {
            //GMailSender sender = new GMailSender("wesaveappinfo@gmail.com", "wesave279");
            GMailSender sender = new GMailSender(Constants.EMAIL, "Singaporewesave");
            sender.sendMail(args[0], args[1], args[2], args[3]);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }

        return null;
    }


}
