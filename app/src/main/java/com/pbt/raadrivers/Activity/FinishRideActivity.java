package com.pbt.raadrivers.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
import com.pbt.raadrivers.Fonts.CEditText;
import com.pbt.raadrivers.Notification.Config;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.ConnectivityReceiver;
import com.pbt.raadrivers.Utils.DebugLog;
import com.pbt.raadrivers.Utils.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.shadowfax.proswipebutton.ProSwipeButton;

import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;

public class FinishRideActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, AppConstant {

    CEditText endotp, endkm, txtTotalKm,talltax,bodertax, parmitioncharge, parkingchage, driverallowance, driverhold;
    ProSwipeButton endslider;
    String trip_id, startkm1, endotp_text = "";
    BroadcastReceiver mRegistrationBroadcastReceiver;
    private String TAG = "finishtrip";

    private Boolean network = Boolean.TRUE;
    private ConnectivityReceiver connectivityReceiver;

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "You Can Not Go to Back Without complete the Billing Proocess ", Toast.LENGTH_LONG).show();
    }

    TextView stratkm,estimated_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finishride);

        connectivityReceiver = new ConnectivityReceiver();
        connectivityReceiver.addListener(FinishRideActivity.this);
        this.registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        SharedPreferences sharedpreferences1 = getSharedPreferences("Login", Context.MODE_PRIVATE);
       String   myestimated_price =  sharedpreferences1.getString("estimate_price", "EMPTY");
        Log.e(TAG," estimate_price : "+myestimated_price);

        MyApplication.setCurrentActivity(this);

        endotp = findViewById(R.id.endotp);
        endkm = findViewById(R.id.endkm);
        talltax = findViewById(R.id.talltax);
        bodertax = findViewById(R.id.bodertax);
        parmitioncharge = findViewById(R.id.parmitioncharge);
        parkingchage = findViewById(R.id.parkingchage);
        driverallowance = findViewById(R.id.driverallowance);
        driverhold = findViewById(R.id.driverhold);
        endslider = findViewById(R.id.endslider);
        stratkm = findViewById(R.id.stratkm);
        txtTotalKm = findViewById(R.id.txtTotalKm);
        estimated_price = findViewById(R.id.estimated_price);

        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        startkm1 = sharedpreferences.getString("STRATKM", "");
        stratkm.setText(Html.fromHtml("Start km: " + startkm1));
        estimated_price.setText("Estimated price ₹"+myestimated_price);
        try {


            endotp_text = sharedpreferences.getString("is_auto_endotp", "");

            if (!endotp_text.equalsIgnoreCase("")) {
                endotp.setText(endotp_text);
                endotp.setEnabled(false);
            }
        } catch (Exception e) {
            Log.e("Exception ", e.toString());
        }

        txtTotalKm.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                DebugLog.printInfo(TAG,"beforeTextChanged "+s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                DebugLog.printInfo(TAG,"beforeTextChanged "+s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                DebugLog.printInfo(TAG,"onTextChanged "+s);
                if(s.length() != 0)
                        endkm.setText((Integer.parseInt(String.valueOf(s) )+  Integer.parseInt(startkm1))+"");
            }
        });

        endslider.setSwipeDistance(.6f);
        endslider.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (network) {
                            if (endkm.getText().toString().trim().equalsIgnoreCase("")) {
//                                Toast.makeText(FinishRideActivity.this, "Please Enter End OTP and ENDkm", Toast.LENGTH_LONG).show();
                                Toast.makeText(FinishRideActivity.this, "Please Enter End KM", Toast.LENGTH_LONG).show();
                                endslider.showResultIcon(false, true);
                            } else {
                                int st = Integer.parseInt(startkm1);
                                int end = Integer.parseInt(endkm.getText().toString().trim());
                                if (st > end) {
                                    Toast.makeText(FinishRideActivity.this, "You have to enter more than " + startkm1 + " KM", Toast.LENGTH_LONG).show();
                                    endslider.showResultIcon(false, true);
                                    return;
                                } else {
                                    hederget();
                                }
                            }
                        } else {
                            Toast.makeText(FinishRideActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 2000);
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
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

    void hederget() {

        progstart(FinishRideActivity.this, "Loading...", "Loading...");

        SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        trip_id = sharedpreferences.getString("trip_id", trip_id);
        Log.e("trip_id", trip_id);
        final String logtokan;
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        StringRequest request = new StringRequest(Request.Method.POST, finishtrip, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Array Response:- -   " + response);

                try {
                    JSONObject main = new JSONObject(response);

                    try {
                        if (main.has("error")) {
                            JSONObject submain = main.getJSONObject("error");
                            Toast.makeText(FinishRideActivity.this, submain.getString("end_otp"), Toast.LENGTH_LONG).show();
                            endslider.showResultIcon(false, true);
                            progstop();
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("Exception error", e.toString());
                    }

                    JSONObject submain = main.getJSONObject("success");
                    String status = submain.getString("status");

                    if (status.equalsIgnoreCase("success")) {

                        String payable_amount = submain.getString("payable_amount");
                        String startkm = submain.getString("start_km");
                        String endkm = submain.getString("end_km");
                        String totalkm = submain.getString("total_km");
                        String acual_price = submain.getString("payable_amount");
                        String estimated_price = submain.getString("estimated_price");
                        String tall_tax = submain.getString("tall_tax");
                        String border_tax = submain.getString("border_tax");
                        String permission_charger = submain.getString("permission_charger");
                        String parking_charger = submain.getString("parking_charger");
                        String driver_allowance = submain.getString("driver_allowance");
                        String driver_night_hold_charge = submain.getString("driver_night_hold_charge");
                        String total_time = submain.getString("total_time");
                        String ride_amount = submain.getString("ride_amount");
                        String wallet_amount = submain.getString("wallet_amount");
                        String checkInCity = submain.getString("incity");

                        String free_trip = submain.getString("free_trip");
                        String no_discount = submain.getString("no_discount");


                        SharedPreferences sharedpreferences,navigateLocation;
                        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        navigateLocation = getSharedPreferences("NavigateLocation", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        SharedPreferences.Editor navigateLocationEditor = navigateLocation.edit();
                        navigateLocationEditor.clear().apply();
                        editor.putString("payable_amount", payable_amount);
                        editor.putString("startkm", startkm);
                        editor.putString("endkm", endkm);
                        editor.putString("ride_amount", ride_amount);
                        editor.putString("totalkm", totalkm);
                        editor.putString("estimated_price", estimated_price);
                        editor.putString("acual_price", acual_price);
                        editor.putString("total_time", total_time);
                        editor.putString("border_tax", border_tax);
                        editor.putString("tall_tax", tall_tax);
                        editor.putString("permission_charger", permission_charger);
                        editor.putString("parking_charger", parking_charger);
                        editor.putString("driver_allowance", driver_allowance);
                        editor.putString("driver_night_hold_charge", driver_night_hold_charge);
                        editor.putString("finalBill", "yes");
                        editor.putString("isridestart", "");
                        editor.putString("startaddss", "");
                        editor.putString("endaddss", "");
                        editor.putString("cust_start_driver", "");
                        editor.putString("cust_end_driver", "");
                        editor.putString("is_autop_tripfrompayload", "");
                        editor.putString("start_otp1frompayload", "");
                        editor.putString("whaitingaddress_one", "");
                        editor.putString("whaitingcustpayload", "");
                        editor.putString("whaitingmessageTO", "");
                        editor.putString("wallet_amount", wallet_amount);
                        editor.putString("checkInCity", checkInCity);
                        editor.putString("free_trip", free_trip);
                        editor.putString("no_discount", no_discount);
                        editor.apply();
                        ;
                        if (submain.has("discount")) {
                            String discount = submain.getString("discount");
                            editor.putString("discount", discount);
                            editor.apply();
                            ;
                        }
                        String signature_image = submain.getString("signature_image");
                        if (signature_image.equalsIgnoreCase("yes")) {
                            Intent i = new Intent(FinishRideActivity.this, SignatureCompanyActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }else {
                            Intent i = new Intent(FinishRideActivity.this, FinalBillActivity.class);
                            startActivity(i);
                            finishAffinity();
                        }
                    } else {
                        Toast.makeText(FinishRideActivity.this, status, Toast.LENGTH_LONG).show();
                        endslider.showResultIcon(false, true);
                    }
                    progstop();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progstop();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);

                endslider.showResultIcon(false, true);
                progstop();
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan);
                Log.e(TAG, "Bearer " + logtokan);
                return params;
            }

            //Pass Your Parameters here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Log.e("end_otp", endotp.getText().toString().trim());
                Log.e("end_km", endkm.getText().toString().trim());
                Log.e("tall_tax", talltax.getText().toString().trim());
                Log.e("border_tax", bodertax.getText().toString().trim());
                Log.e("permission_charger", parmitioncharge.getText().toString().trim());
                Log.e("parking_charger", parkingchage.getText().toString().trim());
                Log.e("driver_allowance", driverallowance.getText().toString().trim());
                Log.e("driver_night_", driverhold.getText().toString().trim());
                Log.e("trip_id", trip_id);

                params.put("end_otp","0000");
                params.put("end_km", endkm.getText().toString().trim());
                params.put("tall_tax", talltax.getText().toString().trim());
                params.put("border_tax", bodertax.getText().toString().trim());
                params.put("permission_charger", parmitioncharge.getText().toString().trim());
                params.put("parking_charger", parkingchage.getText().toString().trim());
                params.put("driver_allowance", driverallowance.getText().toString().trim());
                params.put("driver_night_hold_charge", driverhold.getText().toString().trim());
                params.put("trip_id", trip_id);

                Log.e(TAG, " Finish trip Params ------->> " + new Gson().toJson(params));

                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
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
}
