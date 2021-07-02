package com.pbt.raadrivers.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context context;
    public static Activity activity;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public synchronized static Context getAppContext() {
        return MyApplication.context;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        activity = currentActivity;
    }

    public static Activity currentActivity() {
        return activity;
    }

}