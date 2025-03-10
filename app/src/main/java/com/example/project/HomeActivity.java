package com.example.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        text = (TextView) findViewById(R.id.textView);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", null);
        text.setText(token);



    }

    public void openCartActivity(View view){
        CartListActivity cartListActivity = new CartListActivity(HomeActivity.this);
        cartListActivity.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        cartListActivity.setCancelable(true);
        cartListActivity.show();
    }
    public void openMenuActivity(View view){
        Intent intent = new Intent(HomeActivity.this,MenuActivity.class);
        startActivity(intent);
    }
    public void returnMainActivity(View view){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading... Please wait...!!");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "http://196.169.4.27:8888/logout",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        Toast.makeText(HomeActivity.this, "Logout successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(HomeActivity.this,"" + error,Toast.LENGTH_SHORT).show();
                    }
                }
        ){
        };

        //Fix Volley time out error
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}