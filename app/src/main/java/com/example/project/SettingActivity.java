package com.example.project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.project.helpers.TinyDB;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.content.Intent;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;

public class SettingActivity extends AppCompatActivity {
    private SwitchMaterial switchNotification;
    private ConstraintLayout changeLanguBtn;
    private TextView btn_login;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        // Ánh xạ
        switchNotification = findViewById(R.id.switch_notification);
        changeLanguBtn = findViewById(R.id.changeLanguBtn);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(v -> {
            TinyDB tinyDB = new TinyDB(SettingActivity.this);
            tinyDB.remove("addressShop");
            startActivity(new Intent(SettingActivity.this,LoginActivity.class));
        });

        changeLanguBtn.setOnClickListener(v -> startActivity(new Intent(SettingActivity.this,ChangeLanguageActivity.class)));


        // Cập nhật trạng thái switch khi mở app
        updateSwitchState();

        switchNotification.setOnClickListener(v -> {
            openNotificationSettings();
        });
    }

    // Cập nhật trạng thái switch dựa vào cài đặt thông báo của hệ thống
    private void updateSwitchState() {

        // Kiểm tra xem ứng dụng có được cấp quyền thông báo không
        boolean ischecked = NotificationManagerCompat.from(this).areNotificationsEnabled();

        switchNotification.setChecked(ischecked);
    }

    private void openNotificationSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật trạng thái switch
        updateSwitchState();
    }
}
