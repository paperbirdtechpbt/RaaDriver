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
import com.pbt.raadrivers.Adapter.AdapterMissTrip;
import com.pbt.raadrivers.Fonts.CButton;
import com.pbt.raadrivers.Fonts.CTextView;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.ConnectivityReceiver;
import com.pbt.raadrivers.Utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pbt.raadrivers.Utils.Common.progstop;

public class MissedTripActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener,AppConstant {
    SharedPreferences prefs;
    String userid, logtokan, monthName;
    ListView ls1;
    CButton mtselectmonth;
    List<String> categories;
    List<String> categories1;
    Dialog dialog;
    CTextView data;

    private Boolean network = Boolean.TRUE;
    private ConnectivityReceiver connectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missed_trip);

        connectivityReceiver = new ConnectivityReceiver();
        connectivityReceiver.addListener(this);
        this.registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        MyApplication.setCurrentActivity(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ls1 = (ListView) findViewById(R.id.ls1);
        mtselectmonth = (CButton) findViewById(R.id.mtselectmonth);
        data = (CTextView) findViewById(R.id.data);

        categories = new ArrayList<String>();
        categories1 = new ArrayList<String>();

        prefs = getSharedPreferences("Login", MODE_PRIVATE);
        userid = prefs.getString("userid", "");
        Log.e("asdf", "driver_id" + userid);

        Calendar cal = Calendar.getInstance();
        monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
        monthName.toLowerCase();

        mtselectmonth.setText(monthName);
        mtselectmonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });

        if (network) {
            call_service(userid, monthName);
        } else {
            Toast.makeText(MissedTripActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityReceiver.removeListener(this);
        this.unregisterReceiver(connectivityReceiver);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void showPopUp() {
        try {
            dialog = new Dialog(MissedTripActivity.this);
            dialog.setContentView(R.layout.dialog_spinner);
            ListView ls1 = dialog.findViewById(R.id.ls1);
            ArrayAdapter adapter = new ArrayAdapter<String>(MissedTripActivity.this,
                    R.layout.item_monthly, categories1);
            ls1.setAdapter(adapter);
            ls1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (network) {
                        monthName = categories1.get(position);
                        call_service(userid, categories1.get(position));
                        mtselectmonth.setText(categories1.get(position));
                        categories.clear();
                        categories1.clear();
                        dialog.cancel();
                    } else {
                        Toast.makeText(MissedTripActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
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

    private void call_service(final String userid, final String month) {
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        Log.e("logtokan", logtokan);
        StringRequest request = new StringRequest(Request.Method.POST, missedtrip, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("missedtrip", response);
                try {
                    JSONObject main = new JSONObject(response);
                    JSONArray reportData = main.getJSONArray("data");
                    if (reportData.length() == 0) {
                        data.setVisibility(View.VISIBLE);
                        ls1.setVisibility(View.GONE);
                    } else {
                        data.setVisibility(View.GONE);
                        ls1.setVisibility(View.VISIBLE);
                    }
                    String[] date, time, source, destination, customername;
                    date = new String[reportData.length()];
                    time = new String[reportData.length()];
                    source = new String[reportData.length()];
                    destination = new String[reportData.length()];
                    customername = new String[reportData.length()];

                    for (int i = 0; i < reportData.length(); i++) {

                        date[i] = reportData.getJSONObject(i).getString("created_at");
                        source[i] = reportData.getJSONObject(i).getString("start_point");
                        destination[i] = reportData.getJSONObject(i).getString("end_point");
                        customername[i] = reportData.getJSONObject(i).getString("first_name");

                    }
                    AdapterMissTrip adeptor = new AdapterMissTrip(MissedTripActivity.this, date, time, source, destination, customername);
                    adeptor.notifyDataSetChanged();
                    ls1.setAdapter(adeptor);
                    JSONArray monthArray = main.getJSONArray("monthArray");
                    //JSONArray trip_amount = submain.getJSONArray("trip_amount");
                    for (int i = 0; i < monthArray.length(); i++) {
                        categories.add(monthArray.getJSONObject(i).getString("value"));
                        categories1.add(monthArray.getJSONObject(i).getString("month"));
                        // Log.e("monthArray.getJ",monthArray.getJSONObject(i).getString("value"));
                    }

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
                return params;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("driver_id", userid);
                params.put("month", month);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MissedTripActivity.this, DrivarHomePageActivity.class));
        finish();
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
