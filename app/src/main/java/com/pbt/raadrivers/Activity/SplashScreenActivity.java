package com.pbt.raadrivers.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.pbt.raadrivers.Dialog.CustomDialogClass;
import com.pbt.raadrivers.Dialog.UpdateAppDialog;
import com.pbt.raadrivers.Fonts.CButton;
import com.pbt.raadrivers.Notification.MyFirebaseMessagingService;
import com.pbt.raadrivers.Other.AppVersionChecker;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.ConnectivityReceiver;
import com.pbt.raadrivers.Utils.CustomExceptionHandler;
import com.pbt.raadrivers.Utils.DatabaseHelper;
import com.pbt.raadrivers.Utils.MyApplication;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;
import static java.lang.System.exit;

public class SplashScreenActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, AppConstant {
    private static int SPLASH_TIME_OUT = 4500;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    LinearLayout internetlayout;
    CButton close;
    String role, trip_id;
    String logtokan, ipv4;
    String referLink = "0";
    Context context;
    Activity activity;
    String TAG = "SplashScreenActivity";

    private DatabaseHelper db;
    int MY_PERMISSIONS = 1020;

    private Boolean network = Boolean.TRUE;
    private ConnectivityReceiver connectivityReceiver;

    private static final String[] PERMISSIONS = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    Dialog dialog;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        connectivityReceiver = new ConnectivityReceiver();
        connectivityReceiver.addListener(this);
        this.registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                SharedPreferences FireBaseToken = getSharedPreferences("FireBaseToken", MODE_PRIVATE);
                SharedPreferences.Editor editorFireBaseToken = FireBaseToken.edit();
                if (!instanceIdResult.getToken().isEmpty())
                    editorFireBaseToken.putString("regId", instanceIdResult.getToken());
                else
                    editorFireBaseToken.putString("regId", "151SplashEMPTY");

                editorFireBaseToken.apply();
                SharedPreferences pref;
                pref = getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                if (!instanceIdResult.getToken().isEmpty())
                    editorFireBaseToken.putString("regId", instanceIdResult.getToken());
                else
                    editorFireBaseToken.putString("regId", "162SplashEMPTY");
                editor.apply();

                Log.e(TAG, "Firebase Token :" + instanceIdResult.getToken());
            }
        });


        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        context = getApplicationContext();
        db = new DatabaseHelper(this);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/RaadarbarCrash", "", getApplicationContext()));
        }

        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            CustomExceptionHandler.sendToServer(this);

        MyFirebaseMessagingService.activity = SplashScreenActivity.this;
        activity = SplashScreenActivity.this;

        MyApplication.setCurrentActivity(this);
        final SharedPreferences prefs = getSharedPreferences("AutoStartServic", MODE_PRIVATE);

        if ("samsung".equalsIgnoreCase(Build.MANUFACTURER) || "Xiaomi".equalsIgnoreCase(Build.MANUFACTURER) || "vivo".equalsIgnoreCase(Build.MANUFACTURER) || "oppo".equalsIgnoreCase(Build.MANUFACTURER)) {
        } else {
            if (prefs != null && prefs.getString("isAllow", "false").equals("false")) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("isAllow", "true");
                editor.apply();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }

        if ("samsung".equalsIgnoreCase(Build.MANUFACTURER) || "Xiaomi".equalsIgnoreCase(Build.MANUFACTURER) || "vivo".equalsIgnoreCase(Build.MANUFACTURER) || "oppo".equalsIgnoreCase(Build.MANUFACTURER)) {
            if (prefs != null && prefs.getString("isAllow", "false").equals("false")) {
                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_autostart);
                TextView dialogButton = (TextView) dialog.findViewById(R.id.txtok);
                ImageView imageView = (ImageView) dialog.findViewById(R.id.img_war);
                if ("Xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {
                    imageView.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.mimobile));
                } else {
                    imageView.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.overlay_permi));
                }

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        if ("vivo".equalsIgnoreCase(Build.MANUFACTURER)) {
                            autoLaunchVivo(getApplicationContext());
                        } else if ("oppo".equalsIgnoreCase(Build.MANUFACTURER)) {
                            initOPPO();
                        } else if ("Xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {
                            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                            intent.setClassName("com.miui.securitycenter",
                                    "com.miui.permcenter.permissions.PermissionsEditorActivity");
                            intent.putExtra("extra_pkgname", getPackageName());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName()));
                            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                        }
                        SplashScreenActivity.this.finishAffinity();
                    }
                });
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("isAllow", "true");
                editor.apply();
                dialog.show();
            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                      openPermission();
//                    }else{
//                        move();//call here
//                    }

//                }else{
                    move();
//                }
            }
        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    openPermission();
//                }else{
                    move();//call here
//                }`
//            }else{
//                move();
//            }`
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                move();
                break;
        }
    }

    public void openPermission() {

        Button btnAccessPermision;
        LinearLayout lldd;

        dialog = new Dialog(SplashScreenActivity.this);
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

        Log.e("OPenDialog", " screenHeight -->> "+screenHeight);
        Log.e("OPenDialog", " screenHeight -->> "+(screenHeight/3));

// Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = lldd.getLayoutParams();
// Changes the height and width to the specified *pixels*
        params.height = (screenHeight/3+100);
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
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void askforlocationpermision() {
        ActivityCompat.requestPermissions(SplashScreenActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    public void move() {

        try {
            String device_data = getDeviceInfo("   &  ");
            SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        internetlayout = findViewById(R.id.internetlayout);
        close = findViewById(R.id.close);
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        role = prefs.getString("role", "");
        SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("start_address", "");
        editor.putString("end_address", "");
        editor.apply();

        if (network) {
            logtokan = prefs.getString("tokan", "");
            if (logtokan.equalsIgnoreCase("")) {
                if ("driver".equalsIgnoreCase(role)) {
                    trip_id = prefs.getString("trip_id", "");
                } else {
                    trip_id = prefs.getString("KEY_PREF_TRIP_ID", "");
                }
                if (!trip_id.equalsIgnoreCase("")) {
                    //for Cheking application or trip stetus
                    if (network) {
                        Log.e(TAG," callapiforgettingstatus ===>> 335");
                        callapiforgettingstatus();
                    } else {
                        internetlayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    main();
                }
            } else {
                new CheckStatus(SplashScreenActivity.this, "", role).execute();
            }
            getlink();
            String haskeyfacebook = printKeyHash(SplashScreenActivity.this);
        } else {
            internetlayout.setVisibility(View.VISIBLE);
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initOPPO() {
        try {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity"));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        } catch (Exception e) {
            try {
                Intent intent = new Intent("action.coloros.safecenter.FloatWindowListActivity");
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception ee) {
                try {
                    Intent i = new Intent("com.coloros.safecenter");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity"));
                    startActivity(i);
                } catch (Exception e1) {
                }
            }
        }
    }

    private static void autoLaunchVivo(Context context) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception ex) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.iqoo.secure",
                            "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception exx) {
                }
            }
        }
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            String packageName = context.getApplicationContext().getPackageName();
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
            }
        } catch (PackageManager.NameNotFoundException e1) {
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }
        return key;
    }

    private void getlink() {
        try {
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            try {
                                Uri deepLink = null;
                                if (pendingDynamicLinkData != null) {
                                    deepLink = pendingDynamicLinkData.getLink();
                                    referLink = deepLink.toString();
                                    referLink = referLink.substring(referLink.lastIndexOf("/") + 1);
                                    SharedPreferences sharedpreferences;
                                    sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("refer_id", referLink);
                                    editor.apply();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Exception : " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                                referLink = "0";
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            referLink = "0";
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
            referLink = "0";
        }
    }

    private void call_api_firsttrip() {
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        StringRequest postRequest = new StringRequest(Request.Method.POST, firsttrip,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, "response" + response);
                        try {
                            JSONArray array = new JSONArray(response);
                            if (array.length() == 0) {
                                main();
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    int result = object.getInt("id");
                                    trip_id = String.valueOf(result);
                                    if (!trip_id.equalsIgnoreCase("")) {
                                        if (network)
                                            callapiforgettingstatus();
                                        else
                                            internetlayout.setVisibility(View.VISIBLE);
                                    } else
                                        main();
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Exception : " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences prefsFireBase = getSharedPreferences("FireBaseToken", MODE_PRIVATE);
                params.put("fireregId", prefsFireBase.getString("regId", SPLASCREEN));
                updateFireBaseToken(prefsFireBase.getString("regId", SPLASCREEN));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);

    }

    public void updateFireBaseToken(String token) {
        SharedPreferences pref;
        pref = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (!token.isEmpty())
            editor.putString("regId", token);
        else
            editor.putString("regId", "572SplashEMPTY");
        editor.apply();
    }


    public String getDeviceInfo(String p_seperator) throws Throwable {
        String m_data = "";
        StringBuilder m_builder = new StringBuilder();
        m_builder.append("RELEASE   =  " + android.os.Build.VERSION.RELEASE + p_seperator);
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = getLocalIpAddress();
        m_builder.append("IP Address  =   " + ip + p_seperator);
        String userAgent = System.getProperty("http.agent");
        m_builder.append("USer Agent   =  " + userAgent + p_seperator);
        m_builder.append("DEVICE  =   " + android.os.Build.DEVICE + p_seperator);
        m_builder.append("MODEL   =  " + android.os.Build.MODEL + p_seperator);
        m_builder.append("PRODUCT  =   " + android.os.Build.PRODUCT + p_seperator);
        m_builder.append("BRAND   =  " + android.os.Build.BRAND + p_seperator);
        m_builder.append("USER   =  " + android.os.Build.USER + p_seperator);
        m_builder.append("HOST   =  " + android.os.Build.HOST + p_seperator);
        m_data = m_builder.toString();
        return m_data;
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    // for getting IPV4 format
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {
                        String ip = inetAddress.getHostAddress().toString();
                        return ip;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
        }
        return null;
    }

    public void callapiforgettingstatus() {

        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        progstart(SplashScreenActivity.this, "Loading...", "Please wait while fetching your trip details...");

        StringRequest postRequest = new StringRequest(Request.Method.POST, gettripstatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response" + response);
                        try {
                            JSONObject responce = new JSONObject(response);
                            JSONObject success = responce.getJSONObject("success");
                            String trip_status = success.getString("trip_status");
                            Log.e(TAG, String.valueOf(trip_status.equalsIgnoreCase("waiting_for_driver")));
                            if (trip_status.equalsIgnoreCase("just_booked") || trip_status.equalsIgnoreCase("waiting_for_driver") || trip_status.equalsIgnoreCase("started")) {
                                SharedPreferences sharedpreferences;
                                sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("isridestart", "yes");
                                String start = success.getString("start_lat") + "," + success.getString("start_long");
                                String end = success.getString("end_lat") + "," + success.getString("end_long");
                                editor.putString("startaddss", start);
                                editor.putString("endaddss", end);
                                editor.putString("trip_id", trip_id);
                                editor.putString("Driver_namecws", success.getString("driver_first_name"));
                                editor.putString("Driver_id", success.getString("driver_id"));
                                editor.putString("phonecws", success.getString("driver_phone"));
                                editor.putString("driver_latcws", success.getString("last_lat"));
                                editor.putString("driver_lngcws", success.getString("last_long"));
                                editor.putString("trip_start_latcws", success.getString("start_lat"));
                                editor.putString("trip_end_latcws", success.getString("start_long"));
                                editor.putString("trip_start_otpcws", success.getString("start_otp"));
                                editor.putString("carnumbercws", success.getString("carnumber"));
                                editor.putString("driver_photocws", success.getString("image"));
                                editor.putString("car_namecws", success.getString("carname"));
                                editor.putString("car_image_textcws", success.getString("carimage"));
                                editor.putString("trip_status", success.getString("trip_status"));
                                editor.putString("start_point", success.getString("start_point"));
                                editor.putString("end_point", success.getString("end_point"));
                                editor.putString("cust_address", success.getString("start_point"));
                                editor.putString("STRATKM", success.getString("start_km"));
                                editor.putString("cust_name_driver", success.getString("customer_first_name"));
                                editor.putString("cust_number_driver", success.getString("customer_phone"));
                                editor.putString("incity", success.getString("incity"));
                                editor.putString("roundtrip", success.getString("roundtrip"));
                                editor.apply();
                                editor.putString("startaddss", start);
                                editor.putString("endaddss", end);
                                Log.d(TAG, "Driver_namecws " + success.getString("driver_first_name"));
                                Log.d(TAG, "phonecws " + success.getString("driver_phone"));
                                Log.d(TAG, "cust_name_driver " + success.getString("customer_first_name"));
                                Log.d(TAG, "cust_number_driver " + success.getString("customer_phone"));
                                Log.d(TAG, "driver_latcws " + success.getString("last_lat"));
                                Log.d(TAG, "driver_lngcws " + success.getString("last_long"));
                                Log.d(TAG, "trip_start_latcws " + success.getString("start_lat"));
                                Log.d(TAG, "trip_end_latcws " + success.getString("start_long"));
                                Log.d(TAG, "trip_start_otpcws " + success.getString("start_otp"));
                                Log.d(TAG, "carnumbercws " + success.getString("carnumber"));
                                Log.d(TAG, "driver_photocws  " + success.getString("image"));
                                Log.d(TAG, "car_namecws  " + success.getString("carname"));
                                Log.d(TAG, "car_image_textcws   " + success.getString("carimage"));
                            } else {
                                SharedPreferences sharedpreferences;
                                sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("finalBill", "");
                                editor.putString("trip_status", "");
                                editor.putString("isridestart", "");
                                editor.putString("startaddss", "");
                                editor.putString("endaddss", "");
                                editor.putString("whaitingaddress_one", "");
                                editor.putString("whaitingcustpayload", "");
                                editor.putString("cust_name_driver", "");
                                editor.putString("cust_number_driver", "");
                                editor.putString("page", "");
                                editor.putString("is_autop_tripfrompayload", "");
                                editor.putString("start_otp1frompayload", "");
                                editor.putString("message", "");
                                editor.putString("payload", "");
                                editor.putString("is_auto_endotp", "");
                                editor.remove("KEY_PREF_TRIP_ID");
                                editor.remove("trip_id");
                                editor.putString("Driver_namecws", "");
                                editor.putString("phonecws", "");
                                editor.putString("cash123", "");
                                editor.putString("driver_latcws", "");
                                editor.putString("driver_lngcws", "");
                                editor.putString("trip_start_latcws", "");
                                editor.putString("trip_end_latcws", "");
                                editor.putString("trip_start_otpcws", "");
                                editor.putString("carnumbercws", "");
                                editor.putString("driver_photocws", "");
                                editor.putString("car_namecws", "");
                                editor.putString("car_image_textcws", "");
                                editor.putString("finalstartlatcws", "");
                                editor.putString("finalstartlongcws", "");
                                editor.putString("finalendlatcws", "");
                                editor.putString("finalendlongcws", "");
                                editor.remove("finalcws");
                                editor.putString("message1cws", "");
                                editor.remove("isridestart");
                                editor.putString("finalBill", "");
                                editor.putString("endotpnot", "");
                                editor.putString("messagenoti", "");
                                editor.putString("page", "");
                                editor.putString("message", "");
                                editor.putString("payload", "");
                                editor.apply();
                            }
                            main();
                            progstop();
                        } catch (Exception e) {
                            Log.e(TAG, "Exception : " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e(TAG, "Error.Response" + error.toString());
                        progstop();
                        main();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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

    public void main() {
        try {
            SharedPreferences sharedpreferences;
            sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor123 = sharedpreferences.edit();
            editor123.putString("favriteiconchangesorce", "");
            editor123.putString("favriteiconchangedest", "");
            editor123.apply();
            String page = sharedpreferences.getString("page", "");
            String message = sharedpreferences.getString("message", "");
            String payload = sharedpreferences.getString("payload", "");

            Log.e(TAG, "Splesh Scrren  " + page);
            if (page.equalsIgnoreCase("driver")) {
                Log.e(TAG, "PAGE CUST PAGE CUST ");
                sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("page", "");
                editor.putString("message", "");
                editor.putString("payload", "");
                editor.apply();
                Log.e(TAG, " SplashSCreen  Accept Ride Activity Start -->> ");
                Intent i = new Intent(SplashScreenActivity.this, AcceptRideActivity.class);
                i.putExtra("message", message);
                i.putExtra("payload", payload);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                return;
            }
            sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("placefrom");
            editor.remove("end_address");
            editor.remove("start_address");
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (network) {
                    try {
                        AppVersionChecker appVersionChecker = new AppVersionChecker(SplashScreenActivity.this);
                        String versionUpdated = appVersionChecker.execute().get();
                        Log.e(TAG, "version code is" + versionUpdated);
                        PackageInfo packageInfo = null;
                        try {
                            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String version_name = packageInfo.versionName;
                        if (versionUpdated != null && !versionUpdated.isEmpty() && version_name != null && !version_name.isEmpty()) {
                            Log.i("SplashScreenActivity", "   Playstore Version -->> " + versionUpdated + " Android Version  -->>" + version_name);
                            if (Double.valueOf(version_name) < Double.valueOf(versionUpdated)) {
                                String packageName = getApplicationContext().getPackageName();//
                                UpdateAppDialog updateMeeDialog = new UpdateAppDialog();
                                updateMeeDialog.showDialogAddRoute(SplashScreenActivity.this, packageName);
                            } else {
                                SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
                                String role = prefs.getString("role", "");
                                boolean hasLoggedIn = prefs.getBoolean("hasLoggedIn", false);
                                Log.e("LoggedIn", "   " + String.valueOf(hasLoggedIn) + "   " + role);
                                if (hasLoggedIn) {
                                    if (role.equalsIgnoreCase("driver")) {
                                        Intent i = new Intent(SplashScreenActivity.this, DrivarHomePageActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                        return;
                                    }
                                } else {
                                    Intent i = new Intent(SplashScreenActivity.this, PhoneNumberVerifyActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                }

                                SharedPreferences prefs1 = getSharedPreferences("languages", MODE_PRIVATE);
                                String selected = prefs1.getString("Selectedla", "");
                                if (!selected.equalsIgnoreCase("")) {
                                    Intent i = new Intent(SplashScreenActivity.this, PhoneNumberVerifyActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    return;
                                } else {
                                    Intent i = new Intent(SplashScreenActivity.this, PhoneNumberVerifyActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    return;
                                }
                            }
                        } else {
                            SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
                            String role = prefs.getString("role", "");
                            boolean hasLoggedIn = prefs.getBoolean("hasLoggedIn", false);
                            Log.e("LoggedIn", "   " + String.valueOf(hasLoggedIn) + "   " + role);
                            if (hasLoggedIn) {
                                if (role.equalsIgnoreCase("driver")) {
                                    Intent i = new Intent(SplashScreenActivity.this, DrivarHomePageActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                    return;
                                }
                            } else {
                                Intent i = new Intent(SplashScreenActivity.this, PhoneNumberVerifyActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }

                            SharedPreferences prefs1 = getSharedPreferences("languages", MODE_PRIVATE);
                            String selected = prefs1.getString("Selectedla", "");
                            if (!selected.equalsIgnoreCase("")) {
                                Intent i = new Intent(SplashScreenActivity.this, PhoneNumberVerifyActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                return;
                            } else {
                                Intent i = new Intent(SplashScreenActivity.this, PhoneNumberVerifyActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                return;
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception : " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());

                    }
                } else {
                    internetlayout.setVisibility(View.VISIBLE);
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        super.onDestroy();
        connectivityReceiver.removeListener(this);
        this.unregisterReceiver(connectivityReceiver);
    }

    public class CheckStatus extends AsyncTask<String, String, String> {
        Activity activity;
        String tripId;
        String role;

        public CheckStatus(Activity activity, String tripId, String role) {
            this.activity = activity;
            this.tripId = tripId;
            this.role = role;
        }

        @Override
        protected String doInBackground(String... strings) {
            StringRequest postRequest = new StringRequest(Request.Method.POST, checkblock,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e(TAG, "Response : " + response);

                            try {
                                if (!response.isEmpty() && response.trim().equals("0")) {
                                    call_api_firsttrip();
                                } else {
                                    CustomDialogClass cdd = new CustomDialogClass(activity, "You are blocked.Contact to Raa cab service", "checkStatus");
                                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                    cdd.show();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Exception : " + e.getMessage() + " Line : " + e.getStackTrace()[0].getLineNumber());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + logtokan);
                    Log.d("Authorization", "Bearer " + logtokan);
                    return params;
                }

                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    PackageInfo packageInfo = null;
                    try {
                        packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    String version_name = packageInfo.versionName;

                    String manufacturer = Build.MANUFACTURER;
                    String model = Build.MODEL;
                    String modelname = manufacturer + " " + model;
                    String osversion = Build.VERSION.RELEASE;
                    params.put("m_model", modelname);
                    params.put("m_os", osversion);
                    params.put("m_version", version_name);
                    Log.e(TAG, " " + TAG + " CheckStatus  param --->>>> " + new Gson().toJson(params));
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(activity.getApplicationContext());
            queue.add(postRequest);
            return null;
        }
    }
}
