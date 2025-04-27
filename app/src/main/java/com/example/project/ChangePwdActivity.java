package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.helpers.StringHelper;
import com.example.project.helpers.TinyDB;
import com.example.project.model.User;
import com.example.project.utils.UrlUtil;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ChangePwdActivity extends AppCompatActivity {

    private TextInputEditText edtPassword,newPassword,cfPassword;
    private TextView tvError, tvError2, tvError3,savePw;
    private TinyDB tinyDB;
    private String token;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        ConstraintLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(0, 0, 0, navBarInsets.bottom); // đẩy layout lên khỏi nav bar
            return insets;
        });
        requestQueue = Volley.newRequestQueue(this);
        edtPassword = findViewById(R.id.edtPassword);
        newPassword = findViewById(R.id.newPassword);
        cfPassword = findViewById(R.id.cfPassword);
        tvError = findViewById(R.id.tvError);
        tvError2 = findViewById(R.id.tvError2);
        tvError3 = findViewById(R.id.tvError3);
        savePw = findViewById(R.id.savePw);
        tinyDB = new TinyDB(this);
        token = tinyDB.getString("token");

        edtPassword.addTextChangedListener(new TextWatcher() {
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
    private void savePassNewPw(User user,String password,String newPassword){
        String url = UrlUtil.ADDRESS + "user/change-pw";
        StringRequest request = new StringRequest(
                Request.Method.PUT,
                url,
                response -> {
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                    if(response.equals("Cập nhật mật khẩu thành công!")){
                        Intent intent=new Intent(ChangePwdActivity.this,SettingUserActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                error -> {
                    Log.e("UpdatePassword", "Lỗi: " + error.toString());
                    Toast.makeText(this, "Lỗi cập nhật: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("email", user.getEmail());
                    jsonBody.put("password", password);
                    jsonBody.put("newPassword", newPassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(request);
    }

}