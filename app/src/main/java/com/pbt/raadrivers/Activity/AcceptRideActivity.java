package com.pbt.raadrivers.Activity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pbt.raadrivers.Notification.NotificationUtils;
import com.pbt.raadrivers.R;

import org.json.JSONObject;

import in.shadowfax.proswipebutton.ProSwipeButton;

import static com.pbt.raadrivers.Notification.Config.NOTIFICATION_ID_BIG_IMAGE;


public class AcceptRideActivity extends AppCompatActivity {

    TextView startaddress, endaddress, timer, triptype, endtrext, triptext, starttext;
    ProSwipeButton accept;
    String message, payload;
    String[] start;
    CountDownTimer Time;
    MediaPlayer mMediaPlayer;
    PowerManager.WakeLock wl;
    boolean isbackground = false;
    Uri alarmSound;

    String TAG = "AcceptRide";

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().addFlags(flags);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel;
            AudioAttributes attributes;
            attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel = new NotificationChannel("channel01", "name", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("description");
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.cancelAll();
        }

        setContentView(R.layout.activity_accept_ride_activity);

        startaddress = findViewById(R.id.startaddress);
        endaddress = findViewById(R.id.endaddress);
        timer = findViewById(R.id.timer);
        accept = findViewById(R.id.accept);
        triptype = findViewById(R.id.triptype);
        endtrext = findViewById(R.id.endtrext);
        triptext = findViewById(R.id.triptext);
        starttext = findViewById(R.id.starttext);

        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("isridestart", "");
        editor.putString("startaddss", "");
        editor.putString("endaddss", "");
        editor.putString("is_autop_tripfrompayload", "");
        editor.putString("start_otp1frompayload", "");
        editor.putString("whaitingaddress_one", "");
        editor.putString("whaitingcustpayload", "");
        editor.putString("whaitingmessageTO", "");
        editor.putString("cust_name_driver", "");
        editor.putString("cust_number_driver", "");
        editor.putString("payloadfromnoti", "");
        editor.putString("messagefromnoti", "");
        editor.putString("finalbill", "");
        editor.apply();

        try {
            Intent i = getIntent();
            payload = i.getStringExtra("payload");
            message = i.getStringExtra("message");
            try {
                Log.e("from intent", String.valueOf(i.getExtras().getBoolean("isappbaground")));
                isbackground = i.getExtras().getBoolean("isappbaground");

            } catch (Exception e) {
                Log.e("Exception", "from intent   " + e.toString());
            }

            JSONObject main = new JSONObject(payload);

            Log.e(TAG, "Acceept Ride PayLoad ---->> " + main.toString());
            SharedPreferences sharedpreferences1 = getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedpreferences1.edit();

            SharedPreferences preferencesNavigate = getSharedPreferences("NavigateLocation", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorNavigate = preferencesNavigate.edit();

            try {
                if (main != null) {
                    if (main.has("start_latitude") && main.has("start_longitude") && main.has("end_latitude") && main.has("end_longitude")) {
                        editorNavigate.putString("locstart_latitude", main.getString("start_latitude"));
                        editorNavigate.putString("locstart_longitude", main.getString("start_longitude"));
                        editorNavigate.putString("locend_latitude", main.getString("end_latitude"));
                        editorNavigate.putString("locend_longitude", main.getString("end_longitude"));
                        editorNavigate.apply();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception " + e.toString() + " line:" + e.getStackTrace()[0].getLineNumber());
            }

            // For Cancel Ride from USER
            if (main.has("status")) {
                String status = main.getString("status");
                if (status.equalsIgnoreCase("trip_canceled")) {
                    //Toast.makeText(Accept_ride_activity.this, "Caqncel Ride", Toast.LENGTH_LONG).show();
                    String message321 = main.getString("message");
                    startaddress.setText(message321);
                    startaddress.setTextSize(20);
                    endaddress.setVisibility(View.GONE);
                    timer.setVisibility(View.GONE);
                    triptype.setVisibility(View.GONE);
                    accept.setText("OK");
                    endtrext.setVisibility(View.GONE);
                    endtrext.setVisibility(View.GONE);
                    triptext.setVisibility(View.GONE);
                    starttext.setVisibility(View.GONE);

                    editor1.putString("finalBill", "no");
                    editor1.apply();
                    Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                            + "://" + getApplicationContext().getPackageName() + "/raw/notification");
                    try {
                        mMediaPlayer = new MediaPlayer();
                        mMediaPlayer.setDataSource(this, alarmSound);
                        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                            mMediaPlayer.setLooping(true);
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                            Log.e(TAG, "MediaPlayer Start -->>> 162 ");
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }

                    accept.setSwipeDistance(.7f);

                    accept.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
                        @Override
                        public void onSwipeConfirm() {
                            // user has swiped the btn. Perform your async operation now
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    mMediaPlayer = new MediaPlayer();

                                    accept.showResultIcon(true, false);
                                    if (mMediaPlayer != null) {
                                        mMediaPlayer.start();
                                        Log.e(TAG, "MediaPlayer Start -->>> 184 ");
                                        mMediaPlayer.stop();
                                    } else {
                                        mMediaPlayer.stop();
                                    }

                                    String ns = Context.NOTIFICATION_SERVICE;
                                    NotificationManager nMgr = (NotificationManager) getSystemService(ns);
                                    nMgr.cancel(NOTIFICATION_ID_BIG_IMAGE);

                                    SharedPreferences sharedpreferences1;
                                    sharedpreferences1 = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                    final SharedPreferences.Editor editor1 = sharedpreferences1.edit();
                                    editor1.putString("finalBill", "no");
                                    editor1.apply();
                                    Log.d("asdf tag", "acceptr ride");
                                    Intent i = new Intent(AcceptRideActivity.this, DrivarHomePageActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                            }, 1000);
                            mMediaPlayer.stop();
                        }
                    });

                    return;
                } else if (status.equalsIgnoreCase("trip_finished_from_admin")) {

                    String message321 = main.getString("message");
                    startaddress.setText(message321);
                    startaddress.setTextSize(20);
                    endaddress.setVisibility(View.GONE);
                    timer.setVisibility(View.GONE);
                    triptype.setVisibility(View.GONE);
                    accept.setText("OK");
                    endtrext.setVisibility(View.GONE);
                    endtrext.setVisibility(View.GONE);
                    triptext.setVisibility(View.GONE);
                    starttext.setVisibility(View.GONE);
                    Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                            + "://" + getApplicationContext().getPackageName() + "/raw/notification");
                    try {
                        mMediaPlayer = new MediaPlayer();
                        mMediaPlayer.setDataSource(this, alarmSound);
                        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                            mMediaPlayer.setLooping(true);
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                            Log.e(TAG, "MediaPlayer Start -->>> 240 ");

                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }

                    accept.setSwipeDistance(.7f);
                    accept.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
                        @Override
                        public void onSwipeConfirm() {
                            // user has swiped the btn. Perform your async operation now
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    accept.showResultIcon(true, false);
                                    if (mMediaPlayer != null) {
                                        mMediaPlayer.start();
                                        Log.e(TAG, "MediaPlayer Start -->>> 258 ");
                                        mMediaPlayer.stop();
                                    } else {
                                        mMediaPlayer.stop();
                                    }
                                    SharedPreferences sharedpreferences1;
                                    sharedpreferences1 = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                    final SharedPreferences.Editor editor1 = sharedpreferences1.edit();
                                    editor1.putString("finalBill", "no");
                                    editor1.apply();

                                    String ns = Context.NOTIFICATION_SERVICE;
                                    NotificationManager nMgr = (NotificationManager) getSystemService(ns);
                                    nMgr.cancel(NOTIFICATION_ID_BIG_IMAGE);

                                    Intent i = new Intent(AcceptRideActivity.this, DrivarHomePageActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                            }, 1000);
                            mMediaPlayer.stop();
                        }
                    });

                    return;
                } else if (status.equalsIgnoreCase("today_work_done")) {

                    String message321 = main.getString("message");
                    startaddress.setText(message321);
                    startaddress.setTextSize(20);
                    endaddress.setVisibility(View.GONE);
                    timer.setVisibility(View.GONE);
                    triptype.setVisibility(View.GONE);
                    accept.setText("OK");
                    endtrext.setVisibility(View.GONE);
                    endtrext.setVisibility(View.GONE);
                    triptext.setVisibility(View.GONE);
                    starttext.setVisibility(View.GONE);

                    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    try {
                        mMediaPlayer = new MediaPlayer();
                        mMediaPlayer.setDataSource(this, uri);
                        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                            mMediaPlayer.setLooping(false);
                            mMediaPlayer.prepare();
                            mMediaPlayer.start();
                            Log.e(TAG, "MediaPlayer Start -->>> 313 ");

                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }

                    accept.setSwipeDistance(.7f);
                    accept.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
                        @Override
                        public void onSwipeConfirm() {
                            // user has swiped the btn. Perform your async operation now
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    accept.showResultIcon(true, false);
                                    mMediaPlayer.stop();

                                    String ns = Context.NOTIFICATION_SERVICE;
                                    NotificationManager nMgr = (NotificationManager) getSystemService(ns);
                                    nMgr.cancel(NOTIFICATION_ID_BIG_IMAGE);

                                    Intent i = new Intent(AcceptRideActivity.this, DrivarHomePageActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }, 1000);
                            mMediaPlayer.stop();
                        }
                    });

                    return;
                }
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }


        try {
            alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getApplicationContext().getPackageName() + "/raw/notification");
        } catch (Exception e) {
        }

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, alarmSound);
            final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                Log.e(TAG, "MediaPlayer Start -->>> 368 ");
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        try {
            start = message.split("To:");
            startaddress.setText(start[0].replace("From: ", ""));
            endaddress.setText(start[1]);

            JSONObject main = new JSONObject(payload);

            String roundtrip = main.getString("roundtrip");
            String incity = main.getString("incity");
            triptype.setText(Html.fromHtml("<b>" + roundtrip + " " + incity + "</b>"));
            editor.putString("roundtripfrompayload", roundtrip);
            editor.putString("incityfrompayload", incity);
            editor.apply();

        } catch (Exception e) {
            Log.e("|Exception", e.toString());
        }
        try {
            JSONObject main = new JSONObject(payload);
            if (main.has("is_auto_trip")) {
                String is_auto_trip = main.getString("is_auto_trip");
                String start_otp123 = main.getString("start_otp");
                //triptype.setText(Html.fromHtml("<b>"+roundtrip+"</b> And <b>"+incity+"</b>"));
                editor.putString("is_autop_tripfrompayload", is_auto_trip);
                editor.putString("start_otp1frompayload", start_otp123);
                editor.apply();
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        accept.setSwipeDistance(.7f);
        accept.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        accept.showResultIcon(true, false);
                        Time.cancel();
                        mMediaPlayer.stop();




                        String ns = Context.NOTIFICATION_SERVICE;
                        NotificationManager nMgr = (NotificationManager) getSystemService(ns);
                        nMgr.cancel(NOTIFICATION_ID_BIG_IMAGE);

                        if(start != null ) {

                            SharedPreferences sharedpreferences;
                            sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("message", start[0]);
                            editor.putString("messageTO", start[1]);
                            editor.apply();

                            Intent i = new Intent(AcceptRideActivity.this, DrivarHomePageActivity.class);
                            i.putExtra("message", start[0]);
                            i.putExtra("payload", payload);
                            startActivity(i);
                            finish();
                        }

                    }
                }, 2000);

            }
        });

        Time = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("Seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                mMediaPlayer.stop();

                editor.putString("page", "");
                editor.putString("message", "");
                editor.putString("payload", "");
                editor.apply();

                try {
                    wl.release();
                } catch (Exception e) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    Log.e("Exception", "release  " + e.toString());
                }
                NotificationUtils.clearNotifications(AcceptRideActivity.this);
                try {
                    if (isbackground) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        accept.showResultIcon(true, false);
                        Time.cancel();
                        mMediaPlayer.stop();

                    } else {
                        Intent i = new Intent(AcceptRideActivity.this, DrivarHomePageActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        accept.showResultIcon(true, false);
                        Time.cancel();
                        mMediaPlayer.stop();
                    }
                } catch (Exception e) {
                    Intent i = new Intent(AcceptRideActivity.this, DrivarHomePageActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    accept.showResultIcon(true, false);
                    Time.cancel();
                    mMediaPlayer.stop();
                }
            }
        }.start();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(AcceptRideActivity.this, "You can not go Back, Thanks", Toast.LENGTH_LONG).show();
    }

}
