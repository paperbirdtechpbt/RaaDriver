package com.pbt.raadrivers.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pbt.raadrivers.Adapter.AdapterTimeLog;
import com.pbt.raadrivers.Fonts.CButton;
import com.pbt.raadrivers.Fonts.CTextView;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.ConnectivityReceiver;
import com.pbt.raadrivers.Utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;

public class TimeLogActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, AppConstant {

    ListView tllist;
    CTextView data;
    CButton tlselectmonth;
    String monthName, driver_id;
    List<String> categories;
    List<String> categories1;
    SharedPreferences prefs;

    private Boolean network = Boolean.TRUE;
    private ConnectivityReceiver connectivityReceiver;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TimeLogActivity.this, DrivarHomePageActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_log);

        connectivityReceiver = new ConnectivityReceiver();
        connectivityReceiver.addListener(this);
        this.registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        MyApplication.setCurrentActivity(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prefs = getSharedPreferences("Login", MODE_PRIVATE);
        driver_id = prefs.getString("userid", "");

        tllist = (ListView) findViewById(R.id.tllist);
        data = (CTextView) findViewById(R.id.data);
        tlselectmonth = (CButton) findViewById(R.id.tlselectmonth);

        categories = new ArrayList<String>();
        categories1 = new ArrayList<String>();

        Calendar cal = Calendar.getInstance();
        monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
        monthName.toLowerCase();

        tlselectmonth.setText(monthName);
        tlselectmonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });

        if (network) {
            callapiforcal(monthName);
        } else {
            Toast.makeText(TimeLogActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
        }
    }

    void callapiforcal(final String month) {

        progstart(TimeLogActivity.this, "Loading...", "Loading your Data...");

        final String logtokan;
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        Log.e("logtokan", logtokan);
        StringRequest request = new StringRequest(Request.Method.POST, monthlyDutyReport, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("asdf time log ", response);

                    JSONObject submain = new JSONObject(response);
                    JSONArray reportData = submain.getJSONArray("data");
                    if (reportData.length() == 0) {
                        data.setVisibility(View.VISIBLE);
                        tllist.setVisibility(View.GONE);
                    } else {
                        data.setVisibility(View.GONE);
                        tllist.setVisibility(View.VISIBLE);
                    }
                    String[] date, total_min;

                    date = new String[reportData.length()];
                    total_min = new String[reportData.length()];

                    for (int i = 0; i < reportData.length(); i++) {
                        date[i] = reportData.getJSONObject(i).getString("date");
                        total_min[i] = reportData.getJSONObject(i).getString("total_min");
                    }

                    AdapterTimeLog adeptor = new AdapterTimeLog(TimeLogActivity.this, date, total_min);
                    tllist.setAdapter(adeptor);

                    JSONArray monthArray = submain.getJSONArray("monthArray");
                    for (int i = 0; i < monthArray.length(); i++) {
                        categories.add(monthArray.getJSONObject(i).getString("value"));
                        categories1.add(monthArray.getJSONObject(i).getString("month"));
                    }

                } catch (Exception e) {
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
                return params;
            }

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("month", month);
                params.put("driver_id", driver_id);
                Log.e("month", month);
                Log.e("driver_id", driver_id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    Dialog dialog;

    public void showPopUp() {
        try {
            dialog = new Dialog(TimeLogActivity.this);
            dialog.setContentView(R.layout.dialog_spinner);

            ListView ls1 = dialog.findViewById(R.id.ls1);

            ArrayAdapter adapter = new ArrayAdapter<String>(TimeLogActivity.this,
                    R.layout.item_monthly, categories1);
            ls1.setAdapter(adapter);
            ls1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Toast.makeText(monthly_account.this, categories1.get(position), Toast.LENGTH_LONG).show();
                    if (network) {
                        monthName = categories1.get(position);
                        callapiforcal(categories1.get(position));
                        tlselectmonth.setText(categories1.get(position));
                        categories.clear();
                        categories1.clear();
                        dialog.cancel();
                    } else {
                        Toast.makeText(TimeLogActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
                    }

                }
            });
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int displayWidth = displayMetrics.widthPixels;
            int displayHeight = displayMetrics.heightPixels;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            int dialogWindowWidth = (int) (displayWidth * 0.95f);
            layoutParams.width = dialogWindowWidth;
            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e) {
            Log.e("Exception   sd", e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityReceiver.removeListener(this);
        this.unregisterReceiver(connectivityReceiver);
    }

    @Override
    public void networkAvailable() {
        network = Boolean.TRUE;
    }

    @Override
    public void networkUnavailable() {
        network = Boolean.FALSE;
    }
}
