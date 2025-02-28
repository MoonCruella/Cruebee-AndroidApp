package com.example.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText email,username, password, re_password;
    TextView register_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Hide title bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        email= (EditText) findViewById(R.id.email);
        username = (EditText) findViewById(R.id.username);
        password= (EditText) findViewById(R.id.password);
        re_password= (EditText) findViewById(R.id.re_password);

        register_btn = (TextView) findViewById(R.id.register);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(v);
            }
        });
    }

    public void registerUser(View view){

        if(!validateEmail() || !validateUsername() || !validatePassword()){
            return;
        }
        register_btn.setEnabled(false);
        String email1 = email.getText().toString();
        String username1 = username.getText().toString();
        String password1 = password.getText().toString();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading... Please wait...!!");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://196.169.4.27:8888/register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        Toast.makeText(RegisterActivity.this,"" + response,Toast.LENGTH_SHORT).show();
                        register_btn.setEnabled(true);
                        if(response.equals("User registration successful")){
                            Intent intent = new Intent(RegisterActivity.this,ConfirmOTPActivity.class);
                            // Su dung putExtra để pass biến email1 qua Activity khác
                            intent.putExtra("USER_EMAIL",email1);
                            startActivity(intent);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(RegisterActivity.this,"" + error,Toast.LENGTH_SHORT).show();
                        register_btn.setEnabled(true);
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                // Create a JSONObject and put data into it
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("email", email1);
                    jsonBody.put("username", username1);
                    jsonBody.put("password", password1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Return the byte[] of the JSON string
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                //headerMap.put("Authorization", "No Auth");
                return headerMap;
            }
        };

        //Fix Volley time out error
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    // Validate username field
    public boolean validateUsername(){
        String user_name = username.getText().toString();
        if(user_name.isEmpty()){
            username.setError("Username cannot be empty");
            return false;
        }
        username.setError(null);
        return true;
    }

    // Validate email field
    public boolean validateEmail(){
        String user_email = email.getText().toString();
        if(user_email.isEmpty()){
            email.setError("Email cannot be empty");
            return false;
        }
        else if(!StringHelper.isEmailValid(user_email)){
            email.setError("Please enter a valid email");
            return false;
        }
        email.setError(null);
        return true;
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