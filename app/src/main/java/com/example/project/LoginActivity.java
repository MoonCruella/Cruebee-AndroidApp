package com.example.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        email= (EditText) findViewById(R.id.email);
        password= (EditText) findViewById(R.id.password);
    }

    public void openHomeActivity(View view){
        if(!validateEmail() || !validatePassword()){
            return;
        }

        String email1 = email.getText().toString();
        String password1 = password.getText().toString();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading... Please wait...!!");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://196.169.4.27:8888/loginn",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        try {
                            // Parse the JSON response from the backend
                            JSONObject jsonResponse = new JSONObject(response);
                            // Assuming the token is in the 'token' field
                            if (jsonResponse.has("token")) {

                                token = jsonResponse.getString("token");
                                System.out.println(token);
                                // Store the token in SharedPreferences for future use
                                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("auth_token", token);  // Save token with the key 'auth_token'
                                editor.apply();

                                // Proceed to the HomeActivity (or other actions)
                                Intent intent = new Intent(LoginActivity.this, HomeFragment.class);
                                intent.putExtra("USER_EMAIL", email1);
                                startActivity(intent);
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error processing login response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(LoginActivity.this,"" + error,Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                // Create a JSONObject and put data into it
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("email", email1);
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
                return headerMap;
            }
        };

        //Fix Volley time out error
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void openForgotPassActivity(View view){
        startActivity(new Intent(LoginActivity.this,ForgotPassActivity.class));
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
        if(pass.isEmpty()){
            password.setError("Password cannot be empty!");
            return false;
        }
        else if(!StringHelper.isValidPassword(pass)){
            password.setError("Password is not valid!");
            return false;
        }
        password.setError(null);
        return true;
    }


}