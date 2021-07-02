package com.pbt.raadrivers.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pbt.raadrivers.Fonts.CButton;
import com.pbt.raadrivers.Fonts.CEditText;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;
import com.pbt.raadrivers.Utils.MyApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;


public class PhoneNumberVerifyActivity extends AppCompatActivity implements AppConstant {

    CEditText ed_mob;
    CButton otp_continue;
    ImageView backbutton;
    SharedPreferences sharedpreferences;
    private String TAG = "PhoneNumberVerifyActivity";

    public static final int MULTIPLE_PERMISSIONS = 1;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    public static final int WRITE_PERMISSION_REQUEST_CODE = 6;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 5;


    Dialog dialog;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verify);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                openPermission();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PhoneNumberVerifyActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        }

        MyApplication.setCurrentActivity(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        backbutton = toolbar.findViewById(R.id.backbutton1);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ed_mob = findViewById(R.id.ed_mobile_number);
        otp_continue = findViewById(R.id.otp_continue);
        otp_continue.setEnabled(false);

        otp_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mob = ed_mob.getText().toString().trim();
                if (mob.length() == 10) {
                    sharedpreferences = getSharedPreferences("REGISTER", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("mobile", mob);
                    editor.commit();
                    progstart(PhoneNumberVerifyActivity.this, "Loading...", "Loading...");
                    call_api(mob);
                } else {
                    Toast.makeText(PhoneNumberVerifyActivity.this, "Please check the number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void checkpermission() {
//        int permissionCheckCamera = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.RECEIVE_SMS);
//        int permissionCheckwrite = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_SMS);
//        int permissionphonestate = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.SEND_SMS);
//
//
//        // we already asked for permisson & Permission granted, call camera intent
//        if (permissionCheckCamera == PackageManager.PERMISSION_GRANTED && permissionCheckwrite == PackageManager.PERMISSION_GRANTED && permissionphonestate == PackageManager.PERMISSION_GRANTED) {
//        } //asking permission for the first tim e
//        else if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED && permissionCheckwrite != PackageManager.PERMISSION_GRANTED && permissionphonestate != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS},
//                    MULTIPLE_PERMISSIONS);
//
//        } else {
//            // Permission denied, so request permission
//
//            // if camera request is denied
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.RECEIVE_SMS)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage("These permissions are mandatory for the application. Please allow access");
//                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.setPositiveButton("GIVE PERMISSION", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//
//                        // Show permission request popup
//                        ActivityCompat.requestPermissions(PhoneNumberVerifyActivity.this,
//                                new String[]{Manifest.permission.RECEIVE_SMS},
//                                CAMERA_PERMISSION_REQUEST_CODE);
//                    }
//                });
//                builder.show();
//
//            }/// / if storage request is denied
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.READ_SMS)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage("These permissions are mandatory for the application. Please allow access");
//                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.setPositiveButton("GIVE PERMISSION", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//
//                        // Show permission request popup
//                        ActivityCompat.requestPermissions(PhoneNumberVerifyActivity.this,
//                                new String[]{Manifest.permission.READ_SMS},
//                                WRITE_PERMISSION_REQUEST_CODE);
//                    }
//                });
//                builder.show();
//
//            }
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.SEND_SMS)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage("These permissions are mandatory for the application. Please allow access");
//                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.setPositiveButton("GIVE PERMISSION", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//
//                        // Show permission request popup
//                        ActivityCompat.requestPermissions(PhoneNumberVerifyActivity.this,
//                                new String[]{Manifest.permission.SEND_SMS},
//                                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
//                    }
//                });
//                builder.show();
//
//            }
//        }
//    }


    public void openPermission() {

        Button btnAccessPermision;
        SwitchCompat switchCompat, switchCompat1;

        dialog = new Dialog(PhoneNumberVerifyActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.permission_dialog_new);
        switchCompat = dialog.findViewById(R.id.switchButton);
        switchCompat1 = dialog.findViewById(R.id.switchButton1);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            askforlocationpermision();
                        }
                    }
                }
            }
        });
        switchCompat1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            askforStoragePermision();
                        }
                    }
                }
            }
        });

        btnAccessPermision = dialog.findViewById(R.id.btnAccess);
//        lldd = dialog.findViewById(R.id.lldd);
//
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        int screenWidth = displaymetrics.widthPixels;
//        int screenHeight = displaymetrics.heightPixels;
//
//        Log.e("OPenDialog", " screenHeight -->> "+screenHeight);
//        Log.e("OPenDialog", " screenHeight -->> "+(screenHeight/3));
//
//// Gets the layout params that will allow you to resize the layout
//        ViewGroup.LayoutParams params = lldd.getLayoutParams();
//// Changes the height and width to the specified *pixels*
//        params.height = (screenHeight/3+100);
//        lldd.setLayoutParams(params);
//
        btnAccessPermision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }

    private void askforlocationpermision() {
        ActivityCompat.requestPermissions(PhoneNumberVerifyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    private void askforStoragePermision() {
        ActivityCompat.requestPermissions(PhoneNumberVerifyActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        , Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
//                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.RECEIVE_SMS)) {
//                    // check whether camera permission granted or not.
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    }
//                }
//                break;
//            case CAMERA_PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_SMS)) {
//                    // check whether camera permission granted or not.
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    }
//                }
//                break;
//            case WRITE_PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.SEND_SMS)) {
//                    // check whether camera permission granted or not.
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    }
//                }
//                break;

            case MULTIPLE_PERMISSIONS:
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && permissions[1].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && permissions[2].equals(Manifest.permission.RECORD_AUDIO)) {
                    // check whether All permission granted or not.
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    }
                }
                break;
            default:
                break;
        }
    }

    private void call_api(final String mob) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, enterphone,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i(TAG, " Phone Number Verify Response : " + response);

                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                otp_continue.setEnabled(false);
                                Intent i = new Intent(PhoneNumberVerifyActivity.this, OtpVerificationActivity.class);
                                i.putExtra("mobile", mob);
                                startActivity(i);
                            } else {
                                Toast.makeText(PhoneNumberVerifyActivity.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            progstop();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(PhoneNumberVerifyActivity.this, "Something Wrong !", Toast.LENGTH_SHORT).show();
                            progstop();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PhoneNumberVerifyActivity.this, "Something Wrong !", Toast.LENGTH_SHORT).show();
                        progstop();
                        Log.e(TAG, " Volley Exception " + error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", mob);
                params.put("app", "3");
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        otp_continue.setEnabled(true);
    }
}
