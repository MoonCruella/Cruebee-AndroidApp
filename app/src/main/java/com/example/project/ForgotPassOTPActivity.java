package com.example.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassOTPActivity extends AppCompatActivity {

    private EditText user_otp;
    private TextView resend_tv;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        user_otp = (EditText) findViewById(R.id.otp);
        resend_tv = (TextView) findViewById(R.id.resend);
        resend_tv.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        email = intent.getStringExtra("USER_EMAIL");
    }
    public void verifyOTP(View view) {

        String otp = user_otp.getText().toString();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage(otp);
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                "http://192.168.1.5:8888/verify-otp",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        Toast.makeText(ForgotPassOTPActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                        if (response.equals("OTP Verified. You can set new password")) {
                            Intent intent = new Intent(ForgotPassOTPActivity.this, NewPasswordActivity.class);
                            intent.putExtra("USER_EMAIL",email);
                            startActivity(intent);
                        }
                        else if(response.equals("OTP has expired. Please regenerate and try again.")){
                            resend_tv.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(ForgotPassOTPActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("email", email);
                hashMap.put("otp",otp);
                return hashMap;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    public void resendOTP(View view)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading... Please wait...!!");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                "http://192.168.1.5:8888/regenerate-otp",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        Toast.makeText(ForgotPassOTPActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                        resend_tv.setVisibility(View.INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(ForgotPassOTPActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("email", email);
                return hashMap;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}