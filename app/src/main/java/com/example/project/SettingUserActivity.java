package com.example.project;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.project.helpers.TinyDB;
import com.example.project.model.User;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleyHelper;
import com.example.project.volley.VolleySingleton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SettingUserActivity extends AppCompatActivity {
    private SwitchMaterial switchNotification;
    TextView logoutBtn,deleteBtn;
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
        deleteBtn = findViewById(R.id.deleteAccBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        tinyDB = new TinyDB(this);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = UrlUtil.ADDRESS + "logout";

                VolleyHelper.getInstance(SettingUserActivity.this).sendStringRequestWithAuth(
                        Request.Method.GET,
                        url,
                        null,
                        true,
                        response -> {
                            // ✅ Xoá thông tin local
                            tinyDB.remove("token");
                            tinyDB.remove("addressShop");
                            tinyDB.remove("refresh_token");
                            tinyDB.remove("savedUser");
                            tinyDB.putBoolean("is_logged_in", false);

                            Log.d("Logout", "Token after logout: " + tinyDB.getString("token"));

                            Toast.makeText(SettingUserActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

                            // Chuyển về màn hình đăng nhập
                            Intent intent = new Intent(SettingUserActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear backstack
                            startActivity(intent);
                        },
                        error -> {
                            Toast.makeText(SettingUserActivity.this, "Lỗi khi đăng xuất: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                );
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
    private void showConfirmDialog(){
        ConstraintLayout errorConstrlayout = findViewById(R.id.successConstraintLayout);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete,errorConstrlayout);
        TextView okBtn = view.findViewById(R.id.okBtn);
        TextView cancelBtn = view.findViewById(R.id.cancelBtn);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        cancelBtn.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        okBtn.findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                showConfirmPassword();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showConfirmPassword(){
        ConstraintLayout errorConstrlayout = findViewById(R.id.successConstraintLayout);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_pass,errorConstrlayout);
        TextView okBtn = view.findViewById(R.id.okBtn);
        TextView cancelBtn = view.findViewById(R.id.cancelBtn);
        EditText passwordEdt = view.findViewById(R.id.password);

        User user = tinyDB.getObject("savedUser", User.class);
        String email = user.getEmail();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        cancelBtn.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        okBtn.findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEdt.getText().toString().trim();
                deleteAccount(alertDialog,email,password);
            }
        });
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void deleteAccount(AlertDialog alertDialog, String email, String password) {
        String url = UrlUtil.ADDRESS + "user/delete-account";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VolleyHelper.getInstance(this).sendStringRequestWithAuth(
                Request.Method.POST,
                url,
                jsonBody.toString(),
                true,
                response -> {
                    if (response.toString().equals("Delete Success!")) {
                        tinyDB.remove("token");
                        tinyDB.remove("addressShop");
                        tinyDB.remove("savedUser");
                        tinyDB.putBoolean("is_logged_in", false);

                        alertDialog.dismiss();
                        showAnnDeleteSuccess();
                    } else {
                        Toast.makeText(SettingUserActivity.this, "Sai mật khẩu!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(SettingUserActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void showAnnDeleteSuccess(){
        ConstraintLayout errorConstrlayout = findViewById(R.id.successConstraintLayout);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete_acc_success,errorConstrlayout);
        TextView okBtn = view.findViewById(R.id.okBtn);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        okBtn.findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(SettingUserActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
