package com.example.project;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.View;

import com.example.project.helpers.TinyDB;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    TinyDB tinyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
            getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
        tinyDB = new TinyDB(this);
        String token = tinyDB.getString("token");


        // Giả lập màn hình chờ 2 giây trước khi kiểm tra token
        new Handler().postDelayed(() -> {
            if (token != null) {

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