package com.example.project.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.project.R;
import com.example.project.helpers.StringHelper;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private EditText email,username, password, re_password,sdt;
    private TextView tvError,tvError1,tvError2,tvError3,tvError4,tvError5;
    private TextView register_btn;
    private String itemGender;
    private LottieAnimationView loadingBar;
    private FrameLayout loadingOverlay;
    private AutoCompleteTextView gender;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        FrameLayout mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(0, 0, 0, navBarInsets.bottom); // đẩy layout lên khỏi nav bar
            return insets;
        });

        loadingBar = findViewById(R.id.loadingBar);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        email= findViewById(R.id.email);
        username = findViewById(R.id.username);
        password= findViewById(R.id.password);
        re_password= findViewById(R.id.re_password);
        gender = findViewById(R.id.gender);
        sdt = findViewById(R.id.sdt);
        tvError = findViewById(R.id.tvError);
        tvError1 = findViewById(R.id.tvError1);
        tvError2 = findViewById(R.id.tvError2);
        tvError3 = findViewById(R.id.tvError3);
        tvError4 = findViewById(R.id.tvError4);
        tvError5 = findViewById(R.id.tvError5);

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        List<String> genderList = Arrays.asList("Nam", "Nữ", "Khác");

        // Tạo Adapter cho dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, genderList);

        // Gán Adapter vào AutoCompleteTextView
        gender.setAdapter(adapter);

        gender.setOnItemClickListener((parent, view, position, id) -> itemGender = adapter.getItem(position).toString());

        register_btn = findViewById(R.id.register);
        checkInput();
        register_btn.setOnClickListener(v -> {
            boolean hasError = false;

            if(email.getText().toString().isEmpty()){
                tvError1.setText("Không được để trống");
                tvError1.setVisibility(View.VISIBLE);
                hasError = true;
            }
            if(password.getText().toString().isEmpty()){
                tvError3.setText("Không được để trống");
                tvError3.setVisibility(View.VISIBLE);
                hasError = true;
            }
            if(re_password.getText().toString().isEmpty()){
                tvError4.setText("Không được để trống");
                tvError4.setVisibility(View.VISIBLE);
                hasError = true;
            }
            if(sdt.getText().toString().isEmpty()){
                tvError.setText("Không được để trống");
                tvError.setVisibility(View.VISIBLE);
                hasError = true;
            }
            if(gender.getText().toString().isEmpty()){
                tvError5.setText("Không được để trống");
                tvError5.setVisibility(View.VISIBLE);
                hasError = true;
            }
            if(username.getText().toString().isEmpty()){
                tvError2.setText("Không được để trống");
                tvError2.setVisibility(View.VISIBLE);
                hasError = true;
            }

            if (hasError) {
                return; // Ngừng gọi API nếu có lỗi
            }

            registerUser(v); // Gọi API nếu không có lỗi
        });

    }

    public void registerUser(View view){

        register_btn.setEnabled(false);
        String email1 = email.getText().toString().trim();
        String username1 = username.getText().toString().trim();
        String password1 = password.getText().toString().trim();
        String uSdt = sdt.getText().toString().trim();

        loadingOverlay.setVisibility(View.VISIBLE);
        loadingBar.playAnimation();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, UrlUtil.ADDRESS +
                "register",
                new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loadingOverlay.setVisibility(View.GONE);
                    loadingBar.cancelAnimation();
                    if(response.trim().equals("User registration successful")){
                        Intent intent = new Intent(RegisterActivity.this,ConfirmOTPActivity.class);
                        // Su dung putExtra để pass biến email1 qua Activity khác
                        intent.putExtra("USER_EMAIL",email1);
                        startActivity(intent);
                        finish();
                    }
                    else if(response.trim().equals("Email existed!!!")){
                        register_btn.setEnabled(true);
                        Toast.makeText(RegisterActivity.this,"Email đã tồn tại",Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadingOverlay.setVisibility(View.GONE);
                    loadingBar.cancelAnimation();
                    Toast.makeText(RegisterActivity.this,"" + error,Toast.LENGTH_SHORT).show();
                    register_btn.setEnabled(true);
                }
            }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("email", email1);
                    jsonBody.put("username", username1);
                    jsonBody.put("password", password1);
                    jsonBody.put("sdt",uSdt);
                    jsonBody.put("gender",itemGender);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                return headerMap;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void checkInput(){

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                String ema = s.toString().trim();

                if (ema.isEmpty()) {
                    tvError1.setText("* Không được để trống");
                    tvError1.setVisibility(VISIBLE);
                }
                else if(!StringHelper.isEmailValid(ema)){
                    tvError1.setText("* Email không hợp lệ");
                    tvError1.setVisibility(VISIBLE);
                }
                else {
                    tvError1.setVisibility(GONE);
                }
            }
        });
        sdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString().trim();

                if (phone.isEmpty()) {
                    tvError.setText("* Không được để trống");
                    tvError.setVisibility(VISIBLE);
                }
                else if(!StringHelper.isValidVietnamPhone(phone)){
                    tvError.setText("* Số điện thoại không hợp lệ");
                    tvError.setVisibility(VISIBLE);
                }
                else {
                    tvError.setVisibility(GONE);
                }
            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString().trim();

                if (phone.isEmpty()) {
                    tvError2.setText("* Không được để trống");
                    tvError2.setVisibility(VISIBLE);
                }
                else {
                    tvError2.setVisibility(GONE);
                }
            }
        });
        gender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString().trim();

                if (phone.isEmpty()) {
                    tvError5.setText("* Không được để trống");
                    tvError5.setVisibility(VISIBLE);
                }
                else {
                    tvError5.setVisibility(GONE);
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString().trim();
                String confirm = re_password.getText().toString().trim();
                if (!confirm.isEmpty() && !confirm.equals(password)) {
                    tvError4.setText("* Mật khẩu xác nhận không khớp");
                    tvError4.setVisibility(View.VISIBLE);
                }
                if(confirm.equals(password)){
                    tvError4.setVisibility(View.GONE);
                }
                if (password.isEmpty()) {
                    tvError3.setText("* Không được để trống");
                    tvError3.setVisibility(View.VISIBLE);
                } else if (!StringHelper.isValidPassword(password)) {
                    tvError3.setText("* Mật khẩu từ 8 kí tự trở lên, bao gồm chữ hoa, chữ thường và chữ số");
                    tvError3.setVisibility(View.VISIBLE);
                } else {
                    tvError3.setVisibility(View.GONE);
                }
            }
        });
        re_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                String pass = password.getText().toString().trim();
                String confirm = s.toString().trim();
                if (!confirm.isEmpty() && !confirm.equals(pass)) {
                    tvError4.setText("* Mật khẩu xác nhận không khớp");
                    tvError4.setVisibility(View.VISIBLE);
                }
                else if (confirm.isEmpty()) {
                    tvError4.setText("* Không được để trống");
                    tvError4.setVisibility(View.VISIBLE);
                } else if (!StringHelper.isValidPassword(confirm)) {
                    tvError4.setText("* Mật khẩu từ 8 kí tự trở lên, bao gồm chữ hoa, chữ thường và chữ số");
                    tvError4.setVisibility(View.VISIBLE);
                } else {
                    tvError4.setVisibility(View.GONE);
                }
            }
        });

    }



}