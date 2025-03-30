package com.example.project;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.helpers.StringHelper;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePwdActivity extends AppCompatActivity {

    private TextInputEditText edtPassword,newPassword,cfPassword;
    private TextView tvError, tvError2, tvError3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
            getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        edtPassword = findViewById(R.id.edtPassword);
        newPassword = findViewById(R.id.newPassword);
        cfPassword = findViewById(R.id.cfPassword);
        tvError = findViewById(R.id.tvError);
        tvError2 = findViewById(R.id.tvError2);
        tvError3 = findViewById(R.id.tvError3);


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
                // Thực hiện hành động nếu cần trước khi text thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Bạn có thể cập nhật giao diện khi text thay đổi nếu cần
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


    }
}