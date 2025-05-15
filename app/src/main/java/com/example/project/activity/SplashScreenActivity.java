package com.example.project.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.project.R;
import com.example.project.helpers.TinyDB;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private TinyDB tinyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        tinyDB = new TinyDB(this);
        String token = tinyDB.getString("token");
        Log.d("TOKEN", "Token after logout: " + tinyDB.getString("token"));

        // Giả lập màn hình chờ 2 giây trước khi kiểm tra token
        new Handler().postDelayed(() -> {
            if (token != null && !token.isEmpty()) {

                // Nếu đã đăng nhập, chuyển đến HomeActivity
                startActivity(new Intent(SplashScreenActivity.this, BaseActivity.class));
            } else {
                // Nếu chưa đăng nhập, chuyển đến LoginActivity
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            }
            finish(); // Kết thúc SplashScreenActivity
        }, 0);

    }
}