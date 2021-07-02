package com.pbt.raadrivers.Utils;

import android.content.Context;
import android.util.Log;

import com.pbt.raadrivers.BuildConfig;

public class DebugLog {

    public  static  Boolean DEBUG = BuildConfig.DEBUG;

    public static  void printError(String TAG, String message){
        if(DEBUG)
            Log.e(TAG,message);
    }

    public static void printInfo(String TAG, String message){
        if(DEBUG)
            Log.i(TAG,message);
    }
    public static void printDebug(String TAG, String message){
        if(DEBUG)
            Log.i(TAG,message);
    }
}
