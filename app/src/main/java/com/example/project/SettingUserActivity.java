package com.example.project;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.project.helpers.TinyDB;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingUserActivity extends AppCompatActivity {
    private SwitchMaterial switchNotification;
    TextView logoutBtn;
    TinyDB tinyDB;
    private RequestQueue requestQueue;
    ConstraintLayout changePwdBtn,changeLanguBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
            getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
        // Ánh xạ
        switchNotification = findViewById(R.id.switch_notification);
        changePwdBtn = findViewById(R.id.changePwdBtn);
        changeLanguBtn = findViewById(R.id.changeLanguBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        tinyDB = new TinyDB(this);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String url = UrlUtil.ADDRESS + "logout";
                 StringRequest stringRequest = new StringRequest(
                         Request.Method.GET,
                         url,
                         new Response.Listener<String>() {
                             @Override
                             public void onResponse(String response) {
                                 tinyDB.remove("token");
                                 tinyDB.remove("userId");
                                 tinyDB.remove("username");
                                 tinyDB.remove("addressShop");
                                 tinyDB.putBoolean("is_logged_in", false);

                                 Log.d("Logout", "Token after logout: " + tinyDB.getString("token"));

                                 // Parse the JSON response from the backend
                                 Toast.makeText(SettingUserActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                                 Intent intent = new Intent(SettingUserActivity.this, LoginActivity.class);
                                 startActivity(intent);
                             }
                         },
                         new Response.ErrorListener() {
                             @Override
                             public void onErrorResponse(VolleyError error) {

                                 Toast.makeText(SettingUserActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                             }
                         }) {
                     @Override
                     public Map<String, String> getHeaders() throws AuthFailureError {
                         Map<String, String> headers = new HashMap<>();
                         String token = tinyDB.getString("token");
                         headers.put("Authorization", "Bearer " + token); // Thêm token nếu cần
                         return headers;
                     }
                 };
                 requestQueue.add(stringRequest);
             }
         });
        changePwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingUserActivity.this,ChangePwdActivity.class));
            }
        });
        changeLanguBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingUserActivity.this,ChangeLanguageActivity.class));
            }
        });


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
        }
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
