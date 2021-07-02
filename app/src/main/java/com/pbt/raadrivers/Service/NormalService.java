package com.pbt.raadrivers.Service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.pbt.raadrivers.Activity.PhoneNumberVerifyActivity;
import com.pbt.raadrivers.BuildConfig;
import com.pbt.raadrivers.Fonts.CTextView;
import com.pbt.raadrivers.Notification.NotificationUtils;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.Common;
import com.pbt.raadrivers.Utils.ConnectivityReceiver;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



    public class NormalService extends Service implements LocationListener, AppConstant {

    private String modelname;
    private String osversion;


    public static String ISMOCKLOCATION = "ISMOCKLOCATION";

    private static Boolean isMock = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    SharedPreferences prefs;
    String starttimestemp, userid, curranttimestemp, formattedDate, formattedtime;
    String logtokan;
    String fireregId;
    SharedPreferences sharedpreferences;
    private LocationUpdatesService mService = null;
    int dutymode = 0;

    Location location;
    LocationManager locationManager;
    Criteria criteria;
    String Holder;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = (float) 10.7639;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final long MIN_TIME_BW_UPDATES = 30000;
    public static String curlat = "", curlong = "";
    String TAG = "NoramalService";
    public static String callstate = "";
    RequestQueue queue;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();


        Log.e(TAG,"OnCreate Call  ===>>> ");


        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        modelname = manufacturer + " " + model;
        osversion = Build.VERSION.RELEASE;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        Holder = locationManager.getBestProvider(criteria, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        curlat = String.valueOf(location.getLatitude());
                        curlong = String.valueOf(location.getLongitude());
                    }
                }
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    curlat = String.valueOf(location.getLatitude());
                    curlong = String.valueOf(location.getLongitude());
                }
            }
        }

//        setLocationSendingAlarm();
    }

        int i = 0;
    int iM = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final int interval2 = 500;
        final Handler ha2=new Handler();
        ha2.postDelayed(new Runnable() {

            @Override
            public void run() {
                i++;
                Log.e(TAG,"Service is running ====>>> "+NotificationUtils.isAppIsInBackground(getApplicationContext()));
                ha2.postDelayed(this, interval2);
            }
        }, 1000);



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        Holder = locationManager.getBestProvider(criteria, false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        curlat = String.valueOf(location.getLatitude());
                        curlong = String.valueOf(location.getLongitude());
                    }
                }
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    curlat = String.valueOf(location.getLatitude());
                    curlong = String.valueOf(location.getLongitude());
                }
            }
        }
//
        prefs = getSharedPreferences("Login", MODE_PRIVATE);
        final int dutymode = prefs.getInt("dutymode", 6);
        fireregId = prefs.getString("regId", NORMAL_SERVICE);
        userid = prefs.getString("userid", "");
        logtokan = prefs.getString("tokan", "");
        starttimestemp = prefs.getString("starttimestemp", "");
        curranttimestemp = String.valueOf(System.currentTimeMillis());
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
        formattedDate = df.format(c);
        formattedtime = df1.format(c);
//
        updateTimer(dutymode);
        final int interval = 60000;
//        final int interval = 1000;


//
        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.e("NormalService","Timer : dutymode "+iM);
                iM++;
                final int dutymode = prefs.getInt("dutymode", 6);

                if(modelname.toLowerCase().contains("samsung")) {
                    updateTimer(dutymode);
                }

                ha.postDelayed(this, interval);
            }
        }, 1000);

        Log.e("LocationCheck", "NormalService dutymode --->>> "+dutymode);
//

        return START_STICKY;
    }

    public void updateTimer(int dutymode){

        Log.e("NormalService"," updateTimer : "+dutymode);

        if (dutymode == 1) {
            if (Common.isInternetOn(NormalService.this)) {
                SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
                logtokan = prefs.getString("tokan", "");
                totaldeivartime(NormalService.this);
            } else {
                curranttimestemp = "0";
                sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("starttimestemp", curranttimestemp);
                editor.apply();
            }
        }
    }

//    private void check_ValidTime() {
//
//         starttimestemp = prefs.getString("starttimestemp", "");
//        curranttimestemp = String.valueOf(System.currentTimeMillis());
//        StringRequest request = new StringRequest(Request.Method.POST, Common.checkcurrenttime, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//
//
//                    JSONObject obj = new JSONObject(response);
//                    JSONObject obj1 = obj.getJSONObject("success");
//                    JSONObject obj2 = obj1.getJSONObject("current");
//                    String valid = obj1.getString("valid ");
//                    String date = obj2.getString("date");
//                    String[] date1 = date.split(" ");
//                    String fcm_valid = obj1.getString("fcm_valid ");
//                    if (fcm_valid.equalsIgnoreCase("false")) {
//                        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//                        editor.putString("starttimestemp", curranttimestemp);
//                        editor.apply();
//                        logoutclass();
//                    } else {
//                        if (valid.equalsIgnoreCase("false")) {
//                            curranttimestemp = "0";
//                            sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedpreferences.edit();
//                            editor.putString("starttimestemp", curranttimestemp);
//                            editor.apply();
//                        } else {
//                            totaldeivartime(NormalService.this);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("driver_id", userid);
//                params.put("fcm_id", fireregId);
//                return params;
//            }
//        };
//
//        if (queue == null)
//            queue = Volley.newRequestQueue(NormalService.this);
//
//
//        queue.add(request);
//    }

    public void logoutclass() {

        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("tokan");
        editor.remove("role");
        editor.remove("dutymode");
        editor.putBoolean("hasLoggedIn", false);
        editor.apply();

        NotificationUtils.clearNotifications(NormalService.this);
        Intent i = new Intent(NormalService.this, PhoneNumberVerifyActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

//    private void setLocationSendingAlarm() {
//
//        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(getApplicationContext(), NormalService.class);
//        intent.putExtra("locationSendingAlarm", true);
//        PendingIntent pendingIntent = PendingIntent.getService(this, 987654321, intent, 0);
//        try {
//            alarmManager.cancel(pendingIntent);
//        } catch (Exception e) {
//
//        }
//        int interval = (1000 * 60) * 3;
////        int interval = 1000;
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
//    }


    void sendlatlong() {
        try {
            SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
            final String logtokan = prefs.getString("tokan", "");
            final String car_id = prefs.getString("car_id", "");
            final int dutymode = prefs.getInt("dutymode", 6);
            Log.e("logtokan", logtokan);
            StringRequest request = new StringRequest(Request.Method.POST, updatecarlatlong, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG," updatecarlatlong Response :"+ response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error is ", "" + error);
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

                    PackageInfo packageInfo = null;
                    try {
                        packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    String version_name = packageInfo.versionName;

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("lat", curlat);
                    params.put("long", curlong);
                    params.put("car_id", car_id);
                    params.put("dutymode", String.valueOf(dutymode));
                    params.put("app_v", version_name);

                    Log.e(TAG," updatecarlatlong param : "+new Gson().toJson(params));

                    return params;
                }
            };
            if (queue == null) {
                queue = Volley.newRequestQueue(NormalService.this);
            }
            queue.add(request);
        } catch (Exception e) {
            Log.e("Exception", " Send latlong problam  " + e.toString());
        }
    }


    void totaldeivartime(final Context context) {

        Log.e("NormalService"," totaldeivartime call : ");

        logtokan = prefs.getString("tokan", "");
        starttimestemp = prefs.getString("starttimestemp", "");
        curranttimestemp = String.valueOf(System.currentTimeMillis());

        StringRequest request = new StringRequest(Request.Method.POST, adddrivertotalminute, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {


                    Log.e("LocationCheck", " NormalService --->>> "+response);

                    JSONObject object = new JSONObject(response);
                    JSONObject object1 = object.getJSONObject("success");


                    if (object1.has("status")) {

                        String status = object1.getString("status");

                        if (!status.equalsIgnoreCase("success")) {

                            String msg = object1.getString("msg");

                            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                            View layout = inflater.inflate(R.layout.custome_toast_layout, null);
                            RelativeLayout linearLayout = layout.findViewById(R.id.toast_layout_root);
                            CTextView text = (CTextView) layout.findViewById(R.id.text);
                            text.setText(msg);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            curranttimestemp = "0";
                            sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("starttimestemp", curranttimestemp);
                            editor.apply();
                            playAlertTone(getApplicationContext());
//                        logoutclass();
                        } else {
                            sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("starttimestemp", curranttimestemp);
                            editor.apply();
//
                        }
                    } else {
                        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("starttimestemp", curranttimestemp);
                        editor.apply();
                    }

                } catch (Exception e) {

                    Log.e("LocationCheck", " totaldeivartime error --->>> "+e.getMessage());

                    e.printStackTrace();
                    curranttimestemp = "0";
                    sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("starttimestemp", curranttimestemp);
                    editor.apply();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("LocationCheck", " totaldeivartime error --->>> "+error.getMessage());
                curranttimestemp = "0";
                sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("starttimestemp", curranttimestemp);
                editor.apply();
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                PackageInfo packageInfo = null;
                try {
                    packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String version_name = packageInfo.versionName;

                params.put("starttimestamp", starttimestemp);
                params.put("currenttimestamp", curranttimestemp);
                params.put("location_on", String.valueOf(Common.CheckGpsStatus(NormalService.this)));
                params.put("latitude", curlat);
                params.put("longitude", curlong);
                params.put("m_model", modelname);
                params.put("m_os", osversion);
                params.put("m_version", version_name);

                Log.e("LocationCheck", " totaldeivartime param --->>> "+new Gson().toJson(params));
                return params;
            }
        };
        if (queue == null) {
            queue = Volley.newRequestQueue(NormalService.this);
        }
        queue.add(request);
    }

    private void playAlertTone(final Context context) {

        Thread t = new Thread() {
            public void run() {
                MediaPlayer player = null;
                int countBeep = 0;
                while (countBeep < 2) {
                    player = MediaPlayer.create(context, R.raw.firealarm);
                    player.start();
                    countBeep += 1;
                    try {
                        Thread.sleep(1000);
                        player.release();
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);


        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification);
        if(mediaPlayer!= null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e(TAG,"OnLocation Change =>> "+location.getLatitude()+""+location.getLongitude());

        curlat = String.valueOf(location.getLatitude());
        curlong = String.valueOf(location.getLongitude());

        SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        Log.e(TAG,"LatLong : "+location.getLatitude()+":"+location.getLongitude());

//        final Toast toast = Toast.makeText(getApplicationContext(), "LatLong : "+location.getLatitude()+":"+location.getLongitude(), Toast.LENGTH_LONG );
//        toast.show();
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                toast.cancel();
//            }
//        }, 500);

        if (location.isFromMockProvider() && sharedpreferences.getBoolean("isMock", true) &&sharedpreferences.getBoolean("diload", true) ) {

            Intent intent1 = new Intent();
            intent1.setAction(ISMOCKLOCATION);
            intent1.putExtra(ISMOCKLOCATION, "CHANGE_FLOW");
            getApplicationContext().sendBroadcast(intent1);

        }

        if(!location.isFromMockProvider()){
            editor.putBoolean("isMock", true);
            editor.putBoolean("diload", true);
            editor.apply();
        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

}