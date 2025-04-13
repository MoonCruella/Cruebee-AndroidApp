package com.example.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
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
import com.example.project.helpers.StringHelper;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassActivity extends AppCompatActivity {

    private EditText email;
    private TextView tvError1;
    private LottieAnimationView loadingBar;
    private FrameLayout loadingOverlay;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        loadingBar = findViewById(R.id.loadingBar);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        tvError1 = findViewById(R.id.tvError1);
        email = (EditText) findViewById(R.id.email);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Thực hiện hành động nếu cần trước khi text thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Bạn có thể cập nhật giao diện khi text thay đổi nếu cần
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ema = s.toString().trim();

                if (ema.isEmpty()) {
                    tvError1.setText("* Không được để trống");
                    tvError1.setVisibility(View.VISIBLE);
                }
                else if(!StringHelper.isEmailValid(ema)){
                    tvError1.setText("* Email không hợp lệ");
                    tvError1.setVisibility(View.VISIBLE);
                }
                else {
                    tvError1.setVisibility(View.GONE);
                }
            }
        });
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Khi mất focus (người dùng rời khỏi ô nhập)
                if (!hasFocus) {
                    String input = email.getText().toString().trim();
                    if (input.isEmpty()) {
                        tvError1.setText("Không được để trống");
                        tvError1.setVisibility(View.VISIBLE);
                    } else {
                        tvError1.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public void openForgotPassOTPActivity(View view){

        loadingOverlay.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.VISIBLE);
        loadingBar.setMinAndMaxFrame(0, 60);
        loadingBar.setSpeed(1.5f);
        loadingBar.playAnimation();
        StringRequest stringRequest = new StringRequest(
        Request.Method.PUT,
                  UrlUtil.ADDRESS + "forget-password",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingOverlay.setVisibility(View.GONE);
                        loadingBar.cancelAnimation();
                        Toast.makeText(ForgotPassActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                        if (response.equals("Email sent ... please verify account within 3 minute")) {
                            Intent intent = new Intent(ForgotPassActivity.this, ForgotPassOTPActivity.class);
                            intent.putExtra("USER_EMAIL",email.getText().toString());
                            startActivity(intent);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingOverlay.setVisibility(View.GONE);
                        loadingBar.cancelAnimation();
                        Toast.makeText(ForgotPassActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("email", email.getText().toString());
                return hashMap;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

}