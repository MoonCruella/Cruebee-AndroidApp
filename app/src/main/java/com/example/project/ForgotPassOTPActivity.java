package com.example.project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassOTPActivity extends AppCompatActivity {

    private EditText user_otp;
    private TextView resend_tv;
    private RequestQueue requestQueue;
    private String email;
    private LottieAnimationView loadingBar;
    private FrameLayout loadingOverlay;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass_otp);
        FrameLayout mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(0, 0, 0, navBarInsets.bottom); // đẩy layout lên khỏi nav bar
            return insets;
        });

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        user_otp = (EditText) findViewById(R.id.otp);
        resend_tv = (TextView) findViewById(R.id.resend);
        resend_tv.setVisibility(View.INVISIBLE);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingBar = findViewById(R.id.loadingBar);
        Intent intent = getIntent();
        email = intent.getStringExtra("USER_EMAIL");
    }
    public void verifyOTP(View view) {

        String otp = user_otp.getText().toString().trim();
        requestQueue = Volley.newRequestQueue(this);
        hideKeyboard(this);
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingBar.playAnimation();

        String encodedEmail = Uri.encode(email);
        String encodedOtp = Uri.encode(otp);

        String url = UrlUtil.ADDRESS + "verify-otp?email=" + encodedEmail + "&otp=" + encodedOtp;

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingOverlay.setVisibility(View.GONE);
                        loadingBar.cancelAnimation();
                        if (response.equals("OTP Verified. You can set new password")) {
                            Intent intent = new Intent(ForgotPassOTPActivity.this, NewPasswordActivity.class);
                            intent.putExtra("USER_EMAIL", email);
                            startActivity(intent);
                        } else if (response.equals("OTP has expired. Please regenerate and try again.")) {
                            resend_tv.setVisibility(View.VISIBLE);
                            Toast.makeText(ForgotPassOTPActivity.this, "Mã OTP quá hạn. Vui lòng gửi lại mã", Toast.LENGTH_SHORT).show();
                        } else if (response.equals("Invalid OTP. Please check and try again.")) {
                            Toast.makeText(ForgotPassOTPActivity.this, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingOverlay.setVisibility(View.GONE);
                        loadingBar.cancelAnimation();
                        Toast.makeText(ForgotPassOTPActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    public void resendOTP(View view)
    {

        requestQueue = Volley.newRequestQueue(this);
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingBar.playAnimation();
        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                  UrlUtil.ADDRESS+"regenerate-otp",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingOverlay.setVisibility(View.GONE);
                        loadingBar.cancelAnimation();
                        Toast.makeText(ForgotPassOTPActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                        resend_tv.setVisibility(View.INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingOverlay.setVisibility(View.GONE);
                        loadingBar.cancelAnimation();
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
    public void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}