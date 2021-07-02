package com.pbt.raadrivers.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.pbt.raadrivers.Notification.NotificationUtils;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.MyApplication;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;

public class AssignDriverActivity extends AppCompatActivity implements AppConstant {
    SharedPreferences sharedpreferences;
    String logtokan1, driver_id, car_id, sec_driverid;
    TextView sec_name, sec_email, sec_phone;
    CircleImageView user_avatar;
    Button assignbtn, backbutton, assigntomebtn, detail, backbutton1;
    LinearLayout notfound_lay, normal_lay;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AssignDriverActivity.this, DrivarHomePageActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_driver);

        MyApplication.setCurrentActivity(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
//        driver_id = "1288";
        driver_id = sharedpreferences.getString("userid", "");
        Log.e("asdf", "driver_id  " + driver_id);

        sec_name = findViewById(R.id.tv0_val);
        sec_email = findViewById(R.id.tv1_val);
        sec_phone = findViewById(R.id.tv2_val);
        user_avatar = findViewById(R.id.user_avatar);
        assignbtn = findViewById(R.id.assign_btn);
        assigntomebtn = findViewById(R.id.assigntome_btn);
        backbutton = findViewById(R.id.backbutton);
        normal_lay = findViewById(R.id.normal_lay);
        notfound_lay = findViewById(R.id.notfound_lay);
        detail = findViewById(R.id.detail);
        backbutton1 = findViewById(R.id.backbutton1);

        getDriverDetail();
        secondarydrivercheck();

        assignbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String isridestart = sharedpreferences.getString("isridestart", "");
                if (isridestart.equalsIgnoreCase("yes")) {
                    Toast.makeText(AssignDriverActivity.this, getResources().getString(R.string.updatestringfordriver), Toast.LENGTH_LONG).show();
                } else {
                    showSettingsAlert();
                }
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        backbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AssignDriverActivity.this);
        alertDialog.setTitle(getResources().getString(R.string.confirmation));
        alertDialog.setMessage(getResources().getString(R.string.sure));
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                secondarydriverupdate();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void showSettingsAlertlogout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AssignDriverActivity.this);
        alertDialog.setTitle(getResources().getString(R.string.confirmation));
        alertDialog.setMessage(getResources().getString(R.string.clogout));
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                logoutclass();
            }
        });

        alertDialog.show();
    }

    public void logoutclass() {

        progstart(AssignDriverActivity.this, "Loading...", "Loading...");
        SharedPreferences sharedpreferences = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan1 = sharedpreferences.getString("tokan", "");
        StringRequest request = new StringRequest(Request.Method.POST, logout, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Your Array Response", response);

                SharedPreferences sharedpreferences;
                sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove("tokan");
                editor.remove("role");
                editor.remove("dutymode");
                editor.putBoolean("hasLoggedIn", false);
                editor.apply();

                NotificationUtils.clearNotifications(AssignDriverActivity.this);
                Intent i = new Intent(AssignDriverActivity.this, PhoneNumberVerifyActivity.class);
                startActivity(i);
                finish();
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
                params.put("Authorization", "Bearer " + logtokan1);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }

    private void secondarydriverupdate() {
        logtokan1 = sharedpreferences.getString("tokan", "");
        StringRequest postRequest = new StringRequest(Request.Method.POST, secondarydriverupdate,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("Response driverupdate", response);
                        try {
                            JSONObject main = new JSONObject(response);
                            JSONObject success = main.getJSONObject("success");
                            String status = success.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                showSettingsAlertlogout();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        progstop();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan1);
                Log.e("Authorization", "Bearer " + logtokan1);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("driver_id", sec_driverid);
                params.put("car_id", car_id);

                Log.e("driver_id", driver_id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AssignDriverActivity.this);
        queue.add(postRequest);
    }

    private void secondarydrivercheck() {
        logtokan1 = sharedpreferences.getString("tokan", "");
        StringRequest postRequest = new StringRequest(Request.Method.POST, secondarydrivercheck,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("Response", response);
                        try {
                            JSONObject main = new JSONObject(response);
                            JSONObject success = main.getJSONObject("success");
                            String msg = success.getString("msg");
                            if (msg.equalsIgnoreCase("Details found")) {
                                assignbtn.setVisibility(View.VISIBLE);
                                assigntomebtn.setVisibility(View.GONE);

                                JSONObject data = success.getJSONObject("data");
                                car_id = data.getString("id");
                            } else if (msg.equalsIgnoreCase("Not found")) {
                                assigntomebtn.setVisibility(View.VISIBLE);
                                assignbtn.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        progstop();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan1);
                Log.e("Authorization", "Bearer " + logtokan1);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("driver_id", driver_id);

                Log.e("driver_id", driver_id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AssignDriverActivity.this);
        queue.add(postRequest);
    }

    private void getDriverDetail() {
        logtokan1 = sharedpreferences.getString("tokan", "");
        StringRequest postRequest = new StringRequest(Request.Method.POST, secondarydriverdetails,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("Response", response);
                        try {
                            JSONObject main = new JSONObject(response);
                            JSONObject success = main.getJSONObject("success");
                            String status = success.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                if (success.getString("msg").equalsIgnoreCase("Secondary Driver details not found")) {
                                    notfound_lay.setVisibility(View.VISIBLE);
                                    normal_lay.setVisibility(View.GONE);
                                    detail.setText(success.getString("msg"));
                                    detail.setEnabled(false);
                                    detail.setClickable(false);
                                } else {
                                    notfound_lay.setVisibility(View.GONE);
                                    normal_lay.setVisibility(View.VISIBLE);
                                    detail.setEnabled(true);
                                    detail.setClickable(true);

                                    JSONObject data = success.getJSONObject("data");
                                    sec_driverid = data.getString("sec_driver");
                                    JSONObject driver_data = data.getJSONObject("driver");

                                    sec_email.setText(driver_data.getString("email"));
                                    sec_phone.setText(driver_data.getString("phone"));
                                    sec_name.setText(driver_data.getString("first_name") + " " + driver_data.getString("last_name"));
                                    UrlImageViewHelper.setUrlDrawable(user_avatar, image + "users/thumbnail/" + driver_data.getString("image"), R.drawable.raalogo);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        progstop();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan1);
                Log.e("Authorization", "Bearer " + logtokan1);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("driver_id", driver_id);

                Log.e("driver_id", driver_id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AssignDriverActivity.this);
        queue.add(postRequest);
    }
}
