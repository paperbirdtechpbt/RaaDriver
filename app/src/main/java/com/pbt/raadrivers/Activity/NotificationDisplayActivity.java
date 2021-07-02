package com.pbt.raadrivers.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.MyApplication;


public class NotificationDisplayActivity extends AppCompatActivity {

    String message, payload;
    TextView title;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_display);
        MyApplication.setCurrentActivity(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            Intent I = getIntent();
            message = I.getStringExtra("message");
            payload = I.getStringExtra("payload");
        } catch (Exception e) {
            e.printStackTrace();
        }
        title = (TextView) findViewById(R.id.title);
        title.setText(message);
    }
}
