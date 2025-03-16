package com.example.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private TextView text;
    private ViewFlipper viewFlipper;

    private ArrayList<Integer> discountList = new ArrayList<Integer>();
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

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", null);


        init();

    }

    public void init() {
        viewFlipper = findViewById(R.id.discountView);
        Animation inAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        viewFlipper.setInAnimation(inAnimation);
        viewFlipper.setOutAnimation(outAnimation);

        discountList.add(R.drawable.banner_discount_01);
        discountList.add(R.drawable.banner_discount_02);
        discountList.add(R.drawable.banner_discount_03);
        discountList.add(R.drawable.banner_discount_04);

        for (int i = 0; i < discountList.size(); i++) {
            ImageView imageView = new ImageView(this);  // Create a new ImageView for each iteration
            imageView.setImageResource(discountList.get(i));
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewFlipper.addView(imageView);
        }
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
    public void openHomeActivity(View view){
        Intent intent = new Intent(HomeActivity.this,HomeActivity.class);
        startActivity(intent);
    }
    public void openShowMoreActivity(View view){
        Intent intent = new Intent(HomeActivity.this,ShowMoreAcitivity.class);
        startActivity(intent);
    }
    public void openDiscountActivity(View view){
        Intent intent = new Intent(HomeActivity.this,DiscountActivity.class);
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