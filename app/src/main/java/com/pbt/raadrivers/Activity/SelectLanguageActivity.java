package com.pbt.raadrivers.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.MyApplication;

import java.util.Locale;

public class SelectLanguageActivity extends AppCompatActivity {

    TextView eng, guj, hindi, marathi;

    SharedPreferences sharedpreferences;
    String selected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectlanguage);

        MyApplication.setCurrentActivity(this);
        SharedPreferences prefs = getSharedPreferences("languages", MODE_PRIVATE);
        selected = prefs.getString("Selectedla", "");
        if (!selected.equalsIgnoreCase("")) {
            Intent i = new Intent(SelectLanguageActivity.this, PhoneNumberVerifyActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }
        MyApplication.setCurrentActivity(this);

        eng = (TextView) findViewById(R.id.eng);
        guj = (TextView) findViewById(R.id.guj);
        hindi = (TextView) findViewById(R.id.hindi);
        marathi = (TextView) findViewById(R.id.marathi);

        sharedpreferences = getSharedPreferences("languages", Context.MODE_PRIVATE);
        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale locale = new Locale("en");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(
                        config,
                        getResources().getDisplayMetrics()
                );
                Resources res = getApplicationContext().getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.setLocale(new Locale("en")); // API 17+ only.
                res.updateConfiguration(conf, dm);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("Selectedla", "en");
                editor.apply();

                Intent i = new Intent(SelectLanguageActivity.this, PhoneNumberVerifyActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
        guj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Locale locale = new Locale("gu");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(
                        config,
                        getResources().getDisplayMetrics()
                );
                Resources res = getApplicationContext().getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.setLocale(new Locale("gu")); // API 17+ only.
                res.updateConfiguration(conf, dm);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("Selectedla", "gu");
                editor.apply();

                Intent i = new Intent(SelectLanguageActivity.this, PhoneNumberVerifyActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
        hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale locale = new Locale("hi");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(
                        config,
                        getResources().getDisplayMetrics()
                );
                Resources res = getApplicationContext().getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.setLocale(new Locale("hi")); // API 17+ only.
                res.updateConfiguration(conf, dm);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("Selectedla", "hi");
                editor.apply();

                Intent i = new Intent(SelectLanguageActivity.this, PhoneNumberVerifyActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
        marathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale locale = new Locale("mr");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(
                        config,
                        getResources().getDisplayMetrics()
                );
                Resources res = getApplicationContext().getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.setLocale(new Locale("mr")); // API 17+ only.
                res.updateConfiguration(conf, dm);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("Selectedla", "mr");
                editor.apply();
                Intent i = new Intent(SelectLanguageActivity.this, PhoneNumberVerifyActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }
}
