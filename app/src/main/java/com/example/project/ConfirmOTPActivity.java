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
import com.example.project.helpers.TinyDB;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConfirmOTPActivity extends AppCompatActivity {

    private EditText user_otp;
    private RequestQueue requestQueue;
    private TextView resend_tv;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.verify_otp_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Lay biến USER_EMAIL được truyền từ RegisterActivity
        Intent intent = getIntent();
        email = intent.getStringExtra("USER_EMAIL");

        user_otp = (EditText) findViewById(R.id.otp);
        resend_tv = (TextView) findViewById(R.id.resend);
        resend_tv.setVisibility(View.INVISIBLE);
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
    }

    public void verifyOTP(View view) {
        String otp = user_otp.getText().toString();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading... Please wait...!!");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, UrlUtil.ADDRESS + "verify-account",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse the JSON response from the backend
                            JSONObject jsonResponse = new JSONObject(response);
                            String mess = jsonResponse.getString("message");
                            if(mess.equals("OTP verified. You can login.")){
                                progressDialog.hide();
                                Intent intent = new Intent(ConfirmOTPActivity.this, LoginActivity.class);
                                startActivity(intent);
                                String access_token = jsonResponse.getString("access_token");
                                String refresh_token = jsonResponse.getString("refresh_token");
                                TinyDB tinyDB = new TinyDB(ConfirmOTPActivity.this);
                                tinyDB.putString("token",access_token);
                                tinyDB.putString("refresh_token",refresh_token);
                                Toast.makeText(ConfirmOTPActivity.this, access_token, Toast.LENGTH_SHORT).show();
                            }
                            // Hiện nút resend
                            else if (mess.equals("OTP has expired. Please regenerate and try again.")) {
                                resend_tv.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ConfirmOTPActivity.this, "Error processing login response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(ConfirmOTPActivity.this, "" + error, Toast.LENGTH_SHORT).show();
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
                Request.Method.PUT, UrlUtil.ADDRESS +
                "regenerate-otp",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        Toast.makeText(ConfirmOTPActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                        resend_tv.setVisibility(View.INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(ConfirmOTPActivity.this, "" + error, Toast.LENGTH_SHORT).show();
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