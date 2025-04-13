package com.example.project;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.project.helpers.TinyDB;
import com.example.project.model.User;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditAccountActivity extends AppCompatActivity {
    private TextInputEditText sdtTxt,usernameTxt,emailTxt;
    private AutoCompleteTextView genderTxt;
    private TextView saveTxt;
    private TinyDB tinyDB;
    private String token;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
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
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
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

        StringRequest request = new StringRequest(
                Request.Method.PUT,
                url,
                response -> {
                    Log.d("UpdateUser", "Response: " + response);
                    Toast.makeText(this, "Cập nhật tài khoản thành công!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("UpdateUser", "Lỗi: " + error.toString());
                    Toast.makeText(this, "Lỗi cập nhật: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("id", user.getId());
                    jsonBody.put("username", user.getUsername());
                    jsonBody.put("email", user.getEmail());
                    jsonBody.put("gender", user.getGender());
                    jsonBody.put("sdt", user.getSdt());
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