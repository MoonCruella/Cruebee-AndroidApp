package com.example.project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.volley.Request;
import com.example.project.helpers.TinyDB;
import com.example.project.model.User;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleyHelper;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.content.Intent;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingUserActivity extends AppCompatActivity {
    private SwitchMaterial switchNotification;
    private TextView logoutBtn,deleteBtn;
    private TinyDB tinyDB;
    private ConstraintLayout changePwdBtn,changeLanguBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user);
        ConstraintLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
            return insets;
        });
        // Ánh xạ
        switchNotification = findViewById(R.id.switch_notification);
        changePwdBtn = findViewById(R.id.changePwdBtn);
        changeLanguBtn = findViewById(R.id.changeLanguBtn);
        deleteBtn = findViewById(R.id.deleteAccBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        tinyDB = new TinyDB(this);

        deleteBtn.setOnClickListener(v -> showConfirmDialog());

        logoutBtn.setOnClickListener(v -> {
            String url = UrlUtil.ADDRESS + "logout";

            VolleyHelper.getInstance(SettingUserActivity.this).sendStringRequestWithAuth(
                    Request.Method.GET,
                    url,
                    null,
                    true,
                    response -> {

                        // Xoá thông tin local
                        tinyDB.remove("token");
                        tinyDB.remove("addressShop");
                        tinyDB.remove("refresh_token");
                        tinyDB.remove("savedUser");
                        tinyDB.putBoolean("is_logged_in", false);

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
        } catch (JSONException ignored) {

        }

        VolleyHelper.getInstance(this).sendStringRequestWithAuth(
                Request.Method.POST,
                url,
                jsonBody.toString(),
                true,
                response -> {
                    if (response.equals("Delete Success!")) {
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
