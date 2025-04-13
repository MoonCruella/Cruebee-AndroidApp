package com.example.project;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.example.project.helpers.StringHelper;
import com.example.project.helpers.TinyDB;
import com.example.project.model.Address;
import com.example.project.model.User;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private TextView tvError1,tvError2,notLogin,registerBtn;
    private RequestQueue requestQueue;
    private TinyDB tinyDB;
    private String token;
    private LottieAnimationView loadingBar;
    private FrameLayout loadingOverlay;
    private String username;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);


        email= (EditText) findViewById(R.id.email);
        password= (EditText) findViewById(R.id.password);
        tvError1 = findViewById(R.id.tvError1);
        tvError2 = findViewById(R.id.tvError2);
        notLogin =findViewById(R.id.notLogin);
        registerBtn = findViewById(R.id.registerBtn);
        loadingBar = findViewById(R.id.loadingBar);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        tinyDB = new TinyDB(this);
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        password.addTextChangedListener(new TextWatcher() {
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
                String pass = s.toString().trim();

                if (pass.isEmpty()) {
                    tvError2.setText("* Không được để trống");
                    tvError2.setVisibility(VISIBLE);
                } else {
                    tvError2.setVisibility(GONE);
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Khi mất focus (người dùng rời khỏi ô nhập)
                if (!hasFocus) {
                    String input = password.getText().toString().trim();
                    if (input.isEmpty()) {
                        tvError2.setText("Không được để trống");
                        tvError2.setVisibility(VISIBLE);
                    } else {
                        tvError2.setVisibility(GONE);
                    }
                }
            }
        });

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
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Khi mất focus (người dùng rời khỏi ô nhập)
                if (!hasFocus) {
                    String input = email.getText().toString().trim();
                    if (input.isEmpty()) {
                        tvError1.setText("Không được để trống");
                        tvError1.setVisibility(VISIBLE);
                    } else {
                        tvError1.setVisibility(GONE);
                    }
                }
            }
        });

        notLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinyDB.putBoolean("is_logged_in",false);
                startActivity(new Intent(LoginActivity.this,BaseActivity.class));
            }
        });
    }

    public void openHomeActivity(View view){

        String email1 = email.getText().toString();
        String password1 = password.getText().toString();
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingBar.setMinAndMaxFrame(0, 60);
        loadingBar.setSpeed(1.5f);
        loadingBar.playAnimation();
        String url = UrlUtil.ADDRESS + "loginn";
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingOverlay.setVisibility(View.GONE);
                        loadingBar.cancelAnimation();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if(jsonResponse.has("message"))
                            {
                                String mess = jsonResponse.getString("message");
                                Toast.makeText(LoginActivity.this, mess, Toast.LENGTH_SHORT).show();
                            }
                            if (jsonResponse.has("token")) {

                                token = jsonResponse.getString("token");
                                String refresh_token = jsonResponse.getString("refresh_token");
                                JSONObject user = jsonResponse.getJSONObject("user");

                                // Store the token in SharedPreferences for future use
                                tinyDB.putString("token",token);
                                tinyDB.putString("refresh_token",refresh_token);
                                username = user.getString("username");
                                int userId = user.getInt("id");
                                String sdt = user.getString("sdt");
                                String email = user.getString("email");
                                String password = user.getString("password");
                                String gender = user.getString("gender");
                                User savedUser = new User(userId,username,password,email,sdt,gender);
                                tinyDB.putObject("savedUser",savedUser);
                                tinyDB.putBoolean("is_logged_in",true);
                                Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                                startActivity(intent);
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error processing login response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingOverlay.setVisibility(GONE);
                        loadingBar.cancelAnimation();
                        Toast.makeText(LoginActivity.this,"" + error,Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                // Create a JSONObject and put data into it
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("email", email1);
                    jsonBody.put("password", password1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Return the byte[] of the JSON string
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                return headerMap;
            }
        };

        //Fix Volley time out error
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void openForgotPassActivity(View view){
        startActivity(new Intent(LoginActivity.this,ForgotPassActivity.class));
    }



}