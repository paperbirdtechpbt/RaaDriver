package com.pbt.raadrivers.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.pbt.raadrivers.Fonts.CButton;
import com.pbt.raadrivers.Notification.Config;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.ConnectivityReceiver;
import com.pbt.raadrivers.Utils.MyApplication;
import com.pbt.raadrivers.Utils.VolleyMultipartRequest;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.pbt.raadrivers.Utils.Common.driver;
import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;

public class DriverProfileActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, AppConstant {


    ImageView profile_image;
    TextView email, phone, driver_name;
    CButton submit;
    boolean isnewupload = false, isnewtext = false;

    private Boolean network = Boolean.TRUE;
    private ConnectivityReceiver connectivityReceiver;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DriverProfileActivity.this, DrivarHomePageActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        MyApplication.setCurrentActivity(this);

        connectivityReceiver = new ConnectivityReceiver();
        connectivityReceiver.addListener(this);
        this.registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        profile_image = findViewById(R.id.profile_image);
        submit = findViewById(R.id.submit);
        driver_name = findViewById(R.id.driver_name);


        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        String image_pref = sharedpreferences.getString("image", "");

        Log.e("image_pref", image + "users/thumbnail/" + image_pref);

        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);

        driver_name.setText(sharedpreferences.getString("first_name", ""));

        UrlImageViewHelper.setUrlDrawable(profile_image, image + "users/thumbnail/" + image_pref, R.drawable.raalogo_white);

        email.setText(sharedpreferences.getString("email", ""));
        phone.setText(sharedpreferences.getString("phone", ""));


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (network) {
                    if (isnewupload) {
                        uploadBitmap(bitmap);
                    } else {
                        Toast.makeText(DriverProfileActivity.this, "You not made any change", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DriverProfileActivity.this, getResources().getString(R.string.internet_not), Toast.LENGTH_SHORT).show();
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

                            driver(DriverProfileActivity.this, message, payload, datafromnoti);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                //displaying selected image to imageview
                profile_image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    Bitmap bitmap;


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap) {

        progstart(DriverProfileActivity.this, "Uploading...", "Uploading your Image Please wait...");

        //getting the tag from the edittext
        //  final String tags = editTextTags.getText().toString().trim();
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
        final String logtokan = prefs.getString("tokan", "");
        //our custom volley request

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, profile,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.e("response", String.valueOf(response));
                        String parsed;
                        try {
                            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        } catch (UnsupportedEncodingException e) {
                            parsed = new String(response.data);
                        }
                        Log.e("response  parsed ", parsed);
                        try {

                            JSONObject main = new JSONObject(parsed);
                            JSONObject submain = main.getJSONObject("success");
                            String status = submain.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                Toast.makeText(DriverProfileActivity.this, submain.getString("message"), Toast.LENGTH_LONG).show();

                                SharedPreferences sharedpreferences;
                                sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                if (submain.has("file_name")) {
                                    editor.putString("image", submain.getString("file_name"));
                                    editor.apply();
                                }
                                editor.putString("first_name", driver_name.getText().toString().trim());
                                editor.apply();
                            } else {
                                Toast.makeText(DriverProfileActivity.this, submain.getString("message"), Toast.LENGTH_LONG).show();
                            }

                            progstop();


                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Exception", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progstop();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("error", error.toString());
                    }
                }) {


            /** If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             *
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + logtokan);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("name", driver_name.getText().toString().trim());
                return params;
            }

            /* * Here we are passing image by renaming it with a unique name
             **/
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (bitmap != null && !bitmap.equals("null")) {
                    long imagename = System.currentTimeMillis();
                    params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                }

                return params;
            }
        };


        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
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
}
