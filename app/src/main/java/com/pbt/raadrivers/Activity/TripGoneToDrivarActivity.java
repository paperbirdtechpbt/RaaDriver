package com.pbt.raadrivers.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pbt.raadrivers.Fonts.CButton;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.MyApplication;


public class TripGoneToDrivarActivity extends AppCompatActivity {

    CButton btnGoback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_goneto_drivar);
        MyApplication.setCurrentActivity(this);

        btnGoback = (CButton) findViewById(R.id.goback);

        btnGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TripGoneToDrivarActivity.this, DrivarHomePageActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(TripGoneToDrivarActivity.this, DrivarHomePageActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}
