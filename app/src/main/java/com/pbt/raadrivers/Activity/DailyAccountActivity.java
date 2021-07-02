package com.pbt.raadrivers.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pbt.raadrivers.Adapter.AdapterDailyAccount;
import com.pbt.raadrivers.Notification.Config;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.pbt.raadrivers.Utils.Common.driver;
import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;

public class DailyAccountActivity extends AppCompatActivity implements AppConstant {

    TextView total_balance, daily_total_balance, dailybalance, data, totalhr;
    ListView ls;
    LinearLayout title;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    TextView titleleft, titleright;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DailyAccountActivity.this, DrivarHomePageActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_account);

        MyApplication.setCurrentActivity(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        totalhr = findViewById(R.id.totalhr);
        total_balance = findViewById(R.id.total_balance);
        daily_total_balance = findViewById(R.id.daily_total_balance);
        dailybalance = findViewById(R.id.dailybalance);
        titleleft = findViewById(R.id.titleleft);
        titleright = findViewById(R.id.titleright);
        data = findViewById(R.id.data);
        ls = findViewById(R.id.ls);
        title = (LinearLayout) findViewById(R.id.title);


        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        if (sharedpreferences.getString("raadarbar_car", "0").equalsIgnoreCase("1")) {

            title.setBackgroundColor(getResources().getColor(R.color.startgreen));
            titleleft.setText("Your Daily balance");
            titleright.setText("Total hour");
            title.setVisibility(View.VISIBLE);

            CallApiForradarbarCar();
        } else {
            title.setBackgroundColor(getResources().getColor(R.color.blue));
            titleright.setText("Your daily trip");
            titleleft.setText("Your daily trip amount");
            title.setVisibility(View.VISIBLE);

            CallApiForNotradarbar();
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                try {
                    String datafromnoti = intent.getStringExtra("datafromnoti");
                    JSONObject datafromnot = new JSONObject(datafromnoti);
                    JSONObject data = datafromnot.getJSONObject("data");

                    try {
                        int driver = data.getInt("is_driver");
                        if (driver == 1) {
                            String payload = intent.getStringExtra("payload");
                            String message = intent.getStringExtra("message");

                            driver(DailyAccountActivity.this, message, payload, datafromnoti);
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                } catch (Exception e) {
                    Log.e("Exception", e.toString());
                }

            }
        };
    }

    private void CallApiForNotradarbar() {

        progstart(DailyAccountActivity.this, "Loading...", "Loading your Data...");

        final String logtokan;
        final SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        StringRequest request = new StringRequest(Request.Method.POST, tripcount, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AAAAA", "Not for driver" + response);
                try {
                    JSONObject main = new JSONObject(response);
                    JSONObject submain = main.getJSONObject("success");
                    String totaltrip = submain.getString("totaltrip");
                    String totaltripamount = submain.getString("totaltripamount");
                    totalhr.setText("" + totaltrip);
                    dailybalance.setText("₹ " + "00.00");
                    total_balance.setText("₹ " + "00.00");
                    JSONArray trip_amount = submain.getJSONArray("trip_amount");

                    if (trip_amount.length() == 0) {
                        data.setVisibility(View.VISIBLE);
                        ls.setVisibility(View.GONE);
                    } else {
                        data.setVisibility(View.GONE);
                        ls.setVisibility(View.VISIBLE);
                    }
                    String[] id, driver_id, trip_id, date, time, km_array, trip_amount_array, commission, onhandcash;
                    trip_amount_array = new String[trip_amount.length()];
                    date = new String[trip_amount.length()];
                    time = new String[trip_amount.length()];
                    km_array = new String[trip_amount.length()];
                    commission = new String[trip_amount.length()];
                    onhandcash = new String[trip_amount.length()];
                    for (int i = 0; i < trip_amount.length(); i++) {
                        trip_amount_array[i] = trip_amount.getJSONObject(i).getString("trip_amount");
                        date[i] = trip_amount.getJSONObject(i).getString("date");
                        time[i] = trip_amount.getJSONObject(i).getString("time");
                        km_array[i] = trip_amount.getJSONObject(i).getString("kilometer");
                        commission[i] = trip_amount.getJSONObject(i).getString("commission");
                        onhandcash[i] = trip_amount.getJSONObject(i).getString("onhandcash");

                    }
                    AdapterDailyAccount adeptor = new AdapterDailyAccount(DailyAccountActivity.this, date, time, trip_amount_array, km_array, commission, onhandcash);
                    ls.setAdapter(adeptor);

                } catch (JSONException e) {
                    Log.e("Exception", e.toString());
                    progstop();
                }
                progstop();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);
                progstop();
            }
        }) {
            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan);
                Log.e("Authorization", "Bearer " + logtokan);
                return params;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("driver_id", prefs.getString("userid", ""));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void CallApiForradarbarCar() {
        progstart(DailyAccountActivity.this, "Loading...", "Loading your Data...");

        final String logtokan;
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        StringRequest request = new StringRequest(Request.Method.GET, daily_balance, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AAAAAA", "For driver" + response);
                try {
                    JSONObject main = new JSONObject(response);
                    JSONObject submain = main.getJSONObject("success");
                    String totalDailyAmnt = submain.getString("totalDailyAmnt");
                    String totalTripAmnt = submain.getString("totalTripAmnt");
                    String totalhrfromapi = submain.getString("totalHours");

                    totalhr.setText(totalhrfromapi);

                    dailybalance.setText("₹ " + totalDailyAmnt);
                    total_balance.setText("₹ " + totalTripAmnt);
                    double one = Double.parseDouble(totalDailyAmnt);
                    double two = Double.parseDouble(totalTripAmnt);
                    double three = one - two;
                    if (three < 0) {
                        daily_total_balance.setText("₹ " + String.valueOf(three));
                        daily_total_balance.setBackgroundColor(getResources().getColor(R.color.appred));
                    } else {
                        daily_total_balance.setText("₹ " + String.valueOf(three));
                        daily_total_balance.setBackgroundColor(getResources().getColor(R.color.bleck));
                    }
                    JSONArray trip_amount = submain.getJSONArray("trip_amount");

                    if (trip_amount.length() == 0) {
                        data.setVisibility(View.VISIBLE);
                        ls.setVisibility(View.GONE);
                    } else {
                        data.setVisibility(View.GONE);
                        ls.setVisibility(View.VISIBLE);
                    }
                    String[] id, driver_id, trip_id, date, time, km_array, trip_amount_array, commission, onhandcash;
                    trip_amount_array = new String[trip_amount.length()];
                    date = new String[trip_amount.length()];
                    time = new String[trip_amount.length()];
                    km_array = new String[trip_amount.length()];
                    commission = new String[trip_amount.length()];
                    onhandcash = new String[trip_amount.length()];
                    for (int i = 0; i < trip_amount.length(); i++) {
                        trip_amount_array[i] = trip_amount.getJSONObject(i).getString("trip_amount");
                        date[i] = trip_amount.getJSONObject(i).getString("date");
                        time[i] = trip_amount.getJSONObject(i).getString("time");
                        km_array[i] = trip_amount.getJSONObject(i).getString("kilometer");
                        commission[i] = trip_amount.getJSONObject(i).getString("commission");
                        onhandcash[i] = trip_amount.getJSONObject(i).getString("onhandcash");
                    }
                    AdapterDailyAccount adeptor = new AdapterDailyAccount(DailyAccountActivity.this, date, time, trip_amount_array, km_array, commission, onhandcash);
                    ls.setAdapter(adeptor);

                } catch (JSONException e) {
                    Log.e("Exception", e.toString());
                    progstop();
                }
                progstop();
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
                Log.e("Authorization", "Bearer " + logtokan);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    void callapi() {
        progstart(DailyAccountActivity.this, "Loading...", "Loading your Data...");

        final String logtokan;
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        StringRequest request = new StringRequest(Request.Method.GET, daily_balance, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Your Array Response", response);
                try {
                    JSONObject main = new JSONObject(response);
                    JSONObject submain = main.getJSONObject("success");
                    String totalDailyAmnt = submain.getString("totalDailyAmnt");
                    String totalTripAmnt = submain.getString("totalTripAmnt");
                    String totalhrfromapi = submain.getString("totalHours");

                    totalhr.setText(totalhrfromapi);

                    dailybalance.setText("₹ " + totalDailyAmnt);
                    total_balance.setText("₹ " + totalTripAmnt);
                    double one = Double.parseDouble(totalDailyAmnt);
                    double two = Double.parseDouble(totalTripAmnt);
                    double three = one - two;
                    if (three < 0) {
                        daily_total_balance.setText("₹ " + String.valueOf(three));
                        daily_total_balance.setBackgroundColor(getResources().getColor(R.color.appred));
                    } else {
                        daily_total_balance.setText("₹ " + String.valueOf(three));
                        daily_total_balance.setBackgroundColor(getResources().getColor(R.color.bleck));
                    }
                    JSONArray trip_amount = submain.getJSONArray("trip_amount");

                    if (trip_amount.length() == 0) {
                        data.setVisibility(View.VISIBLE);
                        ls.setVisibility(View.GONE);
                    } else {
                        data.setVisibility(View.GONE);
                        ls.setVisibility(View.VISIBLE);
                    }
                    String[] id, driver_id, trip_id, date, time, km_array, trip_amount_array, commission, onhandcash;
                    trip_amount_array = new String[trip_amount.length()];
                    date = new String[trip_amount.length()];
                    time = new String[trip_amount.length()];
                    km_array = new String[trip_amount.length()];
                    commission = new String[trip_amount.length()];
                    onhandcash = new String[trip_amount.length()];
                    for (int i = 0; i < trip_amount.length(); i++) {
                        trip_amount_array[i] = trip_amount.getJSONObject(i).getString("trip_amount");
                        date[i] = trip_amount.getJSONObject(i).getString("date");
                        time[i] = trip_amount.getJSONObject(i).getString("time");
                        km_array[i] = trip_amount.getJSONObject(i).getString("kilometer");
                        commission[i] = trip_amount.getJSONObject(i).getString("commission");
                        onhandcash[i] = trip_amount.getJSONObject(i).getString("onhandcash");

                    }

                    AdapterDailyAccount adeptor = new AdapterDailyAccount(DailyAccountActivity.this, date, time, trip_amount_array, km_array, commission, onhandcash);
                    ls.setAdapter(adeptor);

                } catch (JSONException e) {
                    Log.e("Exception", e.toString());
                    progstop();
                }


                progstop();
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
                Log.e("Authorization", "Bearer " + logtokan);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}