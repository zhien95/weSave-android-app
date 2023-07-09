package com.yong.wesave;
/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.yong.wesave.asyntask.AsyncDownloadFbImage;
import com.yong.wesave.util.SessionManager;

import java.io.IOException;
import java.util.HashMap;

import static com.yong.wesave.util.PermissionHelper.hasAllPermissionsGranted;
import static com.yong.wesave.util.PermissionHelper.hasPermissions;

public class WeSaveActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
//  protected GoogleApiClient client;
  SessionManager session;
  protected String name, email, pic, user_id;
  protected Boolean fblogin;
  protected NotificationCompat.Builder mBuilder;
  String[] barcodePermissions = new String[]{
          Manifest.permission.CAMERA,
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.WRITE_EXTERNAL_STORAGE
  };
  String[] supermarketPermissions = new String[]{
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_FINE_LOCATION
  };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.parent_activity);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(R.string.app_name);
    session = new SessionManager(getApplicationContext());
    HashMap<String, String> user = session.getUserDetails();
    name = user.get(SessionManager.KEY_NAME);
    email = user.get(SessionManager.KEY_EMAIL);
    pic = user.get(SessionManager.KEY_PIC);
    user_id = user.get(SessionManager.KEY_ID);
    fblogin = session.isFbLoggedIn();
    try {
      updateProfile(fblogin, name, pic);
    } catch (IOException e) {
      e.printStackTrace();
    }
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();
    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
//    client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    Intent resultIntent = new Intent(this, ScanBarcodeActivity.class);
    PendingIntent resultPendingIntent =
            PendingIntent.getActivity(
                    this,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
    mBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_small_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent);
  }
  
  protected void replaceContentLayout(int sourceId, int destinationId) {
    View contentLayout = findViewById(destinationId);
    ViewGroup parent = (ViewGroup) contentLayout.getParent();
    int index = parent.indexOfChild(contentLayout);
    parent.removeView(contentLayout);
    contentLayout = getLayoutInflater().inflate(sourceId, parent, false);
    parent.addView(contentLayout, index);
  }
  
  protected void replaceTitle(int title) {
    getSupportActionBar().setTitle(title);
  }
  
  protected void updateProfile(Boolean fblogin, String name, String pic) throws IOException {
    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    View hView = navigationView.getHeaderView(0);
    TextView nav_user = (TextView) hView.findViewById(R.id.usernameTextView);
    ImageView profilepic = (ImageView) hView.findViewById(R.id.profileImageView);
    nav_user.setText(name);
    if (fblogin) {
      AsyncTask task = new AsyncDownloadFbImage(profilepic);
      task.execute(new String[]{pic});
    } else {
    }
    profilepic.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
      }
    });
  }
  
  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }
  
  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    if (id == R.id.nav_home) {
      Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
      return true;
      
    } else if (id == R.id.nav_categories) {
      Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
      startActivity(intent);
      return true;
      
    } else if (id == R.id.nav_scan_barcode) {
      if (hasPermissions(getApplicationContext(), barcodePermissions)) {
        Intent intent = new Intent(getApplicationContext(), ScanBarcodeActivity.class);
        startActivity(intent);
      } else {
        ActivityCompat.requestPermissions(WeSaveActivity.this,
                barcodePermissions, 1);
      }
      return true;
      
    } else if (id == R.id.Nearby) {
      if (hasPermissions(getApplicationContext(),supermarketPermissions)) {
        Intent intent = new Intent(getApplicationContext(), NearbyActivity.class);
        startActivity(intent);
      } else {
        ActivityCompat.requestPermissions(WeSaveActivity.this,
                supermarketPermissions, 2);
      }
      return true;
    } else if (id == R.id.nav_following_items) {
      Intent intent = new Intent(getApplicationContext(), FollowingActivity.class);
      startActivity(intent);
      return true;
      
    } else if (id == R.id.nav_shopping_plan) {
      Intent intent = new Intent(getApplicationContext(), MyShoppingPlanActivity.class);
      startActivity(intent);
      return true;
      
    } else if (id == R.id.nav_notifications) {
      Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
      startActivity(intent);
      return true;
      
    } else if (id == R.id.nav_feedback) {
      Intent intent = new Intent(getApplicationContext(), SendFeedbackActivity.class);
      startActivity(intent);
      return true;
      
    } else if (id == R.id.nav_contributions) {
      Intent intent = new Intent(getApplicationContext(), MyContributionsActivity.class);
      startActivity(intent);
      return true;
    } else if (id == R.id.nav_change_password) {
      Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
      startActivity(intent);
      return true;
    } else if (id == R.id.nav_preferences) {
      Intent intent = new Intent(getApplicationContext(), Preferences.class);
      startActivity(intent);
      return true;
    } else if (id == R.id.nav_logout) {
      logoutProcess();
      return true;
    }
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
//  @Override
//  public void onStart() {
//    super.onStart();
//    // ATTENTION: This was auto-generated to implement the App Indexing API.
//    // See https://g.co/AppIndexing/AndroidStudio for more information.
//    client.connect();
//    Action viewAction = Action.newAction(
//            Action.TYPE_VIEW, // TODO: choose an action type.
//            "ScanBarcode Page", // TODO: Define a title for the content shown.
//            // TODO: If you have web page content that matches this app activity's content,
//            // make sure this auto-generated web page URL is correct.
//            // Otherwise, set the URL to null.
//            Uri.parse("http://host/path"),
//            // TODO: Make sure this auto-generated app URL is correct.
//            Uri.parse("android-app://com.yong.wesave/http/host/path")
//    );
//    AppIndex.AppIndexApi.start(client, viewAction);
//  }
//
//  @Override
//  public void onStop() {
//    super.onStop();
//    // ATTENTION: This was auto-generated to implement the App Indexing API.
//    // See https://g.co/AppIndexing/AndroidStudio for more information.
//    Action viewAction = Action.newAction(
//            Action.TYPE_VIEW, // TODO: choose an action type.
//            "ScanBarcode Page", // TODO: Define a title for the content shown.
//            // TODO: If you have web page content that matches this app activity's content,
//            // make sure this auto-generated web page URL is correct.
//            // Otherwise, set the URL to null.
//            Uri.parse("http://host/path"),
//            // TODO: Make sure this auto-generated app URL is correct.
//            Uri.parse("android-app://com.yong.wesave/http/host/path")
//    );
//    AppIndex.AppIndexApi.end(client, viewAction);
//    client.disconnect();
//  }
  
  public void logoutProcess() {
    LoginManager.getInstance().logOut();
    session.logoutUser();
    Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
    startActivity(logout);
  }
  
  //done by Zhi En
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (hasAllPermissionsGranted(grantResults)) {
      Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
      Intent intent;
      switch(requestCode){
        case 1:
          intent = new Intent(getApplicationContext(), ScanBarcodeActivity.class);
          startActivity(intent);
          break;
        case 2:
          intent = new Intent(getApplicationContext(), NearbyActivity.class);
          startActivity(intent);
          break;
      }
      
    } else {
      Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
    }
    
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.action_search, menu);
    final MenuItem searchItem = menu.findItem(R.id.action_search);
    final SearchView mSearchView = (SearchView) searchItem.getActionView();
    mSearchView.setQueryHint("Search Items");
    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent(getApplicationContext(), SearchResult.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra("query", query);
        startActivity(intent);
        return false;
      }
      
      @Override
      public boolean onQueryTextChange(String newText) {
        return true;
      }
    });
    return super.onCreateOptionsMenu(menu);
  }
  
}