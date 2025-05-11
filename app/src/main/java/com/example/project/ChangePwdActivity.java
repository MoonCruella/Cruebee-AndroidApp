package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.example.project.helpers.StringHelper;
import com.example.project.helpers.TinyDB;
import com.example.project.model.User;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleyHelper;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;


public class ChangePwdActivity extends AppCompatActivity {

    private TextInputEditText edtPassword,newPassword,cfPassword;
    private TextView tvError, tvError2, tvError3,savePw;
    private LottieAnimationView loadingBar;
    private FrameLayout loadingOverlay;
    private TinyDB tinyDB;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        FrameLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
            return insets;
        });
        edtPassword = findViewById(R.id.edtPassword);
        newPassword = findViewById(R.id.newPassword);
        cfPassword = findViewById(R.id.cfPassword);
        tvError = findViewById(R.id.tvError);
        tvError2 = findViewById(R.id.tvError2);
        tvError3 = findViewById(R.id.tvError3);
        savePw = findViewById(R.id.savePw);
        loadingBar = findViewById(R.id.loadingBar);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        tinyDB = new TinyDB(this);

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString().trim();
                if (password.isEmpty()) {
                    tvError.setText("* Không được để trống");
                    tvError.setVisibility(View.VISIBLE);
                } else if (!StringHelper.isValidPassword(password)) {
                    tvError.setText("* Mật khẩu từ 8 kí tự trở lên, bao gồm chữ hoa, chữ thường và chữ số");
                    tvError.setVisibility(View.VISIBLE);
                } else {
                    tvError.setVisibility(View.GONE);
                }
            }
        });
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString().trim();
                String confirm = cfPassword.getText().toString().trim();
                if (!confirm.isEmpty() && !confirm.equals(password)) {
                    tvError3.setText("* Mật khẩu xác nhận không khớp");
                    tvError3.setVisibility(View.VISIBLE);
                }
                else if(confirm.equals(password)){
                    tvError3.setVisibility(View.GONE);
                }
                if (password.isEmpty()) {
                    tvError2.setText("* Không được để trống");
                    tvError2.setVisibility(View.VISIBLE);
                } else if (!StringHelper.isValidPassword(password)) {
                    tvError2.setText("* Mật khẩu từ 8 kí tự trở lên, bao gồm chữ hoa, chữ thường và chữ số");
                    tvError2.setVisibility(View.VISIBLE);
                } else {
                    tvError2.setVisibility(View.GONE);
                }
            }
        });


        cfPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String confirm = s.toString().trim();
                String newPwd = newPassword.getText().toString().trim();

                if (confirm.isEmpty()) {
                    tvError3.setText("* Không được để trống");
                    tvError3.setVisibility(View.VISIBLE);
                } else if (!newPwd.isEmpty() && !confirm.equals(newPwd)) {
                    tvError3.setText("* Mật khẩu xác nhận không khớp");
                    tvError3.setVisibility(View.VISIBLE);
                } else if (!StringHelper.isValidPassword(confirm)) {
                    tvError3.setText("* Mật khẩu từ 8 kí tự trở lên, bao gồm chữ hoa, chữ thường và chữ số");
                    tvError3.setVisibility(View.VISIBLE);
                } else {
                    tvError3.setVisibility(View.GONE);
                }
            }
        });
        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Khi mất focus (người dùng rời khỏi ô nhập)
                if (!hasFocus) {
                    String input = edtPassword.getText().toString().trim();
                    if (input.isEmpty()) {
                        tvError.setText("Không được để trống");
                        tvError.setVisibility(View.VISIBLE);
                    } else {
                        tvError.setVisibility(View.GONE);
                    }
                }
            }
        });
        newPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Khi mất focus (người dùng rời khỏi ô nhập)
                if (!hasFocus) {
                    String input = newPassword.getText().toString().trim();
                    if (input.isEmpty()) {
                        tvError2.setText("Không được để trống");
                        tvError2.setVisibility(View.VISIBLE);
                    } else {
                        tvError2.setVisibility(View.GONE);
                    }
                }
            }
        });
        cfPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Khi mất focus (người dùng rời khỏi ô nhập)
                if (!hasFocus) {
                    String input = cfPassword.getText().toString().trim();
                    if (input.isEmpty()) {
                        tvError3.setText("Không được để trống");
                        tvError3.setVisibility(View.VISIBLE);
                    } else {
                        tvError3.setVisibility(View.GONE);
                    }
                }
            }
        });


        savePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvError.isShown() || tvError2.isShown() || tvError3.isShown()){
                    return;
                }
                if(edtPassword.getText().toString().isEmpty()){
                    tvError.setText("Không được để trống");
                    tvError.setVisibility(View.VISIBLE);
                }
                if(newPassword.getText().toString().isEmpty()){
                    tvError2.setText("Không được để trống");
                    tvError2.setVisibility(View.VISIBLE);
                }
                if(cfPassword.getText().toString().isEmpty()){
                    tvError3.setText("Không được để trống");
                    tvError3.setVisibility(View.VISIBLE);
                }
                else {
                    User user = tinyDB.getObject("savedUser",User.class);
                    String pass = edtPassword.getText().toString();
                    String newPass = newPassword.getText().toString();
                    savePassNewPw(user,pass,newPass);
                }
            }
        });

    }
    private void savePassNewPw(User user, String password, String newPassword) {
        String url = UrlUtil.ADDRESS + "user/change-pw";
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingBar.playAnimation();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", user.getEmail());
            jsonBody.put("password", password);
            jsonBody.put("newPassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyHelper volleyHelper = VolleyHelper.getInstance(this);
        volleyHelper.sendStringRequestWithAuth(
                Request.Method.PUT,
                url,
                jsonBody.toString(),
                true,
                response -> {
                    if (response.equals("Change password successful!")) {
                        loadingOverlay.setVisibility(View.GONE);
                        loadingBar.cancelAnimation();
                        Toast.makeText(this,"Cập nhật mật khẩu thành công",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePwdActivity.this, SettingUserActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        loadingOverlay.setVisibility(View.GONE);
                        loadingBar.cancelAnimation();
                        Toast.makeText(this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingOverlay.setVisibility(View.GONE);
                    loadingBar.cancelAnimation();
                    Toast.makeText(this, "Lỗi cập nhật. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
        );
    }


}