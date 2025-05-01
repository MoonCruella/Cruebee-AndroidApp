package com.example.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.project.helpers.StringHelper;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;


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
        FrameLayout mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(0, 0, 0, navBarInsets.bottom); // đẩy layout lên khỏi nav bar
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

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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
    public void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void openForgotPassOTPActivity(View view){

        hideKeyboard(this);
        String email1 = email.getText().toString().trim();
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingBar.playAnimation();
        String url = UrlUtil.ADDRESS + "forget-password?email=" + Uri.encode(email1);

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                url,
                response -> {
                    loadingOverlay.setVisibility(View.GONE);
                    loadingBar.cancelAnimation();
                    Toast.makeText(ForgotPassActivity.this, response, Toast.LENGTH_SHORT).show();
                    if (response.equals("Email sent ... please verify account within 3 minute")) {
                        Intent intent = new Intent(ForgotPassActivity.this, ForgotPassOTPActivity.class);
                        intent.putExtra("USER_EMAIL", email1);
                        startActivity(intent);
                    }
                },
                error -> {
                    loadingOverlay.setVisibility(View.GONE);
                    loadingBar.cancelAnimation();
                    Toast.makeText(ForgotPassActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

}