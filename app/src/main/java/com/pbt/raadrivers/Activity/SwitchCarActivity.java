package com.pbt.raadrivers.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SwitchCarActivity extends AppCompatActivity implements AppConstant {

    RadioGroup radioGroup;
    RadioButton rdoyes, rdono;
    SharedPreferences prefs;
    RequestQueue queue;
    private String TAG = "extra";
    private String logtokan;
    StringRequest request;
    public static final int DEFAULT_TIMEOUT_MS = 2500;
    public static final int DEFAULT_MAX_RETRIES = 0;
    public static final float DEFAULT_BACKOFF_MULT = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_car);
        MyApplication.setCurrentActivity(this);
        initView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        callapi();
    }

    private void initView() {

        prefs = getSharedPreferences("Login", Context.MODE_PRIVATE);
        radioGroup = findViewById(R.id.radioGroup);
        rdoyes = findViewById(R.id.rdoyes);
        rdono = findViewById(R.id.rdono);

        logtokan = prefs.getString("tokan", "");

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb != null) {
                    String type = rb.getText().toString();
                    switch (type) {
                        case "YES":
                            callapi();
                            break;
                        case "NO":
                            callapi();
                            break;
                    }
                }
            }
        });

    }

    private void callapi() {

        request = new StringRequest(Request.Method.POST, changecartype, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject obj1 = obj.getJSONObject("success");
                    Boolean status = obj1.getBoolean("status");
                    if (!status) {
                        rdoyes.setChecked(true);
                        Toast.makeText(SwitchCarActivity.this, "You are now with Raadarbar", Toast.LENGTH_SHORT).show();
                    } else {
                        rdono.setChecked(true);
                        Toast.makeText(SwitchCarActivity.this, "You can not switch out to Raadarbar", Toast.LENGTH_SHORT).show();
                    }
                    Log.e(TAG, "onResponse: " + response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan);
                return params;
            }
        };

        if (queue == null) {
            queue = Volley.newRequestQueue(SwitchCarActivity.this);
        }
        queue.add(request);
    }

    private void setData(String type) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("isextratime", type);
        editor.apply();
        callapi();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SwitchCarActivity.this, DrivarHomePageActivity.class));
        finish();
    }
}
