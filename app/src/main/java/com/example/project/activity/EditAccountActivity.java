package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.example.project.R;
import com.example.project.helpers.TinyDB;
import com.example.project.model.User;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleyHelper;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class EditAccountActivity extends AppCompatActivity {
    private TextInputEditText sdtTxt,usernameTxt,emailTxt;
    private AutoCompleteTextView genderTxt;
    private TextView saveTxt;
    private TinyDB tinyDB;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        ConstraintLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
            return insets;
        });
        init();
    }

    private void init(){
        saveTxt = findViewById(R.id.saveTxt);
        sdtTxt = findViewById(R.id.sdtTxt);
        usernameTxt = findViewById(R.id.usernameTxt);
        emailTxt = findViewById(R.id.emailTxt);
        genderTxt = findViewById(R.id.genderTxt);
        tinyDB = new TinyDB(this);
        token = tinyDB.getString("token");
        Log.d("TOKEN",token);
        List<String> genderList = Arrays.asList("Nam", "Nữ", "Khác");

        // Tạo Adapter cho dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, genderList);
        genderTxt.setAdapter(adapter);
        User user = tinyDB.getObject("savedUser",User.class);
        usernameTxt.setText(user.getUsername());
        emailTxt.setText(user.getEmail());
        sdtTxt.setText(user.getSdt());
        genderTxt.setText(user.getGender(),false);
        saveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setSdt(sdtTxt.getText().toString());
                user.setGender(genderTxt.getText().toString());
                user.setUsername(usernameTxt.getText().toString());
                updateUser(user);
                tinyDB.putObject("savedUser",user);
                Intent intent=new Intent(EditAccountActivity.this,BaseActivity.class);
                intent.putExtra("opened_fragment", "SHOW_MORE");
                startActivity(intent);
                finish();
            }
        });
    }
    private void updateUser(User user) {
        String url = UrlUtil.ADDRESS + "user/update";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id", user.getId());
            requestBody.put("username", user.getUsername());
            requestBody.put("email", user.getEmail());
            requestBody.put("gender", user.getGender());
            requestBody.put("sdt", user.getSdt());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tạo dữ liệu gửi đi!", Toast.LENGTH_SHORT).show();
            return;
        }

        VolleyHelper volleyHelper = VolleyHelper.getInstance(this);
        volleyHelper.sendJsonObjectRequestWithAuth(
                Request.Method.PUT,
                url,
                requestBody,
                true,
                response -> {
                    Toast.makeText(this, "Cập nhật tài khoản thành công!", Toast.LENGTH_SHORT).show();
                },
                error -> {

                }
        );
    }


}