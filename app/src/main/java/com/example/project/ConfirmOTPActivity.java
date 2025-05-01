package com.example.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.example.project.helpers.TinyDB;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConfirmOTPActivity extends AppCompatActivity {

    private EditText user_otp;
    private RequestQueue requestQueue;
    private TextView resend_tv;
    private String email;
    private LottieAnimationView loadingBar;
    private FrameLayout loadingOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_otp);
        FrameLayout mainLayout = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(0, 0, 0, navBarInsets.bottom); // đẩy layout lên khỏi nav bar
            return insets;
        });

        // Lay biến USER_EMAIL được truyền từ RegisterActivity
        Intent intent = getIntent();
        email = intent.getStringExtra("USER_EMAIL");

        user_otp = (EditText) findViewById(R.id.otp);
        resend_tv = (TextView) findViewById(R.id.resend);
        resend_tv.setVisibility(View.INVISIBLE);
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingBar = findViewById(R.id.loadingBar);
    }
    public void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void verifyOTP(View view) {
        String otp = user_otp.getText().toString().trim();
        hideKeyboard(this);
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingBar.playAnimation();


        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, UrlUtil.ADDRESS + "verify-account",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String mess = jsonResponse.getString("message");
                            if(mess.equals("OTP verified. You can login.")){
                                loadingOverlay.setVisibility(View.GONE);
                                loadingBar.cancelAnimation();
                                String access_token = jsonResponse.getString("access_token");
                                String refresh_token = jsonResponse.getString("refresh_token");
                                TinyDB tinyDB = new TinyDB(ConfirmOTPActivity.this);
                                tinyDB.putString("token",access_token);
                                tinyDB.putString("refresh_token",refresh_token);
                                showErrorDialog();
                            }
                            // Hiện nút resend
                            else if (mess.equals("OTP has expired. Please regenerate and try again.")) {
                                loadingOverlay.setVisibility(View.GONE);
                                loadingBar.cancelAnimation();
                                resend_tv.setVisibility(View.VISIBLE);
                                Toast.makeText(ConfirmOTPActivity.this,"Mã OTP đã quá hạn. Vui lòng gửi lại mã", Toast.LENGTH_SHORT).show();
                            }
                            // Hiện nút resend
                            else if (mess.equals("Invalid OTP. Please check and try again.")) {
                                loadingOverlay.setVisibility(View.GONE);
                                loadingBar.cancelAnimation();
                                Toast.makeText(ConfirmOTPActivity.this,"Mã OTP không hợp lệ",Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            loadingOverlay.setVisibility(View.GONE);
                            loadingBar.cancelAnimation();
                            Toast.makeText(ConfirmOTPActivity.this, "Error processing login response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingOverlay.setVisibility(View.GONE);
                        loadingBar.cancelAnimation();
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
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingBar.playAnimation();
        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT, UrlUtil.ADDRESS +
                "regenerate-otp",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingOverlay.setVisibility(View.GONE);
                        loadingBar.cancelAnimation();
                        Toast.makeText(ConfirmOTPActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                        resend_tv.setVisibility(View.INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingOverlay.setVisibility(View.GONE);
                        loadingBar.cancelAnimation();
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
    private void showErrorDialog(){
        ConstraintLayout errorConstrlayout = findViewById(R.id.successConstraintLayout);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_register_success,errorConstrlayout);
        TextView okBtn = view.findViewById(R.id.okBtn);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        okBtn.findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(ConfirmOTPActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}