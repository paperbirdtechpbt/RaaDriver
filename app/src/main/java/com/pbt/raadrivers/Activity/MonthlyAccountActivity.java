package com.pbt.raadrivers.Activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pbt.raadrivers.Adapter.AdapterMonthlyAccount;
import com.pbt.raadrivers.Fonts.CButton;
import com.pbt.raadrivers.Notification.Config;
import com.pbt.raadrivers.Other.FileDownloader;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.ConnectivityReceiver;
import com.pbt.raadrivers.Utils.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pbt.raadrivers.Utils.Common.driver;
import static com.pbt.raadrivers.Utils.Common.hasPermissions;
import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;

public class MonthlyAccountActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, AppConstant {

    List<String> categories;
    List<String> categories1;
    String monthName;
    ListView ls1;
    TextView data, totalkm, totalwallet, tripamnt, totalamount, cash, company, online;
    CButton selectmonth, pdfdownload;
    String filename_final;
    int totalwalletamnt = 0;
    int totalcommisionamnt = 0;
    Context context;

    private static final String TAG = "Monthly Account";
    private static final String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
        startActivity(new Intent(MonthlyAccountActivity.this, DrivarHomePageActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_account);

        connectivityReceiver = new ConnectivityReceiver();
        connectivityReceiver.addListener(this);
        this.registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        MyApplication.setCurrentActivity(this);

        context = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ls1 = (ListView) findViewById(R.id.ls1);
        data = (TextView) findViewById(R.id.data);
        totalkm = (TextView) findViewById(R.id.totalkm);
        totalwallet = (TextView) findViewById(R.id.totalwallet);
        tripamnt = (TextView) findViewById(R.id.tripamnt);
        totalamount = (TextView) findViewById(R.id.totalamount);
        selectmonth = (CButton) findViewById(R.id.selectmonth);
        pdfdownload = (CButton) findViewById(R.id.pdfdownload);
        cash = (TextView) findViewById(R.id.cash);
        company = (TextView) findViewById(R.id.company);
        online = (TextView) findViewById(R.id.online);

        categories = new ArrayList<String>();
        categories1 = new ArrayList<String>();

        selectmonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });

        Calendar cal = Calendar.getInstance();
        monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
        monthName.toLowerCase();

        selectmonth.setText(monthName);

        if (network) {
            callapiforcal(monthName);
        } else {
            Toast.makeText(MonthlyAccountActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
        }

        ActivityCompat.requestPermissions(MonthlyAccountActivity.this, PERMISSIONS, 112);

        pdfdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filename_final = monthName + "_" + "Raadarbar.pdf";
                // new DownloadFile().execute(Common.pdf_download+monthName, filename_final);
                if (!hasPermissions(MonthlyAccountActivity.this, PERMISSIONS)) {
                    Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
                    t.show();
                } else {
                    if (network) {
                        filename_final = monthName + "_" + "Raadarbar.pdf";
                        //new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
                        progstart(MonthlyAccountActivity.this, "Downloading", "Please wait your Bill is Downloading");
                        new DownloadFile().execute(pdf_download + "/" + monthName, filename_final);
                        //downloadpdf(Common.pdf_download + "/" + monthName,filename_final);
                        Log.e("PDF DOWNLPO", pdf_download + "/" + monthName + filename_final);
                    } else {
                        Toast.makeText(MonthlyAccountActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //for the Notification Reciver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String datafromnoti = intent.getStringExtra("datafromnoti");
                    JSONObject datafromnot = new JSONObject(datafromnoti);
                    JSONObject data = datafromnot.getJSONObject("data");

                    try {
                        //driver = 1;
                        int driver = data.getInt("is_driver");
                        if (driver == 1) {
                            String payload = intent.getStringExtra("payload");
                            String message = intent.getStringExtra("message");
                            driver(MonthlyAccountActivity.this, message, payload, datafromnoti);
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

    BroadcastReceiver mRegistrationBroadcastReceiver;

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

    @Override
    protected void onPause() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);

        super.onPause();
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

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File file;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                file = new File(getApplication().getFilesDir(), "Raadarbar");
            } else {
                extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                file = new File(extStorageDirectory, "Raadarbar");
            }

            if (!file.exists()) {
                file.mkdirs();
            }

            File pdfFile = new File(file, fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDownloader.downloadFilewithauth(getApplicationContext(), fileUrl, pdfFile);
            progstop();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progstop();
            Toast.makeText(MonthlyAccountActivity.this, "Download Complete in Storage " + Environment.getExternalStorageDirectory().toString() + "/Raadarbar/" + filename_final, Toast.LENGTH_LONG).show();
            viewpdf();
        }
    }

//    void viewpdf() {
//
//
//        if (!hasPermissions(MonthlyAccountActivity.this, PERMISSIONS)) {
//            Toast t = Toast.makeText(getApplicationContext(), "You don't have read access !", Toast.LENGTH_LONG);
//            t.show();
//
//        } else {
//
//            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Raadarbar/" + filename_final);
//            Intent target = new Intent(Intent.ACTION_VIEW);
//            target.setDataAndType(Uri.fromFile(file),"application/pdf");
//            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//            Intent intent = Intent.createChooser(target, "Open File");
//            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//            StrictMode.setVmPolicy(builder.build());
//            try {
//                context.startActivity(intent);
//            } catch (ActivityNotFoundException e) {
//            }
//        }
//    }

    void viewpdf() {
        if (!hasPermissions(MonthlyAccountActivity.this, PERMISSIONS)) {
            Toast t = Toast.makeText(MonthlyAccountActivity.this, "You don't have read access !", Toast.LENGTH_LONG);
            t.show();
        } else {

            Intent intent = new Intent(this, PDFViewActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                intent.putExtra("file", "/sdcard/Raadarbar/" + filename_final);
            else
                intent.putExtra("file", Environment.getExternalStorageDirectory().getAbsolutePath() + "/Raadarbar/" + filename_final);
            intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);
        }
    }

    double cash_text321 = 0;
    double compny_text321 = 0;
    double online_text321 = 0;

    void callapiforcal(String month) {

        progstart(MonthlyAccountActivity.this, "Loading...", "Loading your Data...");

        final String logtokan;
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        Log.e("logtokan", logtokan);
        Log.e("logtokan", balance + month);
        StringRequest request = new StringRequest(Request.Method.GET, balance + month, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("asdf  ", "month " + response);
                try {
                    JSONObject main = new JSONObject(response);
                    JSONObject submain = main.getJSONObject("success");

                    JSONArray reportData = submain.getJSONArray("reportData");

                    String totalkm_s = submain.getString("totalTripKm");
                    String totalkm_run = submain.getString("run_kilometer");
                    String tripamnt_s = submain.getString("totalDailyAmnt");
                    String totalamount_s = submain.getString("totalTripAmnt");
                    totalkm.setText(Html.fromHtml("Trip km: <b>" + totalkm_s + "<br/>Run km :<b>" + totalkm_run));
                    tripamnt.setText(Html.fromHtml("Daily amount: <b>₹ " + tripamnt_s));
                    totalamount.setText(Html.fromHtml("Trip amount: <b>₹ " + totalamount_s));
                    tripamnt.setTextColor(getResources().getColor(R.color.startgreen));
                    totalamount.setTextColor(getResources().getColor(R.color.appred));
                    // Log.d("Your Array Response", reportData.toString());


                    if (reportData.length() == 0) {
                        data.setVisibility(View.VISIBLE);
                        ls1.setVisibility(View.GONE);
                    } else {
                        data.setVisibility(View.GONE);
                        ls1.setVisibility(View.VISIBLE);
                    }
                    String[] id, driver_id, trip_id, date, time, wallet, daily_amount, trip_amount, created_at, updated_at, kilometer, payment_mode, commission, onhandcash, driver_amount;


                    id = new String[reportData.length()];
                    driver_id = new String[reportData.length()];
                    trip_id = new String[reportData.length()];
                    date = new String[reportData.length()];
                    time = new String[reportData.length()];
                    daily_amount = new String[reportData.length()];
                    trip_amount = new String[reportData.length()];
                    driver_amount = new String[reportData.length()];
                    created_at = new String[reportData.length()];
                    updated_at = new String[reportData.length()];
                    kilometer = new String[reportData.length()];
                    payment_mode = new String[reportData.length()];
                    commission = new String[reportData.length()];
                    onhandcash = new String[reportData.length()];
                    wallet = new String[reportData.length()];

                    List<String> cash_list = new ArrayList<>();
                    ;
                    List<String> company_list = new ArrayList<>();
                    ;
                    List<String> online_list = new ArrayList<>();
                    ;

                    Log.e("reportData", String.valueOf(reportData));
                    for (int i = 0; i < reportData.length(); i++) {
                        id[i] = reportData.getJSONObject(i).getString("id");
                        driver_id[i] = reportData.getJSONObject(i).getString("driver_id");
                        trip_id[i] = reportData.getJSONObject(i).getString("trip_id");
                        date[i] = reportData.getJSONObject(i).getString("date");
                        time[i] = reportData.getJSONObject(i).getString("time");
                        commission[i] = reportData.getJSONObject(i).getString("commission");
                        onhandcash[i] = reportData.getJSONObject(i).getString("onhandcash");
                        driver_amount[i] = reportData.getJSONObject(i).getString("driver_amount");
                        daily_amount[i] = reportData.getJSONObject(i).getString("daily_amount");

                        totalcommisionamnt = totalcommisionamnt + Integer.valueOf(commission[i]);

                        if (reportData.getJSONObject(i).getString("original_trip_amount") != null && !reportData.getJSONObject(i).getString("original_trip_amount").isEmpty() && !reportData.getJSONObject(i).getString("original_trip_amount").equals("null")) {
                            trip_amount[i] = reportData.getJSONObject(i).getString("original_trip_amount");
                        } else {
                            trip_amount[i] = "0";
                        }
                        try {
                            if (reportData.getJSONObject(i).getString("wallet") != null && !reportData.getJSONObject(i).getString("wallet").isEmpty() && !reportData.getJSONObject(i).getString("wallet").equals("null")) {
                                wallet[i] = reportData.getJSONObject(i).getString("wallet");
                                totalwalletamnt = totalwalletamnt + Math.round(Float.parseFloat(reportData.getJSONObject(i).getString("wallet")));
                            } else {
                                wallet[i] = "0";
                                totalwalletamnt = totalwalletamnt + 0;
                                Log.d("totalwallet", String.valueOf(totalwalletamnt));
                            }
                        } catch (Exception e) {
                            Log.d("Exception", e.getLocalizedMessage());
                        }
                        created_at[i] = reportData.getJSONObject(i).getString("created_at");
                        updated_at[i] = reportData.getJSONObject(i).getString("updated_at");

                        if (reportData.getJSONObject(i).getString("kilometer") != null && !reportData.getJSONObject(i).getString("kilometer").isEmpty() && !reportData.getJSONObject(i).getString("kilometer").equals("null")) {
                            kilometer[i] = reportData.getJSONObject(i).getString("kilometer");
                        } else {
                            kilometer[i] = "0";
                        }
                        payment_mode[i] = reportData.getJSONObject(i).getString("payment_mode");

                        if (payment_mode[i].equalsIgnoreCase("cash")) {
                            cash_list.add(trip_amount[i]);
                        } else if (payment_mode[i].equalsIgnoreCase("company")) {
                            company_list.add(trip_amount[i]);
                        } else if (payment_mode[i].equalsIgnoreCase("online")) {
                            online_list.add(trip_amount[i]);
                        } else {
                            cash_list.add(String.valueOf(Double.valueOf(driver_amount[i]) - Double.valueOf(wallet[i])));
                        }
                    }
                    cash_text321 = 0;
                    online_text321 = 0;
                    compny_text321 = 0;
                    try {
                        for (int i = 0; i < cash_list.size(); i++) {
                            cash_text321 += Double.parseDouble(cash_list.get(i));
                        }
                        for (int i = 0; i < online_list.size(); i++) {
                            online_text321 += Double.parseDouble(online_list.get(i));
                        }
                        for (int i = 0; i < company_list.size(); i++) {
                            compny_text321 += Double.parseDouble(company_list.get(i));
                        }
                    } catch (Exception e) {
                        Log.e("Exception   sx  ", e.toString());
                    }


                    DecimalFormat format = new DecimalFormat("0.00");

                    Log.i(TAG, " CAsh " + cash_text321 + "  :  Formated Text : " + format.format(cash_text321));

                    cash.setText(Html.fromHtml("Cash: <br/><b>" + String.valueOf(format.format(cash_text321))));
                    online.setText(Html.fromHtml("Commision: <b>" + String.valueOf(totalcommisionamnt)));
                    company.setText(Html.fromHtml("Company: <b>" + String.valueOf(compny_text321)));
                    totalwallet.setText(Html.fromHtml("Wallet : ₹" + String.valueOf(totalwalletamnt)));

                    AdapterMonthlyAccount adeptor = new AdapterMonthlyAccount(MonthlyAccountActivity.this, date, time, daily_amount, trip_amount, kilometer, payment_mode, commission, onhandcash, driver_amount, wallet);
                    adeptor.notifyDataSetChanged();
                    ls1.setAdapter(adeptor);

                    JSONArray monthArray = submain.getJSONArray("monthArray");
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

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);


    }

    Dialog dialog;

    public void showPopUp() {
        try {
            dialog = new Dialog(MonthlyAccountActivity.this);
            dialog.setContentView(R.layout.dialog_spinner);

            ListView ls1 = dialog.findViewById(R.id.ls1);

            ArrayAdapter adapter = new ArrayAdapter<String>(MonthlyAccountActivity.this,
                    R.layout.item_monthly, categories1);
            ls1.setAdapter(adapter);
            ls1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Toast.makeText(MonthlyAccountActivity.this, categories1.get(position), Toast.LENGTH_LONG).show();
                    if (network) {
                        monthName = categories1.get(position);
                        callapiforcal(categories1.get(position));
                        selectmonth.setText(categories1.get(position));
                        categories.clear();
                        categories1.clear();
                        dialog.cancel();
                    } else {
                        Toast.makeText(MonthlyAccountActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
                    }

                }
            });
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            // The absolute width of the available display size in pixels.
            int displayWidth = displayMetrics.widthPixels;
            // The absolute height of the available display size in pixels.
            int displayHeight = displayMetrics.heightPixels;

            // Initialize a new window manager layout parameters
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            // Copy the alert dialog window attributes to new layout parameter instance
            layoutParams.copyFrom(dialog.getWindow().getAttributes());

            // Set the alert dialog window width and height
            // Set alert dialog width equal to screen width 90%
            // int dialogWindowWidth = (int) (displayWidth * 0.9f);
            // Set alert dialog height equal to screen height 90%
            // int dialogWindowHeight = (int) (displayHeight * 0.9f);

            // Set alert dialog width equal to screen width 70%
            int dialogWindowWidth = (int) (displayWidth * 0.95f);
            // Set alert dialog height equal to screen height 70%
            //int dialogWindowHeight = (int) (displayHeight * 0.7f);

            // Set the width and height for the layout parameters
            // This will bet the width and height of alert dialog
            layoutParams.width = dialogWindowWidth;
            //layoutParams.height = dialogWindowHeight;

            // Apply the newly created layout parameters to the alert dialog window
            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e) {
            Log.e("Exception   sd", e.toString());
        }
    }
}