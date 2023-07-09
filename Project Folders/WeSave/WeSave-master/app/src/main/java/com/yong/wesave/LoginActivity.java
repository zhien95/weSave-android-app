package com.yong.wesave;
/**
 * Created by Yong on 9/12/2016.
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 * <p>
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */
/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.yong.wesave.common.Constants;
import com.yong.wesave.util.ServerRequest;
import com.yong.wesave.util.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.yong.wesave.util.PermissionHelper.hasAllPermissionsGranted;
import static com.yong.wesave.util.PermissionHelper.hasPermissions;
import static com.yong.wesave.util.Validation.validateEmail;
import static com.yong.wesave.util.Validation.validateFields;

public class LoginActivity extends AppCompatActivity implements OnClickListener {
  private LoginButton fbLoginButton;
  private CallbackManager callbackManager;
  private TextView textView;
  private Button loginButton;
  private Button fingerprintButton;
  private Button signUp;
  private EditText email;
  private EditText password;
  private TextView forgotPassword;
  private TextView statusTextView;
  private AccessTokenTracker accessTokenTracker;
  private ProfileTracker profileTracker;
  private ProgressBar mProgressBar;
  private Profile profile;
  String emailtxt, passwordtxt, username, userid;
  List<NameValuePair> params;
  // Session Manager Class
  SessionManager session;
  SharedPreferences pref;
  //barcodePermissions
  private String[] permissions = {
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_FINE_LOCATION,
  };
  //Facebook login
  private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
    @Override
    public void onSuccess(LoginResult loginResult) {
      //auth = new ParseObject("auth");
      // Facebook Email address
      GraphRequest request = GraphRequest.newMeRequest(
              loginResult.getAccessToken(),
              new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                  try {
                    if (Profile.getCurrentProfile() == null) {
                      profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile,
                                                               Profile currentProfile) {
                          Log.v("facebook - profile", currentProfile.getFirstName());
                          profileTracker.stopTracking();
                        }
                      };
                      // no need to call startTracking() on mProfileTracker
                      // because it is called by its constructor, internally.
                    } else {
                      profile = Profile.getCurrentProfile();
                      Log.v("facebook - profile", profile.getFirstName());
                      String email = object.getString("email");
                      String name = Profile.getCurrentProfile().getName();
                      String pic =
                              Profile.getCurrentProfile().getProfilePictureUri(100, 100).toString();
                      if (findUser(email) < 0) {
                        registerUser(email, "", true);
                      }
                      userid = Integer.toString(findUser(email));
                      session.createLoginSession(name, email, userid, pic, true);
                      Intent intent = new Intent(LoginActivity.this, fingerprint.class);
                      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                      startActivity(intent);
                      finish();
                    }
                    
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                }
              });
      Bundle parameters = new Bundle();
      parameters.putString("fields", "name,email");
      request.setParameters(parameters);
      request.executeAsync();
    }
    
    @Override
    public void onCancel() {
    }
    
    @Override
    public void onError(FacebookException e) {
      e.printStackTrace();
    }
  };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    session = new SessionManager(this);
    //permissions
    if (hasPermissions(LoginActivity.this, permissions)) {
      //Auto Login if previously logged in
      autoLogin();
    } else {
      ActivityCompat.requestPermissions(LoginActivity.this,
              permissions, 1);
    }
    callbackManager = CallbackManager.Factory.create();
    FacebookSdk.sdkInitialize(getApplicationContext());
    setContentView(R.layout.activity_login);
//    AppEventsLogger.activateApp(this);
    // Session Manager
    session = new SessionManager(getApplicationContext());
    AccessToken token = AccessToken.getCurrentAccessToken();
    if (token != null) {
      Intent intent = new Intent(LoginActivity.this, ScanBarcodeActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
      finish();
    }
    accessTokenTracker = new AccessTokenTracker() {
      @Override
      protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
      }
    };
    profileTracker = new ProfileTracker() {
      @Override
      protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
        this.stopTracking();
        Profile.setCurrentProfile(newProfile);
      }
    };
    accessTokenTracker.startTracking();
    profileTracker.startTracking();
    fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
    textView = (TextView) findViewById(R.id.info);
    fbLoginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));
    fbLoginButton.registerCallback(callbackManager, callback);
    loginButton = (Button) findViewById(R.id.iv_login);
    email = (EditText) findViewById(R.id.tv_user_email);
    password = (EditText) findViewById(R.id.tv_password);
    forgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
    signUp = (Button) findViewById(R.id.iv_register);
    statusTextView = (TextView) findViewById(R.id.statusTextView);
    mProgressBar = (ProgressBar) findViewById(R.id.progress);
    loginButton.setOnClickListener(this);
    forgotPassword.setOnClickListener(this);
    signUp.setOnClickListener(this);
    statusTextView.setVisibility(View.INVISIBLE);
    fingerprintButton = (Button) findViewById(R.id.iv_fingerprint);
    fingerprintButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent i = new Intent(LoginActivity.this, fingerprint.class);
        startActivity(i);
        
      }
    });
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }
  
  @Override
  public void onClick(View v) {
    if (v == loginButton) {
      emailtxt = email.getText().toString().trim().toLowerCase();
      passwordtxt = password.getText().toString();
      int err = 0;
      String err_msg = "";
      if (!validateEmail(emailtxt)) {
        err++;
        email.setError("Please enter a valid email!\n");
      }
      if (!validateFields(passwordtxt)) {
        err++;
        password.setError("Password should not be empty!\n");
      }
      if (err > 0) {
        mProgressBar.setVisibility(View.GONE);
        statusTextView.setVisibility(View.VISIBLE);
        statusTextView.setText(err_msg);
      } else {
        mProgressBar.setVisibility(View.VISIBLE);
        loginProcess(emailtxt, passwordtxt);
        mProgressBar.setVisibility(View.GONE);
        
      }
    } else if (v == forgotPassword) {
      Intent forgotPasswordIntent = new Intent(this, ForgotPasswordActivity.class);
      startActivity(forgotPasswordIntent);
    } else if (v == signUp) {
      Intent registerIntent = new Intent(this, RegisterActivity.class);
      startActivity(registerIntent);
    }
  }
  
  @Override
  public void onStop() {
    super.onStop();
    accessTokenTracker.stopTracking();
    profileTracker.stopTracking();
  }
  
  @Override
  public void onResume() {
    super.onResume();
  }
  
  @Override
  protected void onStart() {
    super.onStart();
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
          session.createLoginSession(username, emailtxt, userid, pic, false);
          Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(intent);
          finish();
          
        } else {
          statusTextView.setVisibility(View.VISIBLE);
          statusTextView.setText("Incorrect Email / Password Combination");
        }
        
      } catch (JSONException e) {
        statusTextView.setVisibility(View.VISIBLE);
        statusTextView.setText("Incorrect Email / Password Combination");
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
    statusTextView.setVisibility(View.VISIBLE);
    statusTextView.setText(json.toString());
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
  
  public int findUser(String emailtxt) {
    params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("email", emailtxt));
    ServerRequest sr = new ServerRequest();
    JSONObject json = sr.getJSON(Constants.API_BASE_URL_API + "finduser", params);
    int user_id = -1;
    if (json != null) {
      try {
        if (json.getString("status").equals("success") && json.getJSONObject("data") != null) {
          //edited
          user_id = json.getJSONObject("data").getInt("id");
        }
      } catch (JSONException e) {
        e.printStackTrace();
                /*Context context = getApplicationContext();
                CharSuequence text = "Register failed. Please try again later!";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();*/
      }
    }
    return user_id;
    
  }
  //done by zhien
  
  @Override
  public void onBackPressed() {
    // disable going back to the MainActivity
    super.onBackPressed();
    moveTaskToBack(true);
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (hasAllPermissionsGranted(grantResults)) {
      Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
      autoLogin();
    } else {
      Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
      android.os.Process.killProcess(android.os.Process.myPid()); //quit app
    }
  }
  
  private void autoLogin(){
    if (session.isLoggedIn()) {
      Intent intent = new Intent(this, HomeActivity.class);
      startActivity(intent);
      finish();
    }
  }
  
  
}