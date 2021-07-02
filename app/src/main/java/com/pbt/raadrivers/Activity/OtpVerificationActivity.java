package com.pbt.raadrivers.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.pbt.raadrivers.BuildConfig;
import com.pbt.raadrivers.Dialog.CustomDialogClass;
import com.pbt.raadrivers.Dialog.EnterMailDialog;
import com.pbt.raadrivers.Fonts.CButton;
import com.pbt.raadrivers.Fonts.CTextView;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Receiver.SmsReceiver;
import com.pbt.raadrivers.Utils.ConnectivityReceiver;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.philio.pinentry.PinEntryView;

import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;


public class OtpVerificationActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, AppConstant {

    private static final String TAG = "OtpVerificationActivity";
    PinEntryView pinEntryView;
    CButton btn_verify;
    CTextView tv_resend, tv_timer, tv_mob, txtSendMail;
    String MobileNumber = "";
    private FirebaseAuth mAuth;
    private String OtpVerificationActivityId;
    String  trip_id, logtokan, role = "";
    public static String fireregId = "";
    SharedPreferences sharedpreferences;
    private static final int REQ_USER_CONSENT = 200;

    private Boolean network = Boolean.TRUE;
    private ConnectivityReceiver connectivityReceiver;

//    SmsOtpReceiver smsOtpReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otp_varification);


//        smsOtpReceiver = new SmsOtpReceiver();
//        IntentFilter updateAlertIntentFilter = new IntentFilter();
//        updateAlertIntentFilter.addAction(SmsReceiver.SMSRECEIVE);
//        registerReceiver(smsOtpReceiver, updateAlertIntentFilter);

        connectivityReceiver = new ConnectivityReceiver();
        connectivityReceiver.addListener(this);
        this.registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        MyApplication.setCurrentActivity(this);

        mAuth = FirebaseAuth.getInstance();

        pinEntryView = findViewById(R.id.otp_value1);
        tv_resend = findViewById(R.id.tv_resend);
        tv_mob = findViewById(R.id.tv_mob);
        tv_timer = findViewById(R.id.tv_timer);
        btn_verify = findViewById(R.id.btn_verify);
        txtSendMail = findViewById(R.id.txtSendMail);

        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        fireregId = prefs.getString("regId", VEFICATION);
        if (fireregId.isEmpty() && fireregId.trim().length() > 0) {

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful())
                                return;
                            fireregId = task.getResult().getToken();
                        }
                    });
        } else 
            fireregId = prefs.getString("regId", VEFICATION);

        if (!TextUtils.isEmpty(fireregId))
            sharedpreferences = getSharedPreferences("REGISTER", Context.MODE_PRIVATE);
        if (sharedpreferences != null)
            MobileNumber = sharedpreferences.getString("mobile", "");

        if (!MobileNumber.isEmpty()) {
            tv_mob.setText(" +91" + MobileNumber);
            btn_verify.setEnabled(true);
        } else {
            if (getIntent() != null && getIntent().getStringExtra("mobile") != null) {
                tv_mob.setText(" +91" + getIntent().getStringExtra("mobile"));
                MobileNumber = getIntent().getStringExtra("mobile");
            }
        }

        btn_verify.setEnabled(true);

        setCountDonw();
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = pinEntryView.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    Toast.makeText(OtpVerificationActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    btn_verify.setEnabled(false);
                    progstart(OtpVerificationActivity.this, "Loading...", "Loading...");
                    verfiyOTP(code, MobileNumber);
                }
//                apicallingchecknum();
            }
        });

        txtSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterMailDialog cdd = new EnterMailDialog(OtpVerificationActivity.this, MobileNumber);
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                cdd.show();
            }
        });

        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCountDonw();
                progstart(OtpVerificationActivity.this, "Loading...", "Loading...");
                call_api("+91" + MobileNumber);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                pinEntryView.setText(message.substring(0, 6));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityReceiver.removeListener(this);
        this.unregisterReceiver(connectivityReceiver);

//        if (smsOtpReceiver != null) {
//            unregisterReceiver(smsOtpReceiver);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void call_api(final String mob) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, enterphone,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.i("asdf", "response" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                Toast.makeText(OtpVerificationActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OtpVerificationActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            progstop();
                        } catch (Exception e) {
                            e.printStackTrace();
                            progstop();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progstop();
                        Log.e(TAG,"Error.Response : "+ error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", mob);
                return params;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);

    }

    private void verfiyOTP(final String code, String mobileNumber) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, phoneverify,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, " PhoneVerification response : " + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                Toast.makeText(OtpVerificationActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                                apicallingchecknum();
                            } else {
                                Toast.makeText(OtpVerificationActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            btn_verify.setEnabled(true);

                        } catch (Exception e) {
                            progstop();
                            e.printStackTrace();
                        }
                        progstop();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        progstop();
                        Log.e(TAG,"Error.Response : "+error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", MobileNumber);
                params.put("otp", code);
                return params;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);

    }

    private void setCountDonw() {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                tv_timer.setText("Wait for OTP : " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
                tv_resend.setEnabled(false);
            }
            public void onFinish() {
                tv_timer.setText("Time Out");
                tv_resend.setEnabled(true);
            }
        }.start();

    }

    private void apicallingchecknum() {

        StringRequest postRequest = new StringRequest(Request.Method.POST, checkphone,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.i(TAG,"CheckPhone Response : "+ response);
                        try {
                            progstop();
                            btn_verify.setEnabled(true);
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("success")) {

                                JSONObject success = jsonObject.getJSONObject("success");

                                if (jsonObject.getJSONObject("success").getString("msg").equalsIgnoreCase("Success")) {

                                    logtokan = success.getString("token");
                                    role = success.getString("role");
                                    JSONObject user = success.getJSONObject("user");
                                    String id, first_name, last_name, email, email_verified_at, image, license, phone, address, pincode, city, state, birthdate, created_at, updated_at, photo_proof, driver_mode;
                                    id = user.getString("id");
                                    first_name = user.getString("first_name");
                                    last_name = user.getString("last_name");
                                    email = user.getString("email");
                                    email_verified_at = user.getString("email_verified_at");
                                    image = user.getString("image");

                                    Log.e("ImgageUrl","Image Url : "+image );
                                    license = user.getString("license");
                                    photo_proof = user.getString("photo_proof");
                                    phone = user.getString("phone");
                                    //driver_mode = user.getString("driver_mode");
                                    SharedPreferences sharedpreferences;
                                    sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("tokan", logtokan);
                                    editor.putString("userid", id);
                                    editor.putString("role", role);
                                    editor.putString("first_name", first_name);
                                    editor.putString("last_name", last_name);
                                    editor.putString("email", email);
                                    editor.putString("email_verified_at", email_verified_at);
                                    editor.putString("image", image);
                                    editor.putString("license", license);
                                    editor.putString("phone", phone);
                                    editor.putBoolean("hasLoggedIn", true);
                                    editor.putString("photo_proof", photo_proof);
                                    if (role.equalsIgnoreCase("driver")) {
                                        JSONObject driver = success.getJSONObject("cars");
                                        editor.putString("car_id", driver.getString("id"));
                                        editor.putString("car_number", driver.getString("carnumber"));
                                        editor.putString("car_name", driver.getString("name"));
                                        editor.putString("fueltype", driver.getString("fueltype"));
                                        editor.putString("car_type", driver.getString("car_type"));
                                        editor.putString("raadarbar_car", driver.getString("raadarbar_car"));
                                        editor.putInt("dutymode", success.getInt("driver_mode"));
                                    }
                                    editor.apply();
                                    if (logtokan.equalsIgnoreCase("")) {
                                        if ("driver".equalsIgnoreCase(role)) {
                                            trip_id = sharedpreferences.getString("trip_id", "");
                                        } else {
                                            trip_id = sharedpreferences.getString("KEY_PREF_TRIP_ID", "");
                                        }
                                        if (!trip_id.equalsIgnoreCase("")) {
                                            if (network) {
                                                callapiforgettingstatus();
                                            } else {
                                            }
                                        } else {
                                            main();
                                        }
                                    } else
                                        new CheckStatus(OtpVerificationActivity.this, "", role).execute();
                                }
                            }else if(jsonObject.has("success") && jsonObject.has("user")){

                            } else if (jsonObject.has("error")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                                Intent i = new Intent(OtpVerificationActivity.this, PhoneNumberVerifyActivity.class);
                                startActivity(i);
                                finishAffinity();
                            }
                        } catch (Exception e) {
                            Toast.makeText(OtpVerificationActivity.this, "Exception --->> " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG,"Exception : "+ e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,"Error.Response : "+ error.getMessage());
                        progstop();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                SharedPreferences prefsFireBase = getSharedPreferences("FireBaseToken", MODE_PRIVATE);
                if (fireregId != null && !fireregId.isEmpty())
                    params.put("fireregId", fireregId);
                else
                    params.put("fireregId", prefsFireBase.getString("regId", VEFICATION));
                params.put("phone", MobileNumber);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(OtpVerificationActivity.this);
        queue.add(postRequest);
    }

    private void call_api_firsttrip() {
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        StringRequest postRequest = new StringRequest(Request.Method.POST, firsttrip,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.i(TAG, "response" + response);

                        try {
                            JSONArray array = new JSONArray(response);
                            if (array.length() == 0) {
                                main();
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    int result = object.getInt("id");
                                    trip_id = String.valueOf(result);

                                    if (!trip_id.equalsIgnoreCase("")) {
                                        //for Cheking application or trip stetus
                                        if (network) {
                                            callapiforgettingstatus();
                                        } else {
                                        }
                                    } else {
                                        main();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG,"Exception "+ e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e(TAG,"Error.Response : "+error.toString());
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
                params.put("fireregId", prefsFireBase.getString("regId", VEFICATION));
                return params;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }
    public void callapiforgettingstatus() {

        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        progstart(OtpVerificationActivity.this, "Loading...", "Please wait while fetching your trip details...");

        StringRequest postRequest = new StringRequest(Request.Method.POST, gettripstatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.i(TAG,"Response : "+ response);

                        try {
                            JSONObject responce = new JSONObject(response);
                            JSONObject success = responce.getJSONObject("success");
                            String trip_status = success.getString("trip_status");
                            if (trip_status.equalsIgnoreCase("just_booked") || trip_status.equalsIgnoreCase("waiting_for_driver") || trip_status.equalsIgnoreCase("started")) {
                                SharedPreferences sharedpreferences;
                                sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("isridestart", "yes");
                                String start = success.getString("start_lat") + "," + success.getString("start_long");
                                String end = success.getString("end_lat") + "," + success.getString("end_long");

                                editor.putString("startaddss", start);
                                editor.putString("endaddss", end);
                                editor.putString("trip_id", trip_id);

                                editor.putString("Driver_id", success.getString("driver_id"));
                                editor.putString("Driver_namecws", success.getString("driver_first_name"));
                                editor.putString("phonecws", success.getString("driver_phone"));
                                editor.putString("driver_latcws", success.getString("last_lat"));
                                editor.putString("driver_lngcws", success.getString("last_long"));
                                editor.putString("trip_start_latcws", success.getString("start_lat"));
                                editor.putString("trip_end_latcws", success.getString("start_long"));
                                editor.putString("trip_start_otpcws", success.getString("start_otp"));
                                editor.putString("carnumbercws", success.getString("carnumber"));
                                editor.putString("driver_photocws", success.getString("image"));
                                editor.putString("car_namecws", success.getString("carname"));
                                editor.putString("car_image_textcws", success.getString("carimage"));
                                editor.putString("trip_status", success.getString("trip_status"));
                                editor.putString("start_point", success.getString("start_point"));
                                editor.putString("end_point", success.getString("end_point"));
                                editor.putString("cust_address", success.getString("start_point"));
                                editor.putString("STRATKM", success.getString("start_km"));

                                editor.putString("cust_name_driver", success.getString("customer_first_name"));
                                editor.putString("cust_number_driver", success.getString("customer_phone"));
                                editor.putString("incity", success.getString("incity"));
                                editor.putString("roundtrip", success.getString("roundtrip"));
                                editor.apply();
                                editor.putString("startaddss", start);
                                editor.putString("endaddss", end);

                            } else {
                                SharedPreferences sharedpreferences;
                                sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("finalBill", "");
                                editor.putString("trip_status", "");
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
                                editor.remove("KEY_PREF_TRIP_ID");

                                editor.remove("trip_id");
                                editor.putString("Driver_namecws", "");
                                editor.putString("phonecws", "");
                                editor.putString("cash123", "");
                                editor.putString("driver_latcws", "");
                                editor.putString("driver_lngcws", "");
                                editor.putString("trip_start_latcws", "");
                                editor.putString("trip_end_latcws", "");
                                editor.putString("trip_start_otpcws", "");
                                editor.putString("carnumbercws", "");
                                editor.putString("driver_photocws", "");
                                editor.putString("car_namecws", "");
                                editor.putString("car_image_textcws", "");
                                editor.putString("finalstartlatcws", "");
                                editor.putString("finalstartlongcws", "");
                                editor.putString("finalendlatcws", "");
                                editor.putString("finalendlongcws", "");
                                editor.remove("finalcws");
                                editor.putString("message1cws", "");
                                editor.remove("isridestart");
                                editor.putString("finalBill", "");
                                editor.putString("endotpnot", "");
                                editor.putString("messagenoti", "");
                                editor.putString("page", "");
                                editor.putString("message", "");
                                editor.putString("payload", "");

                                editor.apply();
                            }
                            main();
                            progstop();
                        } catch (Exception e) {
                            Log.e(TAG,"Exception : "+ e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,"Error.Response : "+ error.toString());
                        progstop();
                        main();
                        Toast.makeText(OtpVerificationActivity.this, getResources().getString(R.string.errormsg), Toast.LENGTH_LONG).show();
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
                params.put("trip_id", trip_id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

    private void main() {
        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        role = sharedpreferences.getString("role", "");
        if (role.equalsIgnoreCase("driver")) {
            Toast.makeText(OtpVerificationActivity.this, "Successfully Verify ", Toast.LENGTH_LONG).show();
            Intent i = new Intent(OtpVerificationActivity.this, DrivarHomePageActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }else{
            Toast.makeText(OtpVerificationActivity.this, "Sorry this is not Customer Application", Toast.LENGTH_LONG).show();

            Log.e(TAG," main CAll not user  -->>  ");
        }
    }

    @Override
    public void networkAvailable() {
        network = Boolean.TRUE;
    }

    @Override
    public void networkUnavailable() {
        network = Boolean.FALSE;
    }

    public class CheckStatus extends AsyncTask<String, String, String> {
        Activity activity;
        String tripId;
        String role;

        public CheckStatus(Activity activity, String tripId, String role) {
            this.activity = activity;
            this.tripId = tripId;
            this.role = role;
        }

        @Override
        protected String doInBackground(String... strings) {

            StringRequest postRequest = new StringRequest(Request.Method.POST, checkblock,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            try {
                                if (!response.isEmpty() && response.trim().equals("0")) {
                                    call_api_firsttrip();
                                } else {
                                    CustomDialogClass cdd = new CustomDialogClass(activity, "", "");
                                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                    cdd.show();
                                }

                            } catch (Exception e) {
                                Log.e(TAG,"Exception : "+ e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG,"Error.Response : "+ error.getMessage());
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
                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    PackageInfo packageInfo = null;
                    try {
                        packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    String version_name = packageInfo.versionName;

                    String manufacturer = Build.MANUFACTURER;
                    String model = Build.MODEL;
                    String modelname = manufacturer + " " + model;
                    String osversion = Build.VERSION.RELEASE;
                    params.put("m_model", modelname);
                    params.put("m_os", osversion);
                    params.put("m_version", version_name);
                    Log.e(TAG, " CheckStatus  param : " + new Gson().toJson(params));
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(activity.getApplicationContext());
            queue.add(postRequest);
            return null;
        }
    }

//    public class SmsOtpReceiver extends BroadcastReceiver {
//        private final String TAG = SmsReceiver.class.getSimpleName();
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            Log.e(TAG, " SmsOtpReceiver --->>>> " + intent.getExtras().getString("SMSRECEIVE"));
//
//
//            if(intent != null && intent.getStringExtra("SMSRECEIVE")!= null){
//                pinEntryView.setText(intent.getStringExtra("SMSRECEIVE").substring(0, 6));
//            }
//        }
//    }
}
