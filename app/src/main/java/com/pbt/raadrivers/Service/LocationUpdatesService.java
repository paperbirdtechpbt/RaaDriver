package com.pbt.raadrivers.Service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.pbt.raadrivers.Activity.SplashScreenActivity;
import com.pbt.raadrivers.Fonts.CTextView;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.Common;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LocationUpdatesService extends Service implements AppConstant {

    private static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationupdatesforegroundservice";

    private static final String TAG = "LocationUpdateService";
    private static final String CHANNEL_ID = "channel_01";
    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    private final IBinder mBinder = new LocalBinder();
    //    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS;
    private static final int NOTIFICATION_ID = 12345678;
    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    Handler handler = new Handler();

    SharedPreferences sharedpreferences;
    Runnable runnable;
    private Location mLocation;

    String logtokan;
    SharedPreferences prefs;



    public LocationUpdatesService() {
    }

    RequestQueue queue;

    private String modelname;
    private String osversion;

    String starttimestemp, userid, curranttimestemp;

    @Override
    public void onCreate() {


        prefs = getSharedPreferences("Login", MODE_PRIVATE);
        final int dutymode = prefs.getInt("dutymode", 6);
        logtokan = prefs.getString("tokan", "");
        starttimestemp = prefs.getString("starttimestemp", "");
        curranttimestemp = String.valueOf(System.currentTimeMillis());

        Log.d(TAG, " oncreate  call ");

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        modelname = manufacturer + " " + model;
        osversion = Build.VERSION.RELEASE;


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
                Log.d(TAG, " oncreate onNewLocation ");
                if(!modelname.toLowerCase().contains("samsung")) {
                    totaldeivartime(LocationUpdatesService.this);
                }
            }
        };

        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        sharedpreferences.getString("raadarbar_car", "0");
        Log.e("Timeioujxkjfh", sharedpreferences.getString("raadarbar_car", "0"));
        createLocationRequest();
//        getLastLocation();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    public void requestLocationUpdates() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        } else {
            startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        }
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);


        if (!mChangingConfiguration && prefs.getInt("dutymode", 0) == 1) {
            startForeground(NOTIFICATION_ID, getNotification());
        } else {
            startForeground(NOTIFICATION_ID, getNotification1());
        }
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            stopSelf();
            stopForeground(true);
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    private Notification getNotification() {
        Intent notificationIntent = new Intent(LocationUpdatesService.this, SplashScreenActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(LocationUpdatesService.this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentText(getString(R.string.app_noti))
                .setContentTitle(getString(R.string.on_duty))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.app_noti)))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.raalogo)
                .setWhen(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
            builder.setContentIntent(intent);
        }
        return builder.build();
    }

    private Notification getNotification1() {

        Intent notificationIntent = new Intent(LocationUpdatesService.this, SplashScreenActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(LocationUpdatesService.this, 0,
                notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.app_noti)))
                .setContentText(getString(R.string.app_noti))
                .setContentTitle(getString(R.string.off_duty))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.raalogo)
                .setWhen(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
            builder.setContentIntent(intent);
        }
        return builder.build();
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        Log.i(TAG, "New location: " + location);

        mLocation = location;
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        if (serviceIsRunningInForeground(this)) {
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
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


                    Log.e("LocationCheck", " LocationUpdate --->>> "+response);

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
                logtokan = prefs.getString("tokan", "");
                Log.e("LocationCheck","Token :"+"Bearer " + logtokan);
                PackageInfo packageInfo = null;
                try {
                    packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String version_name = packageInfo.versionName;

                params.put("starttimestamp", starttimestemp);
                params.put("currenttimestamp", curranttimestemp);
                params.put("location_on", String.valueOf(Common.CheckGpsStatus(LocationUpdatesService.this)));
                params.put("latitude", String.valueOf(mLocation.getLatitude()));
                params.put("longitude", String.valueOf(mLocation.getLongitude()));
                params.put("m_model", modelname);
                params.put("m_os", osversion);
                params.put("m_version", version_name);

                Log.e("LocationCheck", "latitude : "+mLocation.getLatitude()+","+mLocation.getLongitude());
                return params;
            }
        };
        if (queue == null) {
            queue = Volley.newRequestQueue(LocationUpdatesService.this);
        }
        queue.add(request);
    }

}