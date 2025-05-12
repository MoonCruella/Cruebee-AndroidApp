package com.example.project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.helpers.StringHelper;
import com.example.project.utils.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NewPasswordActivity extends AppCompatActivity {
    private EditText password,re_password;
    private String email;
    private TextView tvError1,tvError2,saveTxt;
    private LottieAnimationView loadingBar;
    private FrameLayout loadingOverlay;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        FrameLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(0, 0, 0, navBarInsets.bottom); // đẩy layout lên khỏi nav bar
            return insets;
        });

        loadingBar = findViewById(R.id.loadingBar);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        password = (EditText) findViewById(R.id.password);
        re_password= (EditText) findViewById(R.id.re_password);
        tvError1 = findViewById(R.id.tvError1);
        tvError2 = findViewById(R.id.tvError2);
        saveTxt = findViewById(R.id.saveTxt);
        Intent intent = getIntent();
        email = intent.getStringExtra("USER_EMAIL");
        checkInput();
        saveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().isEmpty()){
                    tvError1.setText("Không được để trống");
                    tvError1.setVisibility(View.VISIBLE);
                }
                if(re_password.getText().toString().isEmpty()){
                    tvError2.setText("Không được để trống");
                    tvError2.setVisibility(View.VISIBLE);
                }
                if(tvError2.isShown() || tvError1.isShown() ){
                    return;
                }else{
                    openMainActivity(v);
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
    public void openMainActivity(View view){


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        hideKeyboard(this);
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingBar.playAnimation();


        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                UrlUtil.ADDRESS +"reset-password",
                response -> {
                    loadingOverlay.setVisibility(View.GONE);
                    loadingBar.cancelAnimation();
                    if(response.equals("Change password successful!")){
                        Toast.makeText(NewPasswordActivity.this,"Thay đổi mật khẩu thành công!",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewPasswordActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(NewPasswordActivity.this, "Lỗi hệ thống. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingOverlay.setVisibility(View.GONE);
                    loadingBar.cancelAnimation();
                    Toast.makeText(NewPasswordActivity.this,"" + error,Toast.LENGTH_SHORT).show();
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("email", email.trim());
                    jsonBody.put("password", password.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                return headerMap;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void checkInput(){
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString().trim();
                String confirm = re_password.getText().toString().trim();
                if (!confirm.isEmpty() && !confirm.equals(password)) {
                    tvError2.setText("* Mật khẩu xác nhận không khớp");
                    tvError2.setVisibility(View.VISIBLE);
                }
                if(confirm.equals(password)){
                    tvError2.setVisibility(View.GONE);
                }
                if (password.isEmpty()) {
                    tvError1.setText("* Không được để trống");
                    tvError1.setVisibility(View.VISIBLE);
                } else if (!StringHelper.isValidPassword(password)) {
                    tvError1.setText("* Mật khẩu từ 8 kí tự trở lên, bao gồm chữ hoa, chữ thường và chữ số");
                    tvError1.setVisibility(View.VISIBLE);
                } else {
                    tvError1.setVisibility(View.GONE);
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

            @Override
            public void afterTextChanged(Editable s) {
                String pass = password.getText().toString().trim();
                String confirm = s.toString().trim();
                if (!confirm.isEmpty() && !confirm.equals(pass)) {
                    tvError2.setText("* Mật khẩu xác nhận không khớp");
                    tvError2.setVisibility(View.VISIBLE);
                }
                else if (confirm.isEmpty()) {
                    tvError2.setText("* Không được để trống");
                    tvError2.setVisibility(View.VISIBLE);
                } else if (!StringHelper.isValidPassword(confirm)) {
                    tvError2.setText("* Mật khẩu từ 8 kí tự trở lên, bao gồm chữ hoa, chữ thường và chữ số");
                    tvError2.setVisibility(View.VISIBLE);
                } else {
                    tvError2.setVisibility(View.GONE);
                }
            }
        });
    }
}

