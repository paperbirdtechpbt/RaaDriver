package com.pbt.raadrivers.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.pbt.raadrivers.Fonts.CButton;
import com.pbt.raadrivers.Fonts.CEditText;
import com.pbt.raadrivers.Pojo.SendEmailResponse;
import com.pbt.raadrivers.R;
import com.pbt.raadrivers.Utils.AppConstant;

import java.util.HashMap;
import java.util.Map;

import static com.pbt.raadrivers.Utils.Common.progstart;
import static com.pbt.raadrivers.Utils.Common.progstop;

public class EnterMailDialog extends Dialog implements AppConstant {

    public Activity c;
    public Dialog d;
    String mobile;

    CEditText cEditTextMail;
    CButton cButtonMail;


    public EnterMailDialog(Activity a, String mobile) {
        super(a);
        this.c = a;
        this.mobile = mobile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_enter_mail);

        cEditTextMail = findViewById(R.id.ed_mail);
        cButtonMail = findViewById(R.id.btn_verify);
        cButtonMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cEditTextMail.getText().toString().trim().matches(emailPattern)) {
                    if (cEditTextMail.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast toast = Toast.makeText(c.getApplicationContext(), c.getResources().getString(R.string.allrequired), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else
                        apicallingpois(cEditTextMail.getText().toString(),mobile);
                } else
                    Toast.makeText(c, c.getString(R.string.emailvalid), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    void apicallingpois(final String email,final String mobile) {

        progstart(c, "Send Emaiil ", "Please Wait....");

        StringRequest postRequest = new StringRequest(Request.Method.POST, enteremail,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progstop();
                        try {
                            SendEmailResponse sendEmailResponse = new Gson().fromJson(response, SendEmailResponse.class);
                            if (sendEmailResponse != null && sendEmailResponse.getSuccess()) {
                                dismiss();
                                Toast.makeText(c, sendEmailResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                dismiss();
                                Toast.makeText(c, sendEmailResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(c, "Somethig Wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        progstop();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("phone", mobile);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(c.getApplicationContext());
        queue.add(postRequest);
    }
}
