package com.pbt.raadrivers.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pbt.raadrivers.Activity.PhoneNumberVerifyActivity;
import com.pbt.raadrivers.Notification.NotificationUtils;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;

public class PermissionDialogClass extends Dialog implements AppConstant {

    public Activity c;
    public Dialog d;
    TextView yes, txtMessage;
    String message;
    String status;

    public static final int FILE_PERMISSION_REQUEST_CODE = 112;

    private static final String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public PermissionDialogClass(Activity a, String message, String status) {
        super(a);
        this.c = a;
        this.message = message;
        this.status = status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.permission_dialog);

        yes = findViewById(R.id.txtok);
        txtMessage = findViewById(R.id.txtMessage);

        if (message != null && !message.isEmpty())
            txtMessage.setText(message);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status != null && !status.isEmpty() && status.equals("permission"))
                    ActivityCompat.requestPermissions(c, PERMISSIONS,FILE_PERMISSION_REQUEST_CODE );
                else if (c != null)
                    logoutclass();
                dismiss();
            }
        });
    }

    public void logoutclass() {

        progstart(c, "Loading...", "Loading...");
        SharedPreferences prefs = c.getSharedPreferences("Login", MODE_PRIVATE);
        final String logtokan = prefs.getString("tokan", "");
        StringRequest request = new StringRequest(Request.Method.POST, logout, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Your Array Response", response);

                SharedPreferences sharedpreferences;
                sharedpreferences = c.getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove("tokan");
                editor.remove("role");
                editor.putBoolean("hasLoggedIn", false);
                editor.apply();

                progstop();

                NotificationUtils.clearNotifications(c);
                Intent i = new Intent(c, PhoneNumberVerifyActivity.class);
                c.startActivity(i);
                c.finish();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);
                progstop();
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
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(c.getApplicationContext());
        queue.add(request);

    }

}

