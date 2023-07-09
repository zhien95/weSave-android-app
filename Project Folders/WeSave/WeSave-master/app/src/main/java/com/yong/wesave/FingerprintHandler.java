package com.yong.wesave;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.CancellationSignal;
import androidx.core.app.ActivityCompat;

import android.widget.Toast;
import android.os.*;

import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;
import com.yong.wesave.util.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    // You should use the CancellationSignal method whenever your app can no longer process user input, for example when your app goes
    // into the background. If you don’t use this method, then other apps will be unable to access the touch sensor, including the lockscreen!//

    private CancellationSignal cancellationSignal;
    private Context context;
    SessionManager session;
    String emailtxt ="jeanelle@gmail.com";
    List<NameValuePair> params;

    public FingerprintHandler(Context mContext) {
        context = mContext;
        session = new SessionManager(mContext);
    }

    //Implement the startAuth method, which is responsible for starting the activity_fingerprint authentication process//

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }
    @Override
    //onAuthenticationError is called when a fatal error has occurred. It provides the error code and error message as its parameters//

    public void onAuthenticationError(int errMsgId, CharSequence errString) {

        //I’m going to display the results of activity_fingerprint authentication as a series of toasts.
        //Here, I’m creating the message that’ll be displayed if an error occurs//

        Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
    }

    @Override

    //onAuthenticationFailed is called when the activity_fingerprint doesn’t match with any of the fingerprints registered on the device//

    public void onAuthenticationFailed() {
        Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
    }

    @Override

    //onAuthenticationHelp is called when a non-fatal error has occurred. This method provides additional information about the error,
    //so to provide the user with as much feedback as possible I’m incorporating this information into my toast//
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
    }

    @Override

    //onAuthenticationSucceeded is called when a activity_fingerprint has been successfully matched to one of the fingerprints stored on the user’s device//
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {

        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", emailtxt));
        ServerRequest sr = new ServerRequest();
        JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "finduser", params);
        if (json != null) {

            try {
                JSONObject json_user = json.getJSONObject("data");
                if (json_user != null) {
                    String pic = "";
                    String username = json_user.getString("username");
                    String userid = Integer.toString(json_user.getInt("id"));
                    session.createLoginSession(username, emailtxt, userid, pic, false);
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }

            } catch (JSONException e) {
            }
        }
    }


}