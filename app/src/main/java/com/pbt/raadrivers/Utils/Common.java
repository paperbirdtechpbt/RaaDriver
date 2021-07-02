package com.pbt.raadrivers.Utils;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.pbt.raadrivers.Activity.AcceptRideActivity;

import org.json.JSONObject;

import java.util.List;

public class Common {


    static ProgressDialog progress;

    public static void progstart(Context load, String title, String mes) {
        try {
            progress = new ProgressDialog(load);
            progress.setMessage(title);
            progress.setTitle(mes);
            progress.setCancelable(false);
            progress.show();
        } catch (Exception e) {

        }
    }

    public static void progstop() {
        Log.e("Progress", "Stop ");
        if (progress != null) {
            progress.cancel();
        }
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static void driver(Context mContext, String mess, String payload, String datafromnoti) {
        try {
            JSONObject datafromnot = new JSONObject(datafromnoti);
            JSONObject data = datafromnot.getJSONObject("data");
            try {
                int driver = data.getInt("is_driver");

                if (driver == 1) {
                    Log.e("message", String.valueOf(payload));

                    Log.e("DrivarHomePage", "Common  Accept Ride Activity Start -->> ");

                    Intent i = new Intent(mContext, AcceptRideActivity.class);
                    i.putExtra("message", mess);
                    i.putExtra("payload", String.valueOf(payload));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(i);
                } else {

                }
            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Boolean CheckGpsStatus(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsstaus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gpsstaus) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isInternetOn(Context context) {

        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connec.getActiveNetworkInfo();

        if (activeNetworkInfo != null) {
            if (activeNetworkInfo.isConnected()) {
                return true;
            } else if (!activeNetworkInfo.isConnected()) {
                return false;
            }
        }
        return false;

    }
}
