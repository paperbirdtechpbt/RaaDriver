package com.pbt.raadrivers.Notification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.pbt.raadrivers.Utils.AppConstant;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService implements AppConstant {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    String logtokan = "";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        System.out.println("Firebase Token Update onTokenRefresh -->>>> " + refreshedToken);
        storeRegIdInPref(refreshedToken);
        sendRegistrationToServer(refreshedToken);
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");

        if (!logtokan.isEmpty())
            callingapiforupdatefirebasetokan(refreshedToken);
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
        editor.putString("regId", token);
        editor.apply();

        SharedPreferences FireBaseToken = getSharedPreferences("FireBaseToken", MODE_PRIVATE);
        SharedPreferences.Editor editorFireBaseToken = FireBaseToken.edit();
        editorFireBaseToken.putString("regId", token);
        editorFireBaseToken.apply();

        callingapiforupdatefirebasetokan(token);
    }

    public void callingapiforupdatefirebasetokan(final String token) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, updatefcm,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
        queue.add(postRequest);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref;
        pref = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }
}

