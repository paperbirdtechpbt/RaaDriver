package com.pbt.raadrivers.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pbt.raadrivers.Fonts.CEditText;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.MyApplication;

import java.util.HashMap;
import java.util.Map;

import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;

public class ThanksScreenActivity extends AppCompatActivity {

    RatingBar ratingBar;
    CEditText message;
    Button submit, skip;
    TextView head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks_screen);

        MyApplication.setCurrentActivity(this);

        head = findViewById(R.id.head);
        ratingBar = findViewById(R.id.ratingBar);
        submit = findViewById(R.id.submit);
        message = findViewById(R.id.message);
        skip = findViewById(R.id.skip);

        head.setText("Give User Rating");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rating = String.valueOf(ratingBar.getRating());
                String mess = message.getText().toString().trim();

                callAPIforfavrit(rating, mess);

            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ThanksScreenActivity.this, DrivarHomePageActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }

    void callAPIforfavrit(final String rating, final String mess) {

        progstart(ThanksScreenActivity.this, "Loading...", "Loading...");

        final String logtokan, customer_id, driver_id;
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        logtokan = prefs.getString("tokan", "");
        driver_id = prefs.getString("userid", "");
        customer_id = prefs.getString("customer_id", "");

        Log.e("Common.rating", rating);
        StringRequest request = new StringRequest(Request.Method.POST, AppConstant.rating, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Log.e("response ","start Ride onr onr onr "+response);

                    Intent i = new Intent(ThanksScreenActivity.this, DrivarHomePageActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan);
                Log.e("Authorization", "Bearer " + logtokan);
                return params;
            }

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("rate", rating);
                params.put("comment", mess);
                params.put("user_id", customer_id);
                params.put("from_user_id", driver_id);

                Log.e("Driver", "Driver");
                Log.e("rate", rating);
                Log.e("comment", mess);
                Log.e("user_id", customer_id);
                Log.e("from_user_id", driver_id);

                return params;
            }


        };
        RequestQueue queue = Volley.newRequestQueue(ThanksScreenActivity.this);
        queue.add(request);
    }
}
