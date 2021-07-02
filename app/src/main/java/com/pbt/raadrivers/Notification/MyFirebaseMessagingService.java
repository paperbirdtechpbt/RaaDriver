package com.pbt.raadrivers.Notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pbt.raadrivers.Activity.AcceptRideActivity;
import com.pbt.raadrivers.Activity.DrivarHomePageActivity;
import com.pbt.raadrivers.Activity.NotificationDisplayActivity;
import com.pbt.raadrivers.Activity.OtpVerificationActivity;
import com.pbt.raadrivers.Activity.SplashScreenActivity;
import com.pbt.raadrivers.Activity.ThanksScreenActivity;
import com.pbt.raadrivers.Dialog.CustomDialogClass;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.MyApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService implements AppConstant {

    private static final String TAG = "MyFirebaseMsgService";

    private NotificationUtils notificationUtils;
    String title, message, imageUrl = "", timestamp;
    JSONObject payload;
    boolean isBackground;
    int driver;
    static boolean launch = false;

    public static Activity activity;


    public static MediaPlayer mPlayer;
    public static CountDownTimer countDownTimer;

    String logtokan = "";

    static Context context;


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        OtpVerificationActivity.fireregId = s;

        System.out.println("Firebase Token Update onNewToken -->>>> " + s);

        String refreshedToken = "";
        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);
        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);

        logtokan = prefs.getString("tokan", "");

        if (logtokan.equalsIgnoreCase("")) {

        } else {
            // sending reg id to my server
            callingapiforupdatefirebasetokan(refreshedToken);
        }


        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
        SharedPreferences pref;
        pref = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (!token.isEmpty())
            editor.putString("regId", token);
        else
            editor.putString("regId", "105MyfirebaseEMPTY");
        editor.apply();

        SharedPreferences FireBaseToken = getSharedPreferences("FireBaseToken", MODE_PRIVATE);
        SharedPreferences.Editor editorFireBaseToken = FireBaseToken.edit();
        if (!token.isEmpty())
            editorFireBaseToken.putString("regId", token);
        else
            editorFireBaseToken.putString("regId", "113MyfirebaseEMPTY");
        editorFireBaseToken.apply();

        callingapiforupdatefirebasetokan(token);
    }

    public void callingapiforupdatefirebasetokan(final String token) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, updatefcm,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("Response My FirebaseID", response);

                        try {
                            JSONObject responce = new JSONObject(response);
                            JSONObject success = responce.getJSONObject("success");
                            String trip_status = success.getString("trip_status");

                        } catch (Exception e) {
                            Log.d(TAG, "Exception -->> " + e.getMessage() + " Line:" + e.getStackTrace()[0].getLineNumber());

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
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
                if (token != null && !token.isEmpty())
                    params.put("fireregId", token);
                else
                    params.put("fireregId", prefsFireBase.getString("regId", MYFIREBASE));


                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        //requestQueue.add(jsonObjReq);
        queue.add(postRequest);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref;
        pref = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (!token.isEmpty())
            editor.putString("regId", token);
        else
            editor.putString("regId", "179MyfirebaseEMPTY");
        editor.apply();
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {


            context = getApplicationContext();

            Log.e(TAG, "From: " + remoteMessage.getFrom().toString());
            Log.d(TAG, " onMessageReceived : " + remoteMessage.getFrom().toString());


            if (remoteMessage == null)
                return;


            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                // Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
                // handleNotification(remoteMessage.getNotification().getBody());
            }

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {

                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
                    handleDataMessage(json);
                } catch (Exception e) {
                    Log.d(TAG, "Exception -->> " + e.getMessage() + " Line:" + e.getStackTrace()[0].getLineNumber());
                }
            }

        } catch (Exception e) {
            Log.d(TAG, "Exception -->> " + e.getMessage() + " Line:" + e.getStackTrace()[0].getLineNumber());

        }
    }


    JSONObject data;

    private void handleDataMessage(final JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            data = json.getJSONObject("data");
            try {


                //driver = 1;
                title = data.getString("title");
                message = data.getString("message");
                driver = data.getInt("is_driver");
                timestamp = data.getString("timestamp");
                if (data.getJSONObject("payload") != null)
                    payload = data.getJSONObject("payload");

                Log.e(TAG, " Title  =====  ---->>" + title);
                isBackground = false;
            } catch (Exception e) {
                Log.d(TAG, "Exception -->> " + e.getMessage() + " Line:" + e.getStackTrace()[0].getLineNumber());

            }

            Log.e(TAG, "L: " + String.valueOf(driver));

            try {



                if(title.equalsIgnoreCase("You Miss Trip")) {
                    Log.e(TAG, " Title  =====  \"You Miss Trip\" ---->>" + message);
                    SharedPreferences sharedpreferences1 = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences1.edit();
                    editor.putString("trip_status", "");
                    editor.apply();
                }

                if (!title.trim().equals("Driver Is coming")) {
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("message", message);
                    pushNotification.putExtra("payload", String.valueOf(payload));
                    pushNotification.putExtra("datafromnoti", String.valueOf(json));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                }
                else if(title.equalsIgnoreCase("New_latlong_update")) { }
                else {
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("message", message);
                    pushNotification.putExtra("payload", String.valueOf(payload));
                    pushNotification.putExtra("datafromnoti", String.valueOf(json));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                }

                if (title.equals("Thank You for Ride!!!")) {
                    if (driver == 0) {
                        Intent dialogIntent = new Intent(this, ThanksScreenActivity.class);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(dialogIntent);
                    }
                }

            } catch (Exception e) {
                Log.d(TAG, "Exception -->> " + e.getMessage() + " Line:" + e.getStackTrace()[0].getLineNumber());
            }

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
            {

                if (title.equals("Trip is cancelled")) {

                    if (driver == 1) {
                        Log.e(TAG, "message :" + String.valueOf(payload));
                        Log.e(TAG, "Common  Accept Ride Activity Start -->> ");
                        Intent i = new Intent(getApplicationContext(), AcceptRideActivity.class);
                        i.putExtra("message", message);
                        i.putExtra("payload", String.valueOf(payload));
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        getApplicationContext().startActivity(i);
                        SharedPreferences sharedpreferences1 = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences1.edit();
                        editor.putString("trip_status", "");
                        editor.apply();
                    }

                }
                else if(title.equalsIgnoreCase("New_latlong_update")){

                    Intent dialogIntent = new Intent(getApplicationContext(), DrivarHomePageActivity.class);
                    dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogIntent.putExtra("message", message);
                    dialogIntent.putExtra("payload", String.valueOf(payload));
                    dialogIntent.putExtra("datafromnoti", String.valueOf(data));
                    getApplicationContext().startActivity(dialogIntent);

                }
                else if (title.equals("You have New Trip")) {

                    //@Anil open Accept_rideactivity  Payload when app is open

//                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

                    JSONObject main = payload;

                    SharedPreferences sharedpreferences1 = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences1.edit();
                    editor.putString("trip_status",  main.getString("trip_status"));
                    editor.putString("estimate_price", main.getString("estimate_price"));
                    editor.apply();
                    Intent dialogIntent = new Intent(getApplicationContext(), AcceptRideActivity.class);
                    dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogIntent.putExtra("message", message);
                    dialogIntent.putExtra("payload", String.valueOf(payload));
                    dialogIntent.putExtra("datafromnoti", String.valueOf(data));
                    SharedPreferences FireBaseToken = getSharedPreferences("TripStatus", MODE_PRIVATE);
                    getApplicationContext().startActivity(dialogIntent);
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, driver, String.valueOf(payload), dialogIntent);
//                    } else {
//                        Log.e(TAG, " MyFireBase  Accept Ride Activity Start -->> 324 ");
//                        callalertNotification(message, String.valueOf(payload));
//                    }
                }
                else {
                    //@Anil send user Payload when app is open
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("message", message);
                    pushNotification.putExtra("payload", String.valueOf(payload));
                    pushNotification.putExtra("datafromnoti", String.valueOf(json));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                }
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound(driver);

                //@Anil User Block Popup
                if (title.equals("You are blocked") && payload != null) {
                    JSONObject main = new JSONObject(String.valueOf(payload));
                    if (main != null) {
                        if ("blocked".equals(main.getString("trip_status"))) {

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (MyApplication.currentActivity() != null) {
                                        CustomDialogClass cdd = new CustomDialogClass(MyApplication.currentActivity(), "", "");
                                        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                        cdd.show();
                                    }
                                }
                            });
                        }
                    }
                }
            }
            else {

                if(title.equalsIgnoreCase("New_latlong_update")){
                    Intent dialogIntent = new Intent(getApplicationContext(), DrivarHomePageActivity.class);
                    dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogIntent.putExtra("message", message);
                    dialogIntent.putExtra("payload", String.valueOf(payload));
                    dialogIntent.putExtra("datafromnoti", String.valueOf(data));
                    getApplicationContext().startActivity(dialogIntent);
                }

                try {

                    if (driver == 1 && title.equals("You have New Trip")) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

                            try {
                                JSONObject main = payload;
                                SharedPreferences sharedpreferences1 = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences1.edit();
                                if (main.has("start_latitude") && main.has("start_longitude") && main.has("end_latitude") && main.has("end_longitude")) {
                                    editor.putString("locstart_latitude", main.getString("start_latitude"));
                                    editor.putString("locstart_longitude", main.getString("start_longitude"));
                                    editor.putString("locend_latitude", main.getString("end_latitude"));
                                    editor.putString("locend_longitude", main.getString("end_longitude"));


                                }
                                editor.putString("trip_status",  main.getString("trip_status"));
                                editor.putString("estimate_price", main.getString("estimate_price"));
                                editor.apply();

                            } catch (Exception e) {

                            }

                            Intent dialogIntent = new Intent(getApplicationContext(), AcceptRideActivity.class);
                            dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            dialogIntent.putExtra("message", message);
                            dialogIntent.putExtra("payload", String.valueOf(payload));
                            getApplicationContext().startActivity(dialogIntent);
                            showNotificationMessage(getApplicationContext(), title, message, timestamp, driver, String.valueOf(payload), dialogIntent);
                        } else {
                            callalertNotification(message, String.valueOf(payload));
                            Log.e(TAG, " MyFireBase  Accept Ride Activity Start -->> 378 ");
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Exception -->> " + e.getMessage() + " Line:" + e.getStackTrace()[0].getLineNumber());

                }

                // app is in background, show the notification in notification tray
                if (driver == 1) {
                    JSONObject main = new JSONObject(String.valueOf(payload));
                    if (main.has("message")) {
                        String status = main.getString("message");
                        if (status.equalsIgnoreCase("Trip finished Success")) {

                            SharedPreferences sharedpreferences;
                            sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("Driver_namecws", "");
                            editor.putString("phonecws", "");
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
                            editor.remove("cancel_layout");
                            editor.apply();

                            Intent resultIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                            resultIntent.putExtra("message", message);
                            resultIntent.putExtra("payload", String.valueOf(payload));
                            showNotificationMessage(getApplicationContext(), title, message, timestamp, driver, String.valueOf(payload), resultIntent);

                        }
                        else {

                            SharedPreferences sharedpreferences;
                            sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("page", "driver");
                            editor.putString("message", message);
                            editor.putString("payload", String.valueOf(payload));
                            editor.apply();
                            ;
                            Intent resultIntent = new Intent(getApplicationContext(), DrivarHomePageActivity.class);
                            resultIntent.putExtra("message", message);
                            resultIntent.putExtra("payload", String.valueOf(payload));
                            resultIntent.putExtra("datafromnoti", String.valueOf(data));
                            showNotificationMessage(getApplicationContext(), title, message, timestamp, driver, String.valueOf(payload), resultIntent);


                             /*Intent intent = new Intent(this, Accept_ride_activity.class);
                             intent.putExtra("message", message);
                             intent.putExtra("payload", String.valueOf(payload));
                             intent.putExtra("datafromnoti", String.valueOf(data));
                             PendingIntent pendingIntent = PendingIntent.getActivity(this,
                                     12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                             AlarmManager am =
                                     (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                             am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() ,
                                     pendingIntent);*/

                        }
                    }
                    else if(main.has("message1")) {
                        Intent dialogIntent = new Intent(getApplicationContext(), DrivarHomePageActivity.class);
                        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogIntent.putExtra("message", message);
                        dialogIntent.putExtra("payload", String.valueOf(payload));
                        dialogIntent.putExtra("datafromnoti", String.valueOf(data));
                        getApplicationContext().startActivity(dialogIntent);
                    }
                    else {


                        if (main.has("trip_status")) {
                            String trip_status = main.getString("trip_status");
                            if (trip_status.equalsIgnoreCase("car_is_available")) {
                                SharedPreferences sharedpreferences;
                                sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("payloadfromnoti", String.valueOf(payload));
                                editor.putString("messagefromnoti", message);
                                editor.putString("page", "driver");
                                editor.putString("message", message);
                                editor.putString("payload", String.valueOf(payload));
                                editor.apply();
                            }
                        }
                        Intent resultIntent = new Intent(getApplicationContext(), DrivarHomePageActivity.class);
                        resultIntent.putExtra("message", message);
                        resultIntent.putExtra("payload", String.valueOf(payload));
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, driver, String.valueOf(payload), resultIntent);


                         /*Intent intent = new Intent(this, Accept_ride_activity.class);
                         intent.putExtra("message", message);
                         intent.putExtra("payload", String.valueOf(payload));
                         intent.putExtra("datafromnoti", String.valueOf(data));
                         intent.putExtra("isappbaground", "yes");
                         PendingIntent pendingIntent = PendingIntent.getActivity(this,
                                 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                         AlarmManager am =
                                 (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                         am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                                 pendingIntent);*/

                    }
                }


                if (driver == 1) {
                    JSONObject main = new JSONObject(String.valueOf(payload));
                    if (main.has("message")) {
                        String status = main.getString("message");
                        if (status.equalsIgnoreCase("Trip finished Success")) {

                            SharedPreferences sharedpreferences;
                            sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("Driver_namecws", "");
                            editor.putString("phonecws", "");
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
                            editor.remove("cancel_layout");
                            editor.apply();
                            ;

                            Intent resultIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                            resultIntent.putExtra("message", message);
                            resultIntent.putExtra("payload", String.valueOf(payload));
                            showNotificationMessage(getApplicationContext(), title, message, timestamp, driver, String.valueOf(payload), resultIntent);
                        } else {

                            SharedPreferences sharedpreferences;
                            sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("page", "driver");
                            editor.putString("message", message);
                            editor.putString("payload", String.valueOf(payload));
                            editor.apply();
                            ;
                            Intent resultIntent = new Intent(getApplicationContext(), DrivarHomePageActivity.class);
                            resultIntent.putExtra("message", message);
                            resultIntent.putExtra("payload", String.valueOf(payload));
                            showNotificationMessage(getApplicationContext(), title, message, timestamp, driver, String.valueOf(payload), resultIntent);

                        }
                    }
                    else if(main.has("message1")) {
                        Intent dialogIntent = new Intent(getApplicationContext(), DrivarHomePageActivity.class);
                        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogIntent.putExtra("message", message);
                        dialogIntent.putExtra("payload", String.valueOf(payload));
                        dialogIntent.putExtra("datafromnoti", String.valueOf(data));
                        getApplicationContext().startActivity(dialogIntent);
                    }
                    else {


                        if (main.has("trip_status")) {
                            String trip_status = main.getString("trip_status");
                            if (trip_status.equalsIgnoreCase("car_is_available")) {
                                SharedPreferences sharedpreferences;
                                sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("payloadfromnoti", String.valueOf(payload));
                                editor.putString("messagefromnoti", message);
                                editor.putString("page", "driver");
                                editor.putString("message", message);
                                editor.putString("payload", String.valueOf(payload));
                                editor.apply();
                                ;
                            }
                        }


                        Intent resultIntent = new Intent(getApplicationContext(), AcceptRideActivity.class);
                        resultIntent.putExtra("message", message);
                        resultIntent.putExtra("payload", String.valueOf(payload));
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, driver, String.valueOf(payload), resultIntent);

                        /*Intent intent = new Intent(this, Accept_ride_activity.class);
                        intent.putExtra("message", message);
                        intent.putExtra("payload", String.valueOf(payload));
                        intent.putExtra("datafromnoti", String.valueOf(data));
                        intent.putExtra("isappbaground", "yes");
                        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                                12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager am =
                                (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                                pendingIntent);*/

                    }
                }
            }



            /*if(driver ==1){
                Intent intent = new Intent(this, Accept_ride_activity.class);
                intent.putExtra("message", message);
                intent.putExtra("payload", String.valueOf(payload));
                intent.putExtra("datafromnoti", String.valueOf(data));
                intent.putExtra("isappbaground", NotificationUtils.isAppIsInBackground(getApplicationContext()));
                PendingIntent pendingIntent = PendingIntent.getActivity(this,
                        12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am =
                        (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5,
                        pendingIntent);
            }else */
            if (driver == 0) {
                try {
                    JSONObject main = new JSONObject(String.valueOf(payload));
                    if (main.has("trip_status")) {
                        String status = payload.getString("trip_status");
                    } else if (main.has("status")) {
                        Log.e("status", "status");
                        if (main.getString("status").equalsIgnoreCase("user_trip_canceled")) {
                            Intent resultIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                            showNotificationMessage(getApplicationContext(), title, message, timestamp, driver, String.valueOf(payload), resultIntent);

                            SharedPreferences sharedpreferences;
                            sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putLong(KEY_PREF_CANCEL_TIME_STAMP, System.currentTimeMillis() - COUNT_DOWN_TIME);
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
                            editor.apply();
                        }
                    }

                } catch (Exception e) {
                    Log.d(TAG, "Exception -->> " + e.getMessage() + " Line:" + e.getStackTrace()[0].getLineNumber());
                }

            }
            else if (driver == 2) {

                JSONObject main = new JSONObject(String.valueOf(payload));
                if (main.has("status")) {
                    String status1 = main.getString("status");
                    if (status1.equalsIgnoreCase("today_work_done")) {
                        Intent resultIntent = new Intent(getApplicationContext(), NotificationDisplayActivity.class);
                        resultIntent.putExtra("message", message);
                        resultIntent.putExtra("payload", String.valueOf(payload));
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, driver, String.valueOf(payload), resultIntent);
                    } else {
                        Intent resultIntent = new Intent(getApplicationContext(), NotificationDisplayActivity.class);
                        resultIntent.putExtra("message", message);
                        resultIntent.putExtra("payload", String.valueOf(payload));
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, driver, String.valueOf(payload), resultIntent);
                    }
                } else {
                    Intent resultIntent = new Intent(getApplicationContext(), NotificationDisplayActivity.class);
                    resultIntent.putExtra("message", message);
                    resultIntent.putExtra("payload", String.valueOf(payload));
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, driver, String.valueOf(payload), resultIntent);
                }
            }

        } catch (Exception e) {
            Toast.makeText(activity, TAG + " Exception :" + e.getMessage() + "  line : " + e.getStackTrace()[0].getLineNumber(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, int driver, String payload, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, driver, payload, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, int driver, String payload, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, driver, payload, intent, imageUrl);
    }

    public void callalertNotification(String message, String payload) {


        try {
            Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + MyFirebaseMessagingService.this.getApplicationContext().getPackageName() + "/" + R.raw.notification);
            NotificationChannel channel;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                AudioAttributes attributes;
                attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                channel = new NotificationChannel("channel01", "name", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("description");
                channel.setSound(sound, attributes);
                NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);

            }

            //For Action Button Click event
            Intent intentAction = new Intent(this, ActionReceiver.class);
            //This is optional if you have more than one buttons and want to differentiate between two
            intentAction.putExtra("message", message);
            intentAction.putExtra("payload", String.valueOf(payload));
            intentAction.putExtra("action", "accept");
            PendingIntent pIntentlogin = PendingIntent.getBroadcast(this, 1, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);


            Intent intentActionAnswer = new Intent(this, ActionReceiver.class);
            //This is optional if you have more than one buttons and want to differentiate between two
            intentActionAnswer.putExtra("message", message);
            intentActionAnswer.putExtra("action", "accept");
            intentActionAnswer.putExtra("payload", String.valueOf(payload));
            PendingIntent pIntentloginAccept = PendingIntent.getBroadcast(this, 2, intentActionAnswer, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent notifyIntent = new Intent(this, AcceptRideActivity.class);
            notifyIntent.setAction("open");
            notifyIntent.putExtra("message", message);
            notifyIntent.putExtra("payload", String.valueOf(payload));
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                    this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
// Register channel with system
            Notification notification = new NotificationCompat.Builder(this, "channel01")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(this.getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setSound(sound)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)// heads-up
                    .setContentIntent(notifyPendingIntent)
                    .setFullScreenIntent(notifyPendingIntent, true)
                    .setAutoCancel(false)
                    .setOngoing(true)
//                    .addAction(0, "Reject", pIntentlogin)
                    .addAction(0, "START TRIP", pIntentloginAccept)
//                .setCustomContentView(expandedView)
//                .setCustomBigContentView(expandedView)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .build();

//        notification.bigContentView = expandedView;
//        notification.flags = Notification.FLAG_NO_CLEAR;
            NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(this);
            notificationManager1.notify(202, notification);
        } catch (Exception e) {
            Log.d(TAG, "Exception -->> " + e.getMessage() + " Line:" + e.getStackTrace()[0].getLineNumber());
        }
    }

    public static class ActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent response) {


            if (response != null && response.getStringExtra("payload")!= null  && response.getStringExtra("message") != null && response.getStringExtra("action") != null && response.getStringExtra("action").equals("accept")) {
                Intent intent = new Intent(context, AcceptRideActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("message", response.getStringExtra("message"));
                intent.putExtra("payload", response.getStringExtra("payload"));
                context.startActivity(intent);

                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// The id of the channel.
                String id = "channel01";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.deleteNotificationChannel(id);
                }
            }
        }
    }


}
