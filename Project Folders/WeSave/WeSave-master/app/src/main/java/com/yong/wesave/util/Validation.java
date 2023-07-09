package com.yong.wesave.util;

/**
 * Created by Yong on 4/1/2017.
 */

import android.text.TextUtils;
import android.util.Patterns;

public class Validation {

    public static boolean validateFields(String name) {

        if (TextUtils.isEmpty(name)) {

            return false;

        } else {

            return true;
        }
    }

    public static boolean validateEmail(String string) {

        if (TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches()) {

            return false;

        } else {

            return true;
        }
    }
}