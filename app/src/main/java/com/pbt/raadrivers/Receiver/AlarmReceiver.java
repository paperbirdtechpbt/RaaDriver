package com.pbt.raadrivers.Receiver;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.AlarmManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.pbt.raadrivers.Service.NormalService;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {
    Context context;

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (services != null) {
            for (int i = 0; i < services.size(); i++) {
                if ((serviceClass.getName()).equals(services.get(i).service.getClassName()) && services.get(i).pid != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;



        SharedPreferences prefs = context.getSharedPreferences("Login", MODE_PRIVATE);

        final int dutymode = prefs.getInt("dutymode", 6);

//        Log.e("AlarmReceiver"," My Alarm  Received : "+dutymode+" : "+isMyServiceRunning(this.context, NormalService.class));

        if (dutymode == 1) {

            if (isMyServiceRunning(this.context, NormalService.class)) {
            } else {
                Intent background = new Intent(context, NormalService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startService(background);
                } else {
                    context.startService(background);
                }
            }
            setAlarm();
        }else{
            context.stopService(new Intent(context, NormalService.class));
        }
    }

    public void setAlarm() {

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 1000, pendingIntent);
//        } else
//            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(),
                    pendingIntent
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(),
                    pendingIntent
            );
        } else {
            manager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(),
                    pendingIntent
            );
        }
    }
}