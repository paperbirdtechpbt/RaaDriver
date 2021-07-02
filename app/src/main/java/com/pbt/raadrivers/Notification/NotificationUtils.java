package com.pbt.raadrivers.Notification;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.pbt.raadrivers.Activity.SplashScreenActivity;
import com.pbt.raadrivers.R;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.pbt.raadrivers.Notification.Config.NOTIFICATION_CHANNEL_ID;
import static com.pbt.raadrivers.Notification.Config.NOTIFICATION_ID_BIG_IMAGE;

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();
    Uri alarmSound;
    private Context mContext;
    public static MediaPlayer mPlayer;
    public static CountDownTimer countDownTimer;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, int is_drivar, String payload, Intent intent) {
        showNotificationMessage(title, message, timeStamp, is_drivar, payload, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, final int is_drivar, String payload, Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;


        // notification icon
        final int icon = R.mipmap.ic_launcher;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

        if (is_drivar == 1) {
            alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
        } else {
            alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/consequence");
        }


        if (is_drivar == 1) {
            try {
                JSONObject main = new JSONObject(String.valueOf(payload));
                if (main.has("message")) {
                    String status = main.getString("message");
                    if (status.equalsIgnoreCase("Trip finished Success")) {
                        SharedPreferences sharedpreferences;
                        sharedpreferences = mContext.getSharedPreferences("Login", Context.MODE_PRIVATE);
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

                        Intent resultIntent = new Intent(mContext, SplashScreenActivity.class);
                        resultIntent.putExtra("message", message);
                        resultIntent.putExtra("payload", String.valueOf(payload));
                        showNotificationMessage(title, message, String.valueOf(System.currentTimeMillis()), 1, String.valueOf(payload), resultIntent);

                    } else {

//                        Log.e("DrivarHomePage"," NotificationUtil  Accept Ride Activity Start -->> ");
//
//                        Intent intent1 = new Intent(mContext, Accept_ride_activity.class);
//                        intent1.putExtra("message", message);
//                        intent1.putExtra("payload", String.valueOf(payload));
//                        intent1.putExtra("isappbaground", NotificationUtils.isAppIsInBackground(mContext));
//                        Log.e("in Notificatioon", String.valueOf(NotificationUtils.isAppIsInBackground(mContext)));
//                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
//                                12345, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
//                        AlarmManager am =
//                                (AlarmManager) mContext.getSystemService(Activity.ALARM_SERVICE);
//                        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
//                                pendingIntent);


                    }
                } else {

//                    Log.e("DrivarHomePage"," NotificationUtil  Accept Ride Activity Start -->> ");
//                    Intent intent1 = new Intent(mContext, Accept_ride_activity.class);
//                    intent1.putExtra("message", message);
//                    intent1.putExtra("payload", String.valueOf(payload));
//                    intent1.putExtra("isappbaground", NotificationUtils.isAppIsInBackground(mContext));
//
//                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
//                            12345, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
//                    AlarmManager am =
//                            (AlarmManager) mContext.getSystemService(Activity.ALARM_SERVICE);
//                    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
//                            pendingIntent);
                }
            } catch (Exception e) {
                Log.e("Exception ", "NotificationUtils     " + e.toString());
            }
        }


        showSmallNotification(mBuilder, icon, title, String.valueOf(Html.fromHtml(message)), timeStamp, resultPendingIntent, alarmSound);
        playNotificationSound(is_drivar);


    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationManager mNotificationManager;

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(Html.fromHtml(title).toString())
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.raalogo)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.raalogo))
                .setContentText(Html.fromHtml(message).toString())
                .build();
        //  NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        assert mNotificationManager != null;
        mNotificationManager.notify(NOTIFICATION_ID_BIG_IMAGE /* Request Code */, mBuilder.build());


        //  notificationManager.notify(Config.NOTIFICATION_ID, notification);
    }


    public void initializeTimer() {
        try {

            countDownTimer = new CountDownTimer(10000, 3000) {

                @SuppressLint("SetTextI18n")
                public void onTick(long millisUntilFinished) {

                    mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification);
                    if (!mPlayer.isPlaying()) {
                        mPlayer.start();
                    }
                }

                @Override
                public void onFinish() {

                    if (mPlayer != null && mPlayer.isPlaying()) {
                        mPlayer.stop();
                        mPlayer.release();
                        mPlayer = null;
                    } else if (mPlayer != null) {
                        mPlayer.stop();
                        mPlayer.release();
                    }

                }
            };
            countDownTimer.start();

        } catch (Exception e) {

        }
    }

    // Playing notification sound
    public void playNotificationSound(int is_drivar) {
//        try {
//
//            if (is_drivar == 1) {
//                alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                        + "://" + mContext.getPackageName() + "/raw/notification");
//                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                    Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
//                    r.play();
//                }else{
//                    Handler handler = new Handler(Looper.getMainLooper());
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            initializeTimer();
//                        }
//                    });
//                }
//
//            } else {
//                alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                        + "://" + mContext.getPackageName() + "/raw/consequence");
//
//                Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
//                r.play();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("Exception", e.toString());
//        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                componentInfo = taskInfo.get(0).topActivity;
            }
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

    }


    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
