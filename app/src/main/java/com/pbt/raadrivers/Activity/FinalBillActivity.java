package com.pbt.raadrivers.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.pbt.raadrivers.Utils.ConnectivityReceiver;
import com.pbt.raadrivers.Utils.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.shadowfax.proswipebutton.ProSwipeButton;

import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;

public class FinalBillActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, AppConstant {

    String payable_amount = "", startkm = "", endkm = "", totalkm = "", estimated_price = "", acual_price = "", total_time = "", border_tax = "", tall_tax = "", permission_charger = "", parking_charger = "", driver_allowance = "", discount_text = "", driver_night_hold_charge = "", ride_amount = "", trip_id = "";

    TextView payable_amount_text, startkm_text, endkm_text, totalkm_text, estimated_price_text, acual_price_text, total_time_text, border_tax_text, tall_tax_text, permission_charger_text, parking_charger_text, driver_allowance_text, driver_night_hold_charge_text, acual_price_text1, border_tax_text1, tall_tax_text1, permission_charger_text1, parking_charger_text1, discount, driver_allowance_text1, driver_night_hold_charge_text1;
    ProSwipeButton confirm_amount;
    RelativeLayout discountrel;
    boolean mrrhope = false;
    String wallet_amount, free_trip, checkInCity,no_discount;
    TextView textWalletAmount;

    String payable_amount_temp = "";
    RelativeLayout loutTempAmount;
    TextView text_temp_payamount, titlewalletamount;

    private Boolean network = Boolean.TRUE;
    private ConnectivityReceiver connectivityReceiver;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_bill);

        MyApplication.setCurrentActivity(this);

        connectivityReceiver = new ConnectivityReceiver();
        connectivityReceiver.addListener(this);
        this.registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        discount = (TextView) findViewById(R.id.discount);
        discountrel = (RelativeLayout) findViewById(R.id.discountrel);
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        payable_amount = sharedpreferences.getString("payable_amount", "");
        payable_amount_temp = sharedpreferences.getString("payable_amount", "");
        startkm = sharedpreferences.getString("startkm", "");
        endkm = sharedpreferences.getString("endkm", "");
        totalkm = sharedpreferences.getString("totalkm", "");
        estimated_price = sharedpreferences.getString("estimated_price", "");
        acual_price = sharedpreferences.getString("acual_price", "");
        total_time = sharedpreferences.getString("total_time", "");
        border_tax = sharedpreferences.getString("border_tax", "");
        tall_tax = sharedpreferences.getString("tall_tax", "");
        permission_charger = sharedpreferences.getString("permission_charger", "");
        parking_charger = sharedpreferences.getString("parking_charger", "");
        driver_allowance = sharedpreferences.getString("driver_allowance", "");
        driver_night_hold_charge = sharedpreferences.getString("driver_night_hold_charge", "");
        ride_amount = sharedpreferences.getString("ride_amount", "");
        wallet_amount = sharedpreferences.getString("wallet_amount", "");
        free_trip = sharedpreferences.getString("free_trip", "");
        checkInCity = sharedpreferences.getString("checkInCity", "");
        no_discount = sharedpreferences.getString("no_discount", "");

        try {
            discount_text = sharedpreferences.getString("discount", "");
            Log.e("discount_text", discount_text);

            if (discount_text.equalsIgnoreCase("")) {
                discountrel.setVisibility(View.GONE);
            } else {
                discountrel.setVisibility(View.VISIBLE);
                discount.setText(Html.fromHtml("- <b>" + discount_text));
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("discount", "");
                editor.apply();
            }

        } catch (Exception e) {
            Log.e("Exwption", e.toString());
        }
        Log.e("ride_amount", ride_amount);

        confirm_amount = findViewById(R.id.confirm_amount);
        payable_amount_text = findViewById(R.id.price1);
        startkm_text = findViewById(R.id.startkm);
        endkm_text = findViewById(R.id.endkm);
        totalkm_text = findViewById(R.id.totalkm);
        estimated_price_text = findViewById(R.id.estimated_price);
        acual_price_text = findViewById(R.id.acual_price);
        total_time_text = findViewById(R.id.total_time);
        border_tax_text = findViewById(R.id.border_tax);
        tall_tax_text = findViewById(R.id.tall_tax);
        permission_charger_text = findViewById(R.id.permission_charger);
        parking_charger_text = findViewById(R.id.parking_charger);
        driver_allowance_text = findViewById(R.id.driver_allowance);
        driver_night_hold_charge_text = findViewById(R.id.driver_night_hold_charge);
        acual_price_text1 = findViewById(R.id.acual_price1);
        border_tax_text1 = findViewById(R.id.border_tax1);
        tall_tax_text1 = findViewById(R.id.tall_tax1);
        permission_charger_text1 = findViewById(R.id.permission_charger1);
        parking_charger_text1 = findViewById(R.id.parking_charger1);
        driver_allowance_text1 = findViewById(R.id.driver_allowance1);
        driver_night_hold_charge_text1 = findViewById(R.id.driver_night_hold_charge1);
        loutTempAmount = findViewById(R.id.loutTempAmount);
        text_temp_payamount = findViewById(R.id.text_temp_payamount);
        titlewalletamount = findViewById(R.id.titlewalletamount);

        textWalletAmount = findViewById(R.id.textWalletAmount);

//        if (checkInCity.equalsIgnoreCase("1")) {
//
//            if (Integer.valueOf(totalkm) >= 121) {
//
//                Log.e("FinalBill","no_discount : "+no_discount);
//
//                if(!no_discount.equals("-1")) {
//
//                    Double basePrice = Double.valueOf(ride_amount) + 500;
//
//                    if (ride_amount.equalsIgnoreCase("") || ride_amount.equalsIgnoreCase("0")) {
//                        acual_price_text.setVisibility(View.GONE);
//                        acual_price_text1.setVisibility(View.GONE);
//                    } else {
//                        acual_price_text.setText(Html.fromHtml("₹ <b>" + basePrice));
//                    }
//                    titlewalletamount.setText("Outcity Discount");
//                    textWalletAmount.setText("" + 500);
//                }else{
//                    if (ride_amount.equalsIgnoreCase("") || ride_amount.equalsIgnoreCase("0")) {
//                        acual_price_text.setVisibility(View.GONE);
//                        acual_price_text1.setVisibility(View.GONE);
//                    } else {
//                        titlewalletamount.setVisibility(View.GONE);
//                        textWalletAmount.setVisibility(View.GONE);
//                        acual_price_text.setText(Html.fromHtml("₹ <b>" + ride_amount));
//                    }
//
//                }
//            } else {
//
//                textWalletAmount.setVisibility(View.GONE);
//                titlewalletamount.setVisibility(View.GONE);
//
//                if (ride_amount.equalsIgnoreCase("") || ride_amount.equalsIgnoreCase("0")) {
//                    acual_price_text.setVisibility(View.GONE);
//                    acual_price_text1.setVisibility(View.GONE);
//                } else {
//                    acual_price_text.setText(Html.fromHtml("₹ <b>" + ride_amount));
//                }
//            }
//
//        } else {

            if (ride_amount.equalsIgnoreCase("") || ride_amount.equalsIgnoreCase("0")) {
                acual_price_text.setVisibility(View.GONE);
                acual_price_text1.setVisibility(View.GONE);
            } else {
                acual_price_text.setText(Html.fromHtml("₹ <b>" + ride_amount));
            }
            textWalletAmount.setText("" + wallet_amount);
//        }

        if (checkInCity.equalsIgnoreCase("0")) {

            if (Double.parseDouble(payable_amount) <= Double.parseDouble(wallet_amount)) {
                payable_amount = String.valueOf(0);
                text_temp_payamount.setText("" + payable_amount);
            } else if (Double.parseDouble(wallet_amount) < Double.parseDouble(payable_amount)) {
                payable_amount = String.valueOf(Double.parseDouble(payable_amount) - Double.parseDouble(wallet_amount));
                text_temp_payamount.setText("" + payable_amount);
            }
        } else {
            text_temp_payamount.setText("" + payable_amount_temp);
        }

        if (!payable_amount.equalsIgnoreCase("")) {
            payable_amount_text.setText(Html.fromHtml("₹ <b>" + payable_amount_temp));
        }

        if (startkm.equalsIgnoreCase("") || startkm.equalsIgnoreCase("0")) {
            startkm_text.setVisibility(View.GONE);
        } else {
            startkm_text.setText(Html.fromHtml("Start KM: <b>" + startkm));
        }
        if (endkm.equalsIgnoreCase("") || endkm.equalsIgnoreCase("0")) {
            endkm_text.setVisibility(View.GONE);
        } else {
            endkm_text.setText(Html.fromHtml("END KM: <b>" + endkm));
        }
        if (totalkm.equalsIgnoreCase("") || totalkm.equalsIgnoreCase("0")) {
            totalkm_text.setVisibility(View.GONE);
        } else {
            totalkm_text.setText(Html.fromHtml("Total KM: <b>" + totalkm));
        }

        Log.e("acual_price", acual_price);
        Log.e("ride_amount", ride_amount);

        if (total_time.equalsIgnoreCase("") || total_time.equalsIgnoreCase("0")) {
            total_time_text.setVisibility(View.GONE);
        } else {
            total_time_text.setText(Html.fromHtml("Total travel time: <b>" + total_time));
        }
        if (border_tax.equalsIgnoreCase("") || border_tax.equalsIgnoreCase("0")) {
            border_tax_text.setVisibility(View.GONE);
            border_tax_text1.setVisibility(View.GONE);
        } else {
            border_tax_text.setText(Html.fromHtml("₹ <b>" + border_tax));
        }
        if (tall_tax.equalsIgnoreCase("") || tall_tax.equalsIgnoreCase("0")) {
            tall_tax_text.setVisibility(View.GONE);
            tall_tax_text1.setVisibility(View.GONE);
        } else {
            tall_tax_text.setText(Html.fromHtml("₹ <b>" + tall_tax));
        }
        if (permission_charger.equalsIgnoreCase("") || permission_charger.equalsIgnoreCase("0")) {
            permission_charger_text.setVisibility(View.GONE);
            permission_charger_text1.setVisibility(View.GONE);
        } else {
            permission_charger_text.setText(Html.fromHtml("₹ <b>" + permission_charger));
        }
        if (parking_charger.equalsIgnoreCase("") || parking_charger.equalsIgnoreCase("0")) {
            parking_charger_text.setVisibility(View.GONE);
            parking_charger_text1.setVisibility(View.GONE);
        } else {
            parking_charger_text.setText(Html.fromHtml("₹ <b>" + parking_charger));
        }
        if (driver_allowance.equalsIgnoreCase("") || driver_allowance.equalsIgnoreCase("0")) {
            driver_allowance_text.setVisibility(View.GONE);
            driver_allowance_text1.setVisibility(View.GONE);
        } else {
            driver_allowance_text.setText(Html.fromHtml("₹ <b>" + driver_allowance));
        }
        if (driver_night_hold_charge.equalsIgnoreCase("") || driver_night_hold_charge.equalsIgnoreCase("0")) {
            driver_night_hold_charge_text.setVisibility(View.GONE);
            driver_night_hold_charge_text1.setVisibility(View.GONE);
        } else {
            driver_night_hold_charge_text.setText(Html.fromHtml("₹ <b>" + driver_night_hold_charge));
        }

        confirm_amount.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                if (network) {
                    callapi();
                } else {
                    Toast.makeText(FinalBillActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void callapi() {

        progstart(FinalBillActivity.this, "Loading...", "Loading...");

        final String logtokan;
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        trip_id = prefs.getString("trip_id", trip_id);

        StringRequest request = new StringRequest(Request.Method.POST, confirmendtrip, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("FinalBill", " confirmedTrip : ---->>>   " + response);
                confirm_amount.showResultIcon(true, false);
                try {
                    JSONObject main = new JSONObject(response);
                    JSONObject submain = main.getJSONObject("success");
                    String success = submain.getString("status");
                    if (success.equalsIgnoreCase("success")) {
                        mrrhope = true;

                        SharedPreferences sharedpreferences;
                        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("finalBill", "");
                        editor.putString("isridestart", "");
                        editor.putString("startaddss", "");
                        editor.putString("endaddss", "");
                        editor.putString("whaitingaddress_one", "");
                        editor.putString("whaitingcustpayload", "");
                        editor.putString("cust_name_driver", "");
                        editor.putString("cust_number_driver", "");
                        editor.putString("page", "");
                        editor.putString("is_autop_tripfrompayload", "");
                        editor.putString("start_otp1frompayload", "");
                        editor.putString("message", "");
                        editor.putString("payload", "");
                        editor.putString("is_auto_endotp", "");
                        editor.putString("trip_status", "");
                        editor.apply();

                        Intent i = new Intent(FinalBillActivity.this, ThanksScreenActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(FinalBillActivity.this, success, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Exception final ", "JSONException -->> " + e.toString());

                } catch (Exception e) {
                    Log.e("Exception final", e.toString());
                }
                progstop();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);
                confirm_amount.showResultIcon(false, true);
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

            //Pass Your Parameters here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("trip_id", trip_id);
                params.put("wallet_amount", wallet_amount);

                Log.e("Authorization", "trip_id " + trip_id);

                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        if (mrrhope) {
            Intent i = new Intent(FinalBillActivity.this, DrivarHomePageActivity.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(FinalBillActivity.this, FinishRideActivity.class);
            startActivity(i);
            finish();
        }
        super.onBackPressed();
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
