package com.pbt.raadrivers.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.pbt.raadrivers.Fonts.CButton;
import com.pbt.raadrivers.Fonts.CEditText;
import com.pbt.raadrivers.Notification.Config;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Service.NormalService;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.ConnectivityReceiver;
import com.pbt.raadrivers.Utils.LocationTrack;
import com.pbt.raadrivers.Utils.MyApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.shadowfax.proswipebutton.ProSwipeButton;

import static com.pbt.raadrivers.Utils.Common.driver;
import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;

public class StartRideActivity extends AppCompatActivity implements AppConstant,ConnectivityReceiver.ConnectivityReceiverListener {
    CEditText otp, kmstart, trip_idedit;
    CButton start;
    String trip_id, custname, custnumber, custaddress, latlong1, latlong2;
    ProSwipeButton proSwipeBtn;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    TextView name, location;
    ImageView call, loc;
    SharedPreferences prefs;
    double latitude1, longitude1, latitude2, longitude2;
    LocationTrack locationTrack2;

    String TAG = "StartRideActivity";
    private Boolean network = Boolean.TRUE;
    private ConnectivityReceiver connectivityReceiver;

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "You can not back without Start Trip", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_ride);

        connectivityReceiver = new ConnectivityReceiver();
        connectivityReceiver.addListener(StartRideActivity.this);
        this.registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        MyApplication.setCurrentActivity(this);
        locationTrack2 = new LocationTrack(this);

        start = findViewById(R.id.start);
        otp = findViewById(R.id.otp);
        kmstart = findViewById(R.id.kmstart);
        name = findViewById(R.id.name);
        call = findViewById(R.id.num);
        loc = findViewById(R.id.loc);
        location = findViewById(R.id.location);
        final Intent i = getIntent();
        trip_id = i.getStringExtra("trip_id");

        final String start_otp1frompayload;
        prefs = getSharedPreferences("Login", MODE_PRIVATE);
        start_otp1frompayload = prefs.getString("start_otp1frompayload", "");
        custname = prefs.getString("cust_name_driver", "");
        custnumber = prefs.getString("cust_number_driver", "");
        custaddress = prefs.getString("cust_address", "");
        latlong1 = prefs.getString("cust_start_driver", "");
        latlong2 = prefs.getString("cust_end_driver", "");
        Log.d("asdf", "  " + latlong1 + "   " + latlong2);
        name.setText(custname);
        location.setText(custaddress);

        if (!start_otp1frompayload.equalsIgnoreCase("")) {
            otp.setText(start_otp1frompayload);
            otp.setEnabled(false);
        }
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:" + custnumber));
                startActivity(call);
            }
        });

        if (!latlong1.isEmpty() && latlong1.equals("00.00") && !latlong2.isEmpty() && latlong2.equals("00.00")) {

            String[] origin = latlong1.split(",");
            latitude1 = Double.parseDouble(origin[0]);
            longitude1 = Double.parseDouble(origin[1]);

            String[] dest = latlong2.split(",");
            latitude2 = Double.parseDouble(dest[0]);
            longitude2 = Double.parseDouble(dest[1]);
        }

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences preferencesNavigate = getSharedPreferences("NavigateLocation", Context.MODE_PRIVATE);

                if (!preferencesNavigate.getString("locend_latitude", "0.0").equals("0.0") && !preferencesNavigate.getString("locend_longitude", "0.0").equals("0.0")) {

                    String locend_latitude = preferencesNavigate.getString("locstart_latitude", "0.0");
                    String locendLongitude = preferencesNavigate.getString("locstart_longitude", "0.0");

                    if (!locend_latitude.equals("0.0") && !locendLongitude.equals("0.0")) {
                        Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + locend_latitude + "," + locendLongitude));
                        navigation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(navigation);
                    } else {
                        Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + latitude1 + "," + longitude1));
                        navigation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(navigation);
                    }

                } else {
                    Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + latitude1 + "," + longitude1));
                    navigation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(navigation);
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
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
                            driver(StartRideActivity.this, message, payload, datafromnoti);
                        } else {
                            Log.e("Lozhd", "else");
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }
                } catch (Exception e) {
                    Log.e("Exception", e.toString());
                }


               /* String payload = intent.getStringExtra("payload");
                String message = intent.getStringExtra("message");
                String datafromnoti = intent.getStringExtra("datafromnoti");
                driver(Start_ride.this,message,payload,datafromnoti);*/
            }
        };
        proSwipeBtn = findViewById(R.id.awesome_btn1);

        proSwipeBtn.setSwipeDistance(.7f);

        proSwipeBtn.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // user has swiped the btn. Perform your async operation now
                new Handler().postDelayed(new Runnable() {
                    @SuppressLint("NewApi")
                    @Override
                    public void run() {
//                        proSwipeBtn.showResultIcon(true, false);
                        if (network) {
                            if (otp.getText().toString().trim().equalsIgnoreCase("") || kmstart.getText().toString().trim().equalsIgnoreCase("")) {
                                proSwipeBtn.showResultIcon(false, true);
                                Toast.makeText(StartRideActivity.this, getResources().getString(R.string.enterriqiredfild), Toast.LENGTH_SHORT).show();

                            } else {
                                startride(otp.getText().toString().trim(), kmstart.getText().toString().trim(), trip_id);
                            }
                        } else {
                            Toast.makeText(StartRideActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
                            proSwipeBtn.showResultIcon(false, true);
                        }

                    }
                }, 1000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));


    }

    void startride(final String otp, final String km, final String tripid) {

        progstart(StartRideActivity.this, "Loading...", "Loading...");

        final String logtokan;
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");

        StringRequest request = new StringRequest(Request.Method.POST, starttrip, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Log.e("response ", "start Ride " + response);

                    SharedPreferences preLocationCheck = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorLocationCheck = preLocationCheck.edit();
                    editorLocationCheck.putString("isStart", "0");
                    editorLocationCheck.apply();
                    JSONObject main = new JSONObject(response);
                    JSONObject succ = main.getJSONObject("success");
                    String status = succ.getString("status");
                    if (status.equalsIgnoreCase("otp_not_match")) {
                        Toast.makeText(StartRideActivity.this, succ.getString("msg"), Toast.LENGTH_LONG).show();
                        progstop();
                        proSwipeBtn.showResultIcon(false, true);
                        return;
                    }
                    String start = succ.getString("start_lat") + "," + succ.getString("start_long");
                    String end = succ.getString("end_lat") + "," + succ.getString("end_long");
                    String trip_status = succ.getString("trip_status");


                    Log.e("response ", "start Ride " + start + "    " + end);
                    SharedPreferences sharedpreferences;
                    sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("isridestart", "yes");
                    editor.putString("startaddss", start);
                    editor.putString("endaddss", end);
                    editor.putString("STRATKM", km);
                    editor.putString("trip_status", trip_status);
                    editor.apply();
                    Intent i = new Intent(StartRideActivity.this, DrivarHomePageActivity.class);
                    i.putExtra("startadd", start);
                    i.putExtra("endadd", end);
                    startActivity(i);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Jo kljzdhckjhs", e.toString());
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
                return params;
            }

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("start_otp", otp);
                params.put("start_km", km);
                if (locationTrack2.getLongitude() != 00 && locationTrack2.getLongitude() != 00) {
                    params.put("start_trip_lat", String.valueOf(locationTrack2.getLatitude()));
                    params.put("start_trip_lon", String.valueOf(locationTrack2.getLongitude()));
                } else {
                    if (NormalService.curlat != null && NormalService.curlong != null) {
                        params.put("start_trip_lat", NormalService.curlat);
                        params.put("start_trip_lon", NormalService.curlong);
                    } else {
                        params.put("start_trip_lat", "00.00");
                        params.put("start_trip_lon", "00.00");
                    }
                }
                params.put("trip_id", tripid);


                Log.e("TrainingActivity", " Start Ride  param --->> " + new Gson().toJson(params));

                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(StartRideActivity.this);
        queue.add(request);
    }

    @Override
    public void networkAvailable() {
        network = Boolean.TRUE;
    }

    @Override
    public void networkUnavailable() {
        network = Boolean.FALSE;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityReceiver.removeListener(this);
        this.unregisterReceiver(connectivityReceiver);
    }
}