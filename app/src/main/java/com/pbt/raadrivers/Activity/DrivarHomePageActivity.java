package com.pbt.raadrivers.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.pbt.raadrivers.Fonts.CButton;
import com.pbt.raadrivers.Fonts.CTextView;
import com.pbt.raadrivers.Notification.Config;
import com.pbt.raadrivers.Notification.NotificationUtils;
import com.pbt.raadrivers.Other.ExpandableNavigationListView;
import com.pbt.raadrivers.Pojo.ChildModel;
import com.pbt.raadrivers.Pojo.HeaderModel;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Receiver.AlarmReceiver;
import com.pbt.raadrivers.Service.LocationUpdatesService;
import com.pbt.raadrivers.Service.NormalService;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.ConnectivityReceiver;
import com.pbt.raadrivers.Utils.CustomExceptionHandler;
import com.pbt.raadrivers.Utils.DebugLog;
import com.pbt.raadrivers.Utils.LocationTrack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.shadowfax.proswipebutton.ProSwipeButton;

import static com.pbt.raadrivers.Utils.Common.CheckGpsStatus;
import static com.pbt.raadrivers.Utils.Common.driver;
import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;


public class DrivarHomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, ConnectivityReceiver.ConnectivityReceiverListener, AppConstant {


    private Boolean network = Boolean.TRUE;
    private ConnectivityReceiver connectivityReceiver;

    private static final int REQUESTFORMI = 10;
    private static final int READPHONESTATE = 51;
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    public String manufacture = "xiaomi";
    Location mLastLocation;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Marker mCurrLocationMarker;
    ArrayList<LatLng> MarkerPoints;
    LatLng origin, dest;
    double Latitudedefoult, Longitudedefoult;
    float getbearing;
    String distanceText, durationText, incity, roundtrip, car_number, car_name, fueltype, car_type;
    Dialog dialog;
    ProSwipeButton endslider;
    ImageView loc;
    LinearLayout ls2;

    static boolean active = false;
    CButton offduty, ondutty;
    CTextView car_name_id, car_type_heder, phone_number, driver_name, address, textView1, textView2, incityfrompayload, startotpfrompayload;
    CircleImageView imageView;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    String trip_id, payload, address_one;
    String start_lng, start_lat, cust_number, messageTO, image_pref;
    int dutymode = 0;
    LocationTrack locationTrack;
    // The BroadcastReceiver used to listen from broadcasts from the service.
    MyReceiver myReceiver;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    private LatLng currentlatlng;
    private double curlat;
    private double curlong, navlat, navlong;

    CTextView appversion;

    RequestQueue queue;
    SharedPreferences sharedpreferences;
    int data;
    PowerManager pm;
    private static final int IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002;
    private ExpandableNavigationListView navigationExpandableListView;

    private String TAG = "DrivarHomePage";

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = (LocationUpdatesService) binder.getService();
            mBound = true;
            mService.requestLocationUpdates();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };


    protected PowerManager.WakeLock mWakeLock;
    String whaitingcustpayload, whaitingaddress_one, whaitingmessageTO;
    Toolbar toolbar;

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        active = false;
        super.onStop();
    }

    LocationTrack locationTrack2;

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    public static final int FILE_PERMISSION_REQUEST_CODE = 112;

    private static final String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    RecieverIsMockLocation recieverIsMockLocation;

    Activity activity;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivar_home_page);

        connectivityReceiver = new ConnectivityReceiver();
        connectivityReceiver.addListener(this);
        this.registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        activity = this;
        recieverIsMockLocation = new RecieverIsMockLocation();
        IntentFilter updateAlertIntentFilter = new IntentFilter();
        updateAlertIntentFilter.addAction(NormalService.ISMOCKLOCATION);
        registerReceiver(recieverIsMockLocation, updateAlertIntentFilter);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+FOLDERNAME, "", getApplicationContext()));
        }

        locationTrack2 = new LocationTrack(DrivarHomePageActivity.this);


        toolbar = findViewById(R.id.toolbar);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        navigationExpandableListView = findViewById(R.id.expandable_navigation);

        initAllView();

        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);

        logtokan = sharedpreferences.getString("tokan", "");
        Log.e("LocationCheck","Token :"+"Bearer " + logtokan);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                askIgnoreOptimization();
            } else {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    openPermission();
                } else {

                    if (mMap != null)
                        mMap.setMyLocationEnabled(true);

                    if (!CheckGpsStatus(DrivarHomePageActivity.this)) {
                        displayLocationSettingsRequest(DrivarHomePageActivity.this);
                    } else {
                        CallAllDetails();
                    }
                }
            }
        } else {
            CallAllDetails();
        }

        startMyService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationTrack2 = new LocationTrack(DrivarHomePageActivity.this);
                    displayLocationSettingsRequest(DrivarHomePageActivity.this);
                    ActivityCompat.requestPermissions(this, PERMISSIONS, FILE_PERMISSION_REQUEST_CODE);
                } else {
                    openPermission();
                }
            }
            case FILE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    String extStorageDirectory = null;
                    File file;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
                        file = new File(getApplication().getFilesDir(), FOLDERNAME);
                    else {
                        extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                        file = new File(extStorageDirectory, FOLDERNAME);
                    }
                    if (!file.exists())
                        file.mkdirs();
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts(PACKAGE, getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        }
    }

    private void askIgnoreOptimization() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, IGNORE_BATTERY_OPTIMIZATION_REQUEST);
    }

    private void askforlocationpermision() {
        ActivityCompat.requestPermissions(DrivarHomePageActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }


    private void initAllView() {

        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String version_name = packageInfo.versionName;

        offduty = findViewById(R.id.editText1);
        ondutty = findViewById(R.id.editText2);
        appversion = findViewById(R.id.appversion);
        appversion.setText("Version : " + version_name);
        myReceiver = new MyReceiver();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationExpandableListView
                .init(this)
                .addHeaderModel(new HeaderModel(getResources().getString(R.string.monthly_acc), R.drawable.monthly_account))
                .addHeaderModel(new HeaderModel(getResources().getString(R.string.help), R.drawable.help))
                .addHeaderModel(new HeaderModel(getResources().getString(R.string.myqr_code), R.drawable.qr_code))
                .addHeaderModel(new HeaderModel(getResources().getString(R.string.log_out), R.drawable.logout))
                .build()
                .addOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        navigationExpandableListView.setSelected(groupPosition);
                        if (id == 0) {
                            startActivity(new Intent(DrivarHomePageActivity.this, MonthlyAccountActivity.class));
                            finish();
                            drawer.closeDrawer(GravityCompat.START);
                        } else if (id == 1) {
                            startActivity(new Intent(DrivarHomePageActivity.this, HelpActivity.class));
                            finish();
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        else if (id == 2) {
                            Intent i = new Intent(DrivarHomePageActivity.this, RaaQRCodeActivity.class);
                            i.putExtra("iscust", 0);
                            startActivity(i);
                            finish();
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        else if (id == 3) {
                            String isridestart = sharedpreferences.getString("isridestart", "");
                            if (isridestart.equalsIgnoreCase("yes")) {
                                Toast.makeText(DrivarHomePageActivity.this, getResources().getString(R.string.logoutstingfordriver), Toast.LENGTH_LONG).show();
                            } else {
                                logoutclass();
                                if (mService != null)
                                    mService.removeLocationUpdates();
                            }
                        }
                        return false;
                    }
                })
                .addOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        navigationExpandableListView.setSelected(groupPosition, childPosition);
                        if (id == 0) {
                            Intent i = new Intent(DrivarHomePageActivity.this, DriverProfileActivity.class);
                            startActivity(i);
                            finish();
                        } else if (id == 1) {
                            startActivity(new Intent(DrivarHomePageActivity.this, SelectLanguageActivity.class));
                            finish();
                        } else if (id == 2) {
                            startActivity(new Intent(DrivarHomePageActivity.this, TrainingActivity.class));
                            finish();
                        } else if (id == 3) {
                            startActivity(new Intent(DrivarHomePageActivity.this, SwitchCarActivity.class));
                            finish();
                        }
                        drawer.closeDrawer(GravityCompat.START);
                        return false;
                    }
                });
        navigationExpandableListView.expandGroup(2);
        View headerView = navigationView.getHeaderView(0);

        driver_name = headerView.findViewById(R.id.driver_name);
        car_name_id = headerView.findViewById(R.id.car_name);
        phone_number = headerView.findViewById(R.id.phone_number);
        car_type_heder = headerView.findViewById(R.id.car_type);
        imageView = headerView.findViewById(R.id.imageView);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String datafromnoti = intent.getStringExtra("datafromnoti");
                    JSONObject datafromnot = new JSONObject(datafromnoti);
                    JSONObject data = datafromnot.getJSONObject("data");

                    try {
                        int driver = data.getInt("is_driver");
                        String message = intent.getStringExtra("message");
                        if (driver == 1 && !message.equalsIgnoreCase("New_latlong_update")) {
                            payload = intent.getStringExtra("payload");
                             message = intent.getStringExtra("message");
                            driver(DrivarHomePageActivity.this, message, payload, datafromnoti);
                        }
                    } catch (Exception e) {
                        DebugLog.printError(TAG, ""+ e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                    }
                } catch (Exception e) {
                    DebugLog.printError(TAG, ""+ e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                }
            }
        };
    }

    private void CallAllDetails() {

        car_id = sharedpreferences.getString("car_id", "");
        car_number = sharedpreferences.getString("car_number", "");
        car_name = sharedpreferences.getString("car_name", "");
        image_pref = sharedpreferences.getString("image", "");
        fueltype = sharedpreferences.getString("fueltype", "");
        car_type = sharedpreferences.getString("car_type", "");
        dutymode = sharedpreferences.getInt("dutymode", 0);
        String email = sharedpreferences.getString("email", "");
        String finalbill = sharedpreferences.getString("finalBill", "");
        String roundtripfrompayload = sharedpreferences.getString("roundtripfrompayload", "");
        String incityfrompayloadtext = sharedpreferences.getString("incityfrompayload", "");
        String is_autop_tripfrompayload = sharedpreferences.getString("is_autop_tripfrompayload", "");
        String start_otp1frompayload = sharedpreferences.getString("start_otp1frompayload", "");
        String payloadfromnoti = sharedpreferences.getString("payloadfromnoti", "");
        String messagefromnoti = sharedpreferences.getString("messagefromnoti", "");
        String first_name = sharedpreferences.getString("first_name", "");
        String phone = sharedpreferences.getString("phone", "");
        String car_type = sharedpreferences.getString("car_type", "");
//        setMyAlarm();
        if (finalbill.equalsIgnoreCase("yes")) {
            Log.d("asdf tag", "finish ride");
            Intent i = new Intent(DrivarHomePageActivity.this, FinishRideActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }

        driver_name.setText(first_name);
        phone_number.setText(phone);
        car_name_id.setText(car_number);
        car_type_heder.setText("Type: " + car_type + " Fuel: " + fueltype);

        Log.e("ImgageUrl", "Image Url : " + image + "users/thumbnail/" + image_pref);

        UrlImageViewHelper.setUrlDrawable(imageView, image + "users/thumbnail/" + image_pref, R.drawable.raalogo_white);

        endslider = findViewById(R.id.endslider);
        incityfrompayload = findViewById(R.id.incityfrompayload);

        incityfrompayload.setText(Html.fromHtml("Trip Type:  <b>" + roundtripfrompayload + "</b>   <b>" + incityfrompayloadtext + "</b>"));

        offduty.setBackground(DrivarHomePageActivity.this.getResources().getDrawable(R.drawable.selectedrounded));
        offduty.setTextColor(DrivarHomePageActivity.this.getResources().getColor(R.color.white));
        offduty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if(sharedpreferences.getString("trip_status", "") == null ||  sharedpreferences.getString("trip_status", "").isEmpty()) {


                        ondutty.setBackground(getResources().getDrawable(R.drawable.selectedrounded));
                        offduty.setBackground(getResources().getDrawable(R.drawable.rounded));
                        ondutty.setTextColor(getResources().getColor(R.color.white));
                        offduty.setTextColor(getResources().getColor(R.color.bleck));
                        dutymode = 0;
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt("dutymode", dutymode);
                        editor.putString("starttimestemp", String.valueOf(System.currentTimeMillis()));
                        editor.apply();
                        if (mService != null)
                            mService.requestLocationUpdates();
                        dutyclass();

//                    stopForeground();


                        Toast.makeText(DrivarHomePageActivity.this, "You are off duty now", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    DebugLog.printError(TAG, ""+ e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                }
            }
        });


        ondutty.setBackground(getResources().getDrawable(R.drawable.selectedrounded));
        offduty.setBackground(getResources().getDrawable(R.drawable.rounded));
        ondutty.setTextColor(getResources().getColor(R.color.white));
        offduty.setTextColor(getResources().getColor(R.color.bleck));

        ondutty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Log.e("HomePage"," Trip Status : "+ sharedpreferences.getString("trip_status", ""));

                try {


                    if(sharedpreferences.getString("trip_status", "") == null ||  sharedpreferences.getString("trip_status", "").isEmpty()) {


                        offduty.setBackground(getResources().getDrawable(R.drawable.selectedrounded));
                        ondutty.setBackground(getResources().getDrawable(R.drawable.rounded));
                        offduty.setTextColor(getResources().getColor(R.color.white));
                        ondutty.setTextColor(getResources().getColor(R.color.bleck));
                        dutymode = 1;
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt("dutymode", dutymode);
                        editor.putString("starttimestemp", String.valueOf(System.currentTimeMillis()));
                        editor.apply();
                        startMyService();
                        Toast.makeText(DrivarHomePageActivity.this, "You are on duty now", Toast.LENGTH_LONG).show();
                        dutyclass();
                    }
                }catch (Exception e){
                    Toast.makeText(DrivarHomePageActivity.this, "You are on duty now Exception --->> "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        textView1 = (CTextView) findViewById(R.id.textView1);
        textView2 = (CTextView) findViewById(R.id.textView2);
        address = (CTextView) findViewById(R.id.textView3);
        loc = findViewById(R.id.loc);
        startotpfrompayload = (CTextView) findViewById(R.id.startotpfrompayload);
        slider = findViewById(R.id.slider);
        endslider = findViewById(R.id.endslider);
        relativeLayout1 = findViewById(R.id.relativeLayout1);
        ls2 = findViewById(R.id.ls2);

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                String tripiID = sharedpreferences.getString("trip_id", "0");
                Log.e(TAG,"Checck Trip Id :  ====>>> "+trip_id);
                callapiforgettingstatus(tripiID);
            }
        });

        if (dutymode == 1) {
            offduty.setBackground(getResources().getDrawable(R.drawable.selectedrounded));
            ondutty.setBackground(getResources().getDrawable(R.drawable.rounded));
            offduty.setTextColor(getResources().getColor(R.color.white));
            ondutty.setTextColor(getResources().getColor(R.color.bleck));
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("dutymode", dutymode);
            editor.apply();
        } else {
            ondutty.setBackground(getResources().getDrawable(R.drawable.selectedrounded));
            offduty.setBackground(getResources().getDrawable(R.drawable.rounded));
            ondutty.setTextColor(getResources().getColor(R.color.white));
            offduty.setTextColor(getResources().getColor(R.color.bleck));
            SharedPreferences sharedpreferences;
            sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("dutymode", dutymode);
            editor.remove("timeinbroadcastriciver");
            editor.remove("curranttimestemp");
            editor.remove("starttimestemp");
            editor.apply();
        }

        if (is_autop_tripfrompayload.equalsIgnoreCase("")) {
            startotpfrompayload.setVisibility(View.GONE);
        } else {
            startotpfrompayload.setVisibility(View.VISIBLE);
            String str = " | <span style=\"background-color:" + getResources().getColor(R.color.yellow) + ";\">Start OTP: <b>" + start_otp1frompayload + "</b></span>";
            startotpfrompayload.setText(Html.fromHtml("Auto Trip: <b>" + is_autop_tripfrompayload + "</b>  " + str));
        }

        try {
            whaitingcustpayload = sharedpreferences.getString("whaitingcustpayload", "");
            whaitingaddress_one = sharedpreferences.getString("whaitingaddress_one", "");
            whaitingmessageTO = sharedpreferences.getString("whaitingmessageTO", "");
            Log.e(TAG, "whaitingcustpayload" + whaitingcustpayload);
            Log.e(TAG, "whaitingaddress_one" + whaitingaddress_one);

            if (!whaitingcustpayload.equalsIgnoreCase("")) {
                payload = whaitingcustpayload;
                address_one = whaitingaddress_one;
                messageTO = whaitingmessageTO;
                try {
                    Log.e(TAG, "Check status visible  ===>>>> 670");
                    relativeLayout1.setVisibility(View.VISIBLE);
                    ls2.setVisibility(View.VISIBLE);
                    endslider.setVisibility(View.GONE);
                    slider.setVisibility(View.VISIBLE);
                    SharedPreferences preLocationCheck = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorLocationCheck = preLocationCheck.edit();
                    editorLocationCheck.putString("isStart", "1");
                    editorLocationCheck.apply();
                    JSONObject trip = new JSONObject(payload);

                    trip_id = trip.getString("trip_id");
                    String cust_name = sharedpreferences.getString("cust_name_driver", "");
                    cust_number = sharedpreferences.getString("cust_number_driver", "");
                    start_lng = trip.getString("start_lng");
                    start_lat = trip.getString("start_lat");
                    trip_id = trip.getString("trip_id");
                    textView1.setText(Html.fromHtml("Customer: <b>" + cust_name));
                    textView2.setText(Html.fromHtml("Mobile: <b>" + cust_number));
                    slider.setSwipeDistance(.7f);
                    slider.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
                        @Override
                        public void onSwipeConfirm() {
                            if (network) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        slider.showResultIcon(true, false);
                                        Intent io = new Intent(DrivarHomePageActivity.this, StartRideActivity.class);
                                        io.putExtra("trip_id", trip_id);
                                        startActivity(io);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("firsttimedest", "");
                                        editor.putString("firsttimestart", "");
                                        editor.apply();
                                    }
                                }, 2000);
                            } else {
                                Toast.makeText(DrivarHomePageActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    textView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent call = new Intent(Intent.ACTION_DIAL);
                            call.setData(Uri.parse("tel:" + cust_number));
                            startActivity(call);

//                            Intent callIntent = new Intent(Intent.ACTION_CALL);
//                            callIntent.setData(Uri.parse("tel:" + cust_number));
//                            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                                // TODO: Consider calling
//                                //    Activity#requestPermissions
//                                // here to request the missing permissions, and then overriding
//                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                //                                          int[] grantResults)
//                                // to handle the case where the user grants the permission. See the documentation
//                                // for Activity#requestPermissions for more details.
//                                return;
//                            }
//                            startActivity(callIntent);
                        }
                    });
                    address_one = address_one.replace("From: ", "");
                    address.setText(Html.fromHtml("Go to: <b>" + address_one));

                    try {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String firsttimedest, firsttimestart;
                                firsttimedest = sharedpreferences.getString("firsttimedest", "");
                                firsttimestart = sharedpreferences.getString("firsttimestart", "");
                                if (firsttimestart.equals("0.0,0.0")) {
                                    firsttimestart = sharedpreferences.getString("locstart_latitude", "0.0,0.0") + "," + sharedpreferences.getString("locstart_longitude", "0.0,0.0");
                                }
                                if (firsttimedest.equals("0.0,0.0")) {
                                    firsttimedest = sharedpreferences.getString("locstart_latitude", "0.0,0.0") + "," + sharedpreferences.getString("locstart_longitude", "0.0,0.0");
                                }
                                if (!firsttimestart.isEmpty() && !firsttimestart.equalsIgnoreCase("0.0,0.0") && !firsttimedest.isEmpty() && !firsttimedest.equalsIgnoreCase("0.0,0.0")) {
                                    settingmap(firsttimestart, firsttimedest);
                                }
                            }
                        }, 2000);

                    } catch (Exception e) {
                        Log.e(TAG, "Exception " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Exception " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                }
            } else {
                Intent i = getIntent();
                payload = i.getStringExtra("payload");
                address_one = i.getStringExtra("message");

                if (payload != null && !payload.equalsIgnoreCase("")) {
                    new CountDownTimer(1500, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {

                            SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("page", "");
                            editor.putString("message", "");
                            editor.putString("payload", "");
                            editor.apply();

                            if (!address_one.equalsIgnoreCase("New_latlong_update")){
                                if (network) {
                                    conformride();
                                } else {
                                    Toast.makeText(DrivarHomePageActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
                                }
                        }
                        }
                    }.start();
                }
            }
        } catch (Exception e) {
            DebugLog.printError(TAG, ""+ e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
        }
        MarkerPoints = new ArrayList<>();

        try {
            Intent i = getIntent();
            final String startadd = i.getStringExtra("startadd");
            final String endadd = i.getStringExtra("endadd");
            if (startadd != null && !startadd.isEmpty() && !startadd.equals("null")) {
                Log.e(TAG, "Check status visible  ===>>>> 804");
                relativeLayout1.setVisibility(View.VISIBLE);
                ls2.setVisibility(View.VISIBLE);
                endslider.setVisibility(View.VISIBLE);



                SharedPreferences preLocationCheck = getSharedPreferences("Login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorLocationCheck = preLocationCheck.edit();
                editorLocationCheck.putString("isStart", "0");
                editorLocationCheck.apply();
                relativeLayout1.setWeightSum(0.1f);
                slider.setVisibility(View.GONE);
                textView1.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                address.setVisibility(View.GONE);
                address.setText(Html.fromHtml("Go to: <b>" + sharedpreferences.getString("messageTO", "")));
                new CountDownTimer(3000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("page", "");
                        editor.putString("message", "");
                        editor.putString("payload", "");
                        editor.apply();
                        settingmap(startadd, endadd);
                    }
                }.start();
            }
        } catch (Exception e) {
            DebugLog.printError(TAG, ""+ e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
        }
        endslider.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                SharedPreferences preferencesNavigate = getSharedPreferences("NavigateLocation", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorNavigate = preferencesNavigate.edit();
                editorNavigate.clear();
                if (network) {
                    apicallingpois();
                } else {
                    Toast.makeText(DrivarHomePageActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            String isridestart = sharedpreferences.getString("isridestart", "");
            if (isridestart.equalsIgnoreCase("yes")) {
                try {
                    final SharedPreferences sharedpreferences;
                    sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    String status = sharedpreferences.getString("trip_status", "");
                    if (status.isEmpty()) {
                    } else {
                        if (status.equalsIgnoreCase("started")) {
                            Log.e(TAG, "Check status visible  ===>>>> 862");
                            relativeLayout1.setVisibility(View.VISIBLE);
                            ls2.setVisibility(View.VISIBLE);
                            endslider.setVisibility(View.VISIBLE);

                            Log.e(TAG, "Check status visible  ===>>>> 907");
                            SharedPreferences preLocationCheck = getSharedPreferences("Login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editorLocationCheck = preLocationCheck.edit();
                            editorLocationCheck.putString("isStart", "0");
                            editorLocationCheck.apply();
                            relativeLayout1.setWeightSum(0.1f);
                            slider.setVisibility(View.GONE);
                            textView1.setVisibility(View.VISIBLE);
                            textView2.setVisibility(View.VISIBLE);
                            address.setVisibility(View.GONE);
                            incityfrompayload.setVisibility(View.GONE);


                            final String start = sharedpreferences.getString("startaddss", "");
                            final String end = sharedpreferences.getString("endaddss", "");
                            String cust_name = sharedpreferences.getString("cust_name_driver", "");
                            cust_number = sharedpreferences.getString("cust_number_driver", "");
                            start_lng = sharedpreferences.getString("start_lng", "");
                            start_lat = sharedpreferences.getString("start_lat", "");
                            trip_id = sharedpreferences.getString("trip_id", "");
                            roundtrip = sharedpreferences.getString("roundtrip", "");
                            incity = sharedpreferences.getString("incity", "");
                            address_one = sharedpreferences.getString("end_point", "");
                            String incitytxt = "", roundtriptxt = "";
                            if (incity.equalsIgnoreCase("0")) {
                                incitytxt = "In City";
                            } else if (incity.equalsIgnoreCase("1")) {
                                incitytxt = "Out City";
                            }
                            if (roundtrip.equalsIgnoreCase("0")) {
                                roundtriptxt = "One Way";
                            } else if (roundtrip.equalsIgnoreCase("1")) {
                                roundtriptxt = "Return Trip";
                            }

                            textView1.setText(Html.fromHtml("Customer: <b>" + cust_name));
                            textView2.setText(Html.fromHtml("Mobile:<b>" + cust_number));
                            incityfrompayload.setText(Html.fromHtml("Trip Type:  <b>" + roundtriptxt + "</b>   <b>" + incitytxt + "</b>"));
                            address.setText(Html.fromHtml("Go to: <b>" + address_one));

                            textView2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent call = new Intent(Intent.ACTION_DIAL);
                                    call.setData(Uri.parse("tel:" + cust_number));
                                    startActivity(call);
                                }
                            });

                            new CountDownTimer(3000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                }

                                public void onFinish() {
                                    settingmap(start, end);
                                }

                            }.start();
                        } else if (status.equalsIgnoreCase("waiting_for_driver")) {
                            try {
                                Log.e(TAG, "Check status visible  ===>>>> 917");
                                relativeLayout1.setVisibility(View.VISIBLE);
                                ls2.setVisibility(View.VISIBLE);
                                endslider.setVisibility(View.GONE);
                                slider.setVisibility(View.VISIBLE);
                                Log.e(TAG, "Payload Start Ride  : " + payload);
                                SharedPreferences preLocationCheck = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editorLocationCheck = preLocationCheck.edit();
                                editorLocationCheck.putString("isStart", "1");
                                editorLocationCheck.apply();

                                trip_id = sharedpreferences.getString("trip_id", "");
                                String cust_name = sharedpreferences.getString("cust_name_driver", "");
                                cust_number = sharedpreferences.getString("cust_number_driver", "");
                                start_lng = sharedpreferences.getString("start_lng", "");
                                start_lat = sharedpreferences.getString("start_lat", "");
                                trip_id = sharedpreferences.getString("trip_id", "");
                                roundtrip = sharedpreferences.getString("roundtrip", "");
                                incity = sharedpreferences.getString("incity", "");
                                address_one = sharedpreferences.getString("start_point", "");
                                String incitytxt = "", roundtriptxt = "";
                                if (incity.equalsIgnoreCase("0")) {
                                    incitytxt = "In City";
                                } else if (incity.equalsIgnoreCase("1")) {
                                    incitytxt = "Out City";
                                }
                                if (roundtrip.equalsIgnoreCase("0")) {
                                    roundtriptxt = "One Way";
                                } else if (roundtrip.equalsIgnoreCase("1")) {
                                    roundtriptxt = "Return Trip";
                                }
                                textView1.setText(Html.fromHtml("Customer: <b>" + cust_name));
                                textView2.setText(Html.fromHtml("Mobile: <b>" + cust_number));
                                incityfrompayload.setText(Html.fromHtml("Trip Type:  <b>" + roundtriptxt + "</b>   <b>" + incitytxt + "</b>"));
                                slider.setSwipeDistance(.7f);
                                slider.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
                                    @Override
                                    public void onSwipeConfirm() {
                                        if (network) {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    slider.showResultIcon(true, false);
                                                    Intent io = new Intent(DrivarHomePageActivity.this, StartRideActivity.class);
                                                    io.putExtra("trip_id", trip_id);
                                                    startActivity(io);
                                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                                    editor.putString("firsttimedest", "");
                                                    editor.putString("firsttimestart", "");
                                                    editor.apply();
                                                }
                                            }, 2000);
                                        } else {
                                            Toast.makeText(DrivarHomePageActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                textView2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent call = new Intent(Intent.ACTION_DIAL);
                                        call.setData(Uri.parse("tel:" + cust_number));
                                        startActivity(call);
                                    }
                                });

                                address_one = address_one.replace("From: ", "");
                                address.setText(Html.fromHtml("Go to: <b>" + address_one));
                                try {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            String firsttimedest, firsttimestart;
                                            firsttimedest = sharedpreferences.getString("startaddss", "");
                                            firsttimestart = sharedpreferences.getString("endaddss", "");
                                            curlat = Latitudedefoult;
                                            curlong = Longitudedefoult;
                                            firsttimestart = String.valueOf(curlat) + "," + String.valueOf(curlong);
                                            Log.e("address_one", firsttimestart + " zxzxzxzx  " + firsttimedest);
                                            if (!firsttimestart.isEmpty() && !firsttimedest.isEmpty())
                                                settingmap(firsttimestart, firsttimedest);
                                        }
                                    }, 2000);

                                } catch (Exception e) {
                                    Log.e(TAG, "Exception " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Exception " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
        }
    }

    public void startMyService() {
        if (mService != null)
            mService.requestLocationUpdates();


        if (dutymode == 1) {

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("dutymode", dutymode);
            editor.putString("starttimestemp", String.valueOf(System.currentTimeMillis()));
            editor.apply();

            if (isMyServiceRunning(NormalService.class)) {
            } else {
                Intent background = new Intent(this, NormalService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startService(background);
                } else {
                    startService(background);
                }
            }
            setMyAlarm();
        }


    }

    private void setMyAlarm() {
//        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(getApplicationContext(), c);
//        intent.putExtra("locationSendingAlarm", true);
//        PendingIntent pendingIntent = PendingIntent.getService(this, 987654321, intent, 0);
//        try {
//            alarmManager.cancel(pendingIntent);
//        } catch (Exception e) {
//
//        }
//        int interval = (1000 * 60) * 3;
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    public void cancelAlaram() {

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                          CallAllDetails();
                        if (mService != null) {
                            mService.requestLocationUpdates();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                openPermission();
                                Log.e("OPenDialog", " openPermission 1043 -->> ");
                            }
                        }
                        if (locationTrack != null && mMap != null) {
                            LatLng latLng = new LatLng(locationTrack.getLatitude(), locationTrack.getLongitude());
                            if (latLng.latitude == 00.00 && latLng.longitude == 00.00)
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            mMap.setMyLocationEnabled(true);
                        }

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(DrivarHomePageActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("TAG", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }


    public void openPermission() {

        Button btnAccessPermision;
        LinearLayout lldd;

        dialog = new Dialog(DrivarHomePageActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.permission_dialog);

        btnAccessPermision = dialog.findViewById(R.id.btnAccess);
        lldd = dialog.findViewById(R.id.lldd);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;

        Log.e("OPenDialog", " screenHeight -->> " + screenHeight);
        Log.e("OPenDialog", " screenHeight -->> " + (screenHeight / 3));

// Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = lldd.getLayoutParams();
// Changes the height and width to the specified *pixels*
        params.height = (screenHeight / 3 + 100);
        lldd.setLayoutParams(params);

        btnAccessPermision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("OPenDialog", "Dilog butto  Click -->> ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        askforlocationpermision();
                    }
                }
//
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        CallAllDetails();
                        if (mService != null) {
                            mService.requestLocationUpdates();
                        }
                        Log.e("OPenDialog", " openPermission 1127 -->> ");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                openPermission();
                            }
                        }
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                return;
//                            }
//                        }
//                        mMap.setMyLocationEnabled(true);
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        displayLocationSettingsRequest(DrivarHomePageActivity.this);
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;

            case REQUESTFORMI:
                break;

            case IGNORE_BATTERY_OPTIMIZATION_REQUEST: {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, IGNORE_BATTERY_OPTIMIZATION_REQUEST);
                    } else {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            openPermission();
                            Log.e("OPenDialog", " openPermission 1160 -->> ");
                        } else {
                            CallAllDetails();
                        }
                    }
                }
            }
        }
    }


    void apicallingpois() {
        final SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        trip_id = sharedpreferences.getString("trip_id", trip_id);
        Log.e("trip_id", trip_id);
        final String logtokan1;
        logtokan1 = sharedpreferences.getString("tokan", "");
        progstart(DrivarHomePageActivity.this, "Loading...", "Loading...");
        StringRequest postRequest = new StringRequest(Request.Method.POST, finishtripotp,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response finishtripotp  : " + response);
                        progstop();
                        try {
                            JSONObject main = new JSONObject(response);
                            JSONObject succ = main.getJSONObject("success");
                            if (succ.has("is_auto_trip")) {
                                String is_auto_trip = succ.getString("is_auto_trip");
                                String endotp = succ.getString("endotp");
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("is_auto_endotp", endotp);
                                editor.apply();
                                Intent i = new Intent(DrivarHomePageActivity.this, FinishRideActivity.class);
                                i.putExtra("endotp", endotp);
                                startActivity(i);
                                finishAffinity();
                                return;
                            }
                        } catch (Exception e) {
                            DebugLog.printError(TAG, ""+ e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                        }

                        Intent i = new Intent(DrivarHomePageActivity.this, FinishRideActivity.class);
                        i.putExtra("endotp", "");
                        startActivity(i);
                        finishAffinity();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                        progstop();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan1);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("trip_id", trip_id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DrivarHomePageActivity.this);
        queue.add(postRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        SharedPreferences sharedpreferences = getSharedPreferences("Login", MODE_PRIVATE);
        dutymode = sharedpreferences.getInt("dutymode", dutymode);

        if (dutymode != -1) {
            if (dutymode == 1) {
                offduty.setBackground(getResources().getDrawable(R.drawable.selectedrounded));
                ondutty.setBackground(getResources().getDrawable(R.drawable.rounded));
                offduty.setTextColor(getResources().getColor(R.color.white));
                ondutty.setTextColor(getResources().getColor(R.color.bleck));
            } else {
                ondutty.setBackground(getResources().getDrawable(R.drawable.selectedrounded));
                offduty.setBackground(getResources().getDrawable(R.drawable.rounded));
                ondutty.setTextColor(getResources().getColor(R.color.white));
                offduty.setTextColor(getResources().getColor(R.color.bleck));
            }
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);

        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        if(payload != null && !payload.isEmpty()) {
            try {
                JSONObject trip = new JSONObject(payload);
                if (!trip.getString("status").equalsIgnoreCase("New_latlong")) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("dutymode", dutymode);
                    editor.putString("whaitingcustpayload", payload);
                    editor.putString("whaitingaddress_one", address_one);
                    editor.putString("whaitingmessageTO", messageTO);
                    editor.apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        super.onPause();
    }

    String logtokan, car_id;

    void dutyclass() {
        SharedPreferences sharedpreferences = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = sharedpreferences.getString("tokan", "");
        car_id = sharedpreferences.getString("car_id", "");

        StringRequest request = new StringRequest(Request.Method.POST, driveronduty, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DrivarHomePageActivity.this, "on duty api Response -->> "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.d("Authorization", "Bearer " + logtokan);
                params.put("Authorization", "Bearer " + logtokan);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("duty_mode", String.valueOf(dutymode));
                Log.d("duty_mode", String.valueOf(dutymode));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DrivarHomePageActivity.this);
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startMain);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
        } else if (id == R.id.daily_account) {
            startActivity(new Intent(DrivarHomePageActivity.this, DailyAccountActivity.class));
            finish();
        } else if (id == R.id.monthaly_account) {
            startActivity(new Intent(DrivarHomePageActivity.this, MonthlyAccountActivity.class));
            finish();
        } else if (id == R.id.settingride) {
            startActivity(new Intent(DrivarHomePageActivity.this, SelectLanguageActivity.class));
            finish();
        } else if (id == R.id.help) {
            startActivity(new Intent(DrivarHomePageActivity.this, HelpActivity.class));
            finish();
        } else if (id == R.id.trining) {
            startActivity(new Intent(DrivarHomePageActivity.this, TrainingActivity.class));
            finish();
        } else if (id == R.id.extratime) {
            startActivity(new Intent(DrivarHomePageActivity.this, SwitchCarActivity.class));
            finish();
        } else if (id == R.id.logout) {
            String isridestart = sharedpreferences.getString("isridestart", "");
            if (isridestart.equalsIgnoreCase("yes")) {
                Toast.makeText(DrivarHomePageActivity.this, getResources().getString(R.string.logoutstingfordriver), Toast.LENGTH_LONG).show();
            } else {
                logoutclass();
                if (mService != null)
                    mService.removeLocationUpdates();
            }
        } else if (id == R.id.profile) {
            Intent i = new Intent(DrivarHomePageActivity.this, DriverProfileActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logoutclass() {
        progstart(DrivarHomePageActivity.this, "Loading...", "Loading...");
        dutymode = 0;
        SharedPreferences sharedpreferences = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = sharedpreferences.getString("tokan", "");
        StringRequest request = new StringRequest(Request.Method.POST, logout, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, " Logout Response : " + response);
                SharedPreferences sharedpreferences;
                sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove("tokan");
                editor.remove("role");
                editor.remove("dutymode");
                editor.putBoolean("hasLoggedIn", false);
                editor.apply();
                NotificationUtils.clearNotifications(DrivarHomePageActivity.this);
                Intent i = new Intent(DrivarHomePageActivity.this, PhoneNumberVerifyActivity.class);
                startActivity(i);
                finish();
                progstop();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Volley error logout : " + error.getMessage());
                progstop();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("HH");
        String datetime = dateformat.format(c.getTime());

        locationTrack = new LocationTrack(DrivarHomePageActivity.this);
        if (locationTrack.canGetLocation()) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (mService != null)
                        mService.requestLocationUpdates();
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                if (mService != null)
                    mService.requestLocationUpdates();
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    LinearLayout relativeLayout1;
    ProSwipeButton slider;

    void conformride() {
        try {
            JSONObject trip = new JSONObject(payload);
            trip_id = trip.getString("trip_id");
            String cust_name = trip.getString("customer_name");
            cust_number = trip.getString("customer_phone");
            start_lng = trip.getString("start_lng");
            start_lat = trip.getString("start_lat");
            trip_id = trip.getString("trip_id");
            textView1 = findViewById(R.id.textView1);
            textView2 = findViewById(R.id.textView2);
            address = findViewById(R.id.textView3);
            textView1.setText(Html.fromHtml("Customer: <b>" + cust_name));
            textView2.setText(Html.fromHtml("Mobile: <b>" + cust_number));
            slider.setSwipeDistance(.7f);
            SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("trip_id", trip_id);
            editor.putString("cust_name_driver", cust_name);
            editor.putString("cust_number_driver", cust_number);
            editor.apply();

            slider.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
                @Override
                public void onSwipeConfirm() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editor.putString("firsttimedest", "");
                            editor.putString("firsttimestart", "");
                            editor.apply();
                            slider.showResultIcon(true, false);
                            Intent io = new Intent(DrivarHomePageActivity.this, StartRideActivity.class);
                            io.putExtra("trip_id", trip_id);
                            startActivity(io);
                        }
                    }, 2000);
                }
            });

            textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent call = new Intent(Intent.ACTION_DIAL);
                    call.setData(Uri.parse("tel:" + cust_number));
                    startActivity(call);
                }
            });
            address_one = address_one.replace("From: ", "");
            final SharedPreferences.Editor editor1 = sharedpreferences.edit();
            editor1.putString("cust_address", address_one);
            editor1.apply();
            address.setText(Html.fromHtml("Go to: <b>" + address_one));
            relativeLayout1 = findViewById(R.id.relativeLayout1);
        } catch (Exception e) {
            DebugLog.printError(TAG, ""+ e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
        }

        progstart(DrivarHomePageActivity.this, "Loading...", "Loading...");
        final String logtokan;
        SharedPreferences sharedpreferences = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = sharedpreferences.getString("tokan", "");
        StringRequest request = new StringRequest(Request.Method.POST, acceptride, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(TAG, "AcceptRide Response : " + response);
                    if (relativeLayout1.getVisibility() != View.VISIBLE) {
                        relativeLayout1.setVisibility(View.VISIBLE);
                        Log.e(TAG, "Check status visible  ===>>>> 1609");
                    }
                    ls2.setVisibility(View.VISIBLE);

                    JSONObject main = new JSONObject(response);
                    JSONObject success = main.getJSONObject("success");
                    String stetus = success.getString("status");
                    if (stetus.equalsIgnoreCase("already_car_assigned")) {
                        Toast.makeText(DrivarHomePageActivity.this, success.getString("msg"), Toast.LENGTH_LONG).show();
                        textView1.setText(success.getString("msg"));
                        textView1.setTextColor(getResources().getColor(R.color.slidred));
                        textView2.setVisibility(View.GONE);
                        address.setVisibility(View.GONE);
                        slider.setVisibility(View.GONE);
                        incityfrompayload.setVisibility(View.GONE);
                        payload = "";
                        address_one = "";
                        messageTO = "";
                        trip_id = "";

                        SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("trip_id", "");
                        editor.putString("cust_name_driver", "");
                        editor.putString("cust_number_driver", "");
                        editor.putString("whaitingcustpayload", "");
                        editor.putString("whaitingaddress_one", "");
                        editor.putString("whaitingmessageTO", "");
                        editor.putString("isridestart", "yes");
                        editor.apply();
                        progstop();
                        Intent i = new Intent(DrivarHomePageActivity.this, TripGoneToDrivarActivity.class);
                        startActivity(i);
                        finish();
                        return;
                    } else if (stetus.equalsIgnoreCase("trip_gone_to_other_derive")) {
                        Toast.makeText(DrivarHomePageActivity.this, success.getString("msg"), Toast.LENGTH_LONG).show();
                        textView1.setText(success.getString("msg"));
                        textView1.setTextColor(getResources().getColor(R.color.slidred));
                        textView2.setVisibility(View.GONE);
                        address.setVisibility(View.GONE);
                        slider.setVisibility(View.GONE);
                        incityfrompayload.setVisibility(View.GONE);
                        payload = "";
                        address_one = "";
                        messageTO = "";
                        trip_id = "";

                        SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("trip_id", "");
                        editor.putString("cust_name_driver", "");
                        editor.putString("cust_number_driver", "");
                        editor.putString("whaitingcustpayload", "");
                        editor.putString("whaitingaddress_one", "");
                        editor.putString("whaitingmessageTO", "");
                        editor.apply();
                        progstop();
                        Intent i = new Intent(DrivarHomePageActivity.this, TripGoneToDrivarActivity.class);
                        startActivity(i);
                        finish();
                        return;
                    }

                    String user_id = success.getString("user_id");
                    SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("customer_id", user_id);
                    editor.apply();
                    String dest = start_lat + "," + start_lng;
                    String start = Latitudedefoult + "," + Longitudedefoult;

                    if (start.equals("0.0,0.0") || dest.equals("0.0,0.0")) {
                        start = sharedpreferences.getString("locstart_latitude", "0.0,0.0") + "," + sharedpreferences.getString("locstart_longitude", "0.0,0.0");
                        dest = sharedpreferences.getString("locend_latitude", "0.0,0.0") + "," + sharedpreferences.getString("locend_longitude", "0.0,0.0");
                        settingmap(start, dest);
                    } else {
                        settingmap(start, dest);
                    }
                    editor.putString("firsttimedest", dest);
                    editor.putString("firsttimestart", start);
                    editor.apply();
                } catch (Exception e) {
                    DebugLog.printError(TAG, ""+ e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                }
                progstop();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "" + error);
                relativeLayout1.setVisibility(View.GONE);
                ls2.setVisibility(View.GONE);
                Toast.makeText(DrivarHomePageActivity.this, "Error showing Please Contact Admin for this", Toast.LENGTH_LONG).show();
                progstop();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan);
                Log.e("Authorization", "Bearer " + logtokan);
                return params;
            }

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("car_id", car_id);
                params.put("trip_id", trip_id);
                Log.e(TAG, car_id);
                Log.e(TAG, trip_id);

//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        //
//                        locationTrack2 = new LocationTrack(DrivarHomePageActivity.this);
//                    }
//                }else{
//                    locationTrack2 = new LocationTrack(DrivarHomePageActivity.this);
//                }

                if (locationTrack2.getLongitude() != 00 && locationTrack2.getLongitude() != 00) {
                    params.put("accept_lat", String.valueOf(locationTrack.getLatitude()));
                    params.put("accept_lon", String.valueOf(locationTrack.getLongitude()));
                } else {
                    if (NormalService.curlat != null && NormalService.curlong != null) {
                        params.put("accept_lat", NormalService.curlat);
                        params.put("accept_lon", NormalService.curlong);
                    } else {
                        params.put("accept_lat", "00.00");
                        params.put("accept_lon", "00.00");
                    }
                }
                Log.e(TAG, "AcceptRide Param -->> " + new Gson().toJson(params));
                return params;
            }
        };
        if (queue == null) {
            queue = Volley.newRequestQueue(DrivarHomePageActivity.this);
        }
        queue.add(request);
    }


    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    public void settingmap(String latlon1, String latlon2) {

        SharedPreferences preferencesNavigate = getSharedPreferences("NavigateLocation", Context.MODE_PRIVATE);
        if (latlon1 != null && !latlon1.isEmpty() && !latlon1.equals("0.0,0.0") && !latlon1.equals("00.00,00.00") && latlon2 != null && !latlon2.isEmpty() && !latlon2.equals("0.0,0.0") && !latlon2.equals("00.00,00.00")) {
            latlon1 = preferencesNavigate.getString("locstart_latitude", "0.0") + "," + preferencesNavigate.getString("locstart_longitude", "0.0");
            latlon2 = preferencesNavigate.getString("locend_latitude", "0.0") + "," + preferencesNavigate.getString("locend_longitude", "0.0");
        }
        if (!latlon1.isEmpty() && !latlon2.isEmpty() && !latlon1.equals("0.0,0.0") && !latlon2.equals("0.0,0.0") && !latlon1.equals("00.00,00.00") && !latlon2.equals("00.00,00.00")) {
            MarkerPoints.clear();
            mMap.clear();
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.start);
            BitmapDescriptor icon1 = BitmapDescriptorFactory.fromResource(R.drawable.end);
            SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("cust_start_driver", latlon1);
            editor.putString("cust_end_driver", latlon2);
            editor.apply();
            String[] latlong1 = latlon1.split(",");
            final double latitude1 = Double.parseDouble(latlong1[0]);
            final double longitude1 = Double.parseDouble(latlong1[1]);
            origin = new LatLng(latitude1, longitude1);
            try {

                mMap.addMarker(new MarkerOptions().position(origin).title("Start Ride ").icon(icon));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12.2f));
                String[] latlong2 = latlon2.split(",");
                final double latitude2 = Double.parseDouble(latlong2[0]);
                final double longitude2 = Double.parseDouble(latlong2[1]);
                dest = new LatLng(latitude2, longitude2);
                mMap.addMarker(new MarkerOptions().position(dest)
                        .title("End Ride").icon(icon1));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
                MarkerPoints.add(origin);
                navlong = longitude2;
                navlat = latitude2;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(origin);
                builder.include(dest);
                LatLngBounds bounds = builder.build();
                int padding = 200;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);

            } catch (Exception e) {
                Log.e(TAG, " IOException  SettingMap : " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
            }
        }
    }

    @Override
    public void networkAvailable() {
        network = Boolean.TRUE;
    }

    @Override
    public void networkUnavailable() {
        network = Boolean.FALSE;
    }

    @Override
    protected void onDestroy() {

        this.mWakeLock.release();
        active = false;

        if (recieverIsMockLocation != null) {
            unregisterReceiver(recieverIsMockLocation);
        }

        connectivityReceiver.removeListener(this);
        this.unregisterReceiver(connectivityReceiver);

        SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("isMock", true);
        editor.putBoolean("diload", true);
        editor.apply();

        super.onDestroy();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(DrivarHomePageActivity.this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0);

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (!sharedpreferences.getString("isridestart", "").equalsIgnoreCase("yes")) {
                        //move map camera
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.2f));

                        Latitudedefoult = location.getLatitude();
                        Longitudedefoult = location.getLongitude();

                        final SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("curlatitude", "" + location.getLatitude());
                        editor.putString("curlongtitude", "" + location.getLongitude());
                        editor.apply();

                        curlat = Latitudedefoult;
                        curlong = Longitudedefoult;

                        getbearing = mLastLocation.getBearing();
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("Current Position");
                    }
                    try {
                        if (whaitingcustpayload.equalsIgnoreCase("")) {
                            String dest = start_lat + "," + start_lng;
                            String start = Latitudedefoult + "," + Longitudedefoult;
                        }
                    } catch (Exception e) {
                        DebugLog.printError(TAG, ""+ e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                    }
                } catch (Exception e) {
                    DebugLog.printError(TAG, ""+ e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                }
            }
        }
    }

    public class RecieverIsMockLocation extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

//            try {
//                if (active) {
//                    final Dialog dialog = new Dialog(DrivarHomePageActivity.this);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setCancelable(false);
//                    dialog.setContentView(R.layout.activity_custom_dialog_class);
//                    TextView dialogButton = (TextView) dialog.findViewById(R.id.txtMessage);
//                    TextView imageView = (TextView) dialog.findViewById(R.id.txtok);
//                    dialogButton.setText("Please Remove Fake Location App and try Again !");
//                    imageView.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                            SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedpreferences.edit();
//                            editor.putBoolean("isMock", true);
//                            editor.putBoolean("diload", true);
//                            editor.apply();
//                            if (recieverIsMockLocation != null) {
//                                unregisterReceiver(recieverIsMockLocation);
//                                recieverIsMockLocation = null;
//                            }
//                            activity.stopService(new Intent(activity, NormalService.class));
//                            activity.finishAffinity();
//                        }
//                    });
//                    SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedpreferences.edit();
//                    editor.putBoolean("isMock", false);
//                    editor.putBoolean("diload", false);
//                    editor.apply();
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                    dialog.show();
//                }
//            } catch (Exception e) {
//            }
        }
    }


    public void userNavigate() {
        try {
            SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences preferencesNavigate = getSharedPreferences("NavigateLocation", Context.MODE_PRIVATE);
            if (sharedpreferences.getString("isStart", "1").equals("1")) {
                if (!preferencesNavigate.getString("locstart_latitude", "0.0").equals("0.0") && !preferencesNavigate.getString("locstart_longitude", "0.0").equals("0.0")) {
                    String locstart_latitude = preferencesNavigate.getString("locstart_latitude", String.valueOf(navlat));
                    String locstart_longitude = preferencesNavigate.getString("locstart_longitude", String.valueOf(navlong));
                    if (!locstart_latitude.equals("0.0") && !locstart_longitude.equals("0.0")) {
                        Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + locstart_latitude + "," + locstart_longitude));
                        navigation.setPackage("com.google.android.apps.maps");
                        startActivity(navigation);
                    } else {
                        Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + navlat + "," + navlong));
                        navigation.setPackage("com.google.android.apps.maps");
                        startActivity(navigation);
                    }
                } else {
                    Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + navlat + "," + navlong));
                    navigation.setPackage("com.google.android.apps.maps");
                    if (navigation.resolveActivity(getPackageManager()) != null) {
                        startActivity(navigation);
                    }
                }
            } else {

                if (!preferencesNavigate.getString("locend_latitude", "0.0").equals("0.0") && !preferencesNavigate.getString("locend_longitude", "0.0").equals("0.0")) {
                    String locend_latitude = preferencesNavigate.getString("locend_latitude", "0.0");
                    String locend_longitude = preferencesNavigate.getString("locend_longitude", "0.0");
                    if (!locend_latitude.equals("0.0") && !locend_longitude.equals("0.0")) {
                        Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + locend_latitude + "," + locend_longitude));
                        navigation.setPackage("com.google.android.apps.maps");
                        startActivity(navigation);
                    } else {
                        Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + navlat + "," + navlong));
                        navigation.setPackage("com.google.android.apps.maps");
                        startActivity(navigation);
                    }
                }
            }
        } catch (Exception e) {
            DebugLog.printError(TAG, ""+ e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
        }
    }

    public void callapiforgettingstatus(final String tripiID) {
        progstart(DrivarHomePageActivity.this, "Loading...", "Please wait..");
        StringRequest postRequest = new StringRequest(Request.Method.POST, gettripstatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progstop();
                        Log.e(TAG, "Response Trip Status ==>> " + response);
                        try {
                            JSONObject responce = new JSONObject(response);
                            JSONObject success = responce.getJSONObject("success");
                            if (success.getString("status").equalsIgnoreCase("success")) {

                                if (success.getString("trip_status").equalsIgnoreCase("waiting_for_driver")) {
                                    Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + success.getString("start_lat") + "," + success.getString("start_long")));
                                    navigation.setPackage("com.google.android.apps.maps");
                                    startActivity(navigation);
                                } else if (success.getString("status").equalsIgnoreCase("started")) {
                                    Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + success.getString("end_lat") + "," + success.getString("end_long")));
                                    navigation.setPackage("com.google.android.apps.maps");
                                    startActivity(navigation);
                                } else {
                                    userNavigate();
                                }
                            } else {
                                userNavigate();
                            }
                        } catch (Exception e) {
                            userNavigate();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progstop();
                        userNavigate();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
                logtokan = prefs.getString("tokan", "");
                params.put("Authorization", "Bearer " + logtokan);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("trip_id", trip_id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }
}
