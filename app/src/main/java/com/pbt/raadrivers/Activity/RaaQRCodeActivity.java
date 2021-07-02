package com.pbt.raadrivers.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.MyApplication;

public class RaaQRCodeActivity extends AppCompatActivity {

    int iscust;
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RaaQRCodeActivity.this, DrivarHomePageActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raa_qrcode);

        MyApplication.setCurrentActivity(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        iscust = getIntent().getExtras().getInt("iscust");

    }
}
