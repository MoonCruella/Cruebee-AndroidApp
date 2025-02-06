package com.example.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.example.project.helpers.StringHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NewPasswordActivity extends AppCompatActivity {
    EditText password,re_password;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        password = (EditText) findViewById(R.id.password);
        re_password= (EditText) findViewById(R.id.re_password);
        Intent intent = getIntent();
        email = intent.getStringExtra("USER_EMAIL");
    }

    public void openMainActivity(View view){
        if(!validatePassword()){
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading... Please wait...!!");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                "http://192.168.1.7:8888/reset-password",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        Toast.makeText(NewPasswordActivity.this,"" + response,Toast.LENGTH_SHORT).show();
                        if(response.equals("Change password successful!")){
                            Intent intent = new Intent(NewPasswordActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(NewPasswordActivity.this, "Change Password failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(NewPasswordActivity.this,"" + error,Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("email", email);
                hashMap.put("password",password.getText().toString());
                return hashMap;
            }

        };

        //Fix Volley time out error
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    // Validate password field
    public boolean validatePassword(){
        String pass = password.getText().toString();
        String pass_confirm = re_password.getText().toString();
        if(pass.isEmpty() || pass_confirm.isEmpty()){
            password.setError("Password cannot be empty!");
            re_password.setError("Confirm field cannot be empty!");
            return false;
        }
        else if(!pass.equals(pass_confirm)){
            password.setError(null);
            re_password.setError("Confirm field doesn't match with password!!!");
            return false;
        }
        else if(!StringHelper.isValidPassword(pass)){
            password.setError("Password is not valid!");
            return false;
        }
        password.setError(null);
        re_password.setError(null);
        return true;
    }

}

