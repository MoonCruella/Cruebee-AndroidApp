package com.example.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.adapter.AddressShopAdapter;
import com.example.project.adapter.PromotionAdapter;
import com.example.project.helpers.TinyDB;
import com.example.project.model.AddressShop;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressShopActivity extends AppCompatActivity {

    ImageView editAddress;
    TextView addressTxt;
    TinyDB tinyDB;
    private RecyclerView recyclerView;
    private AddressShopAdapter adapter;

    List<AddressShop> addressShops;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_shop);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        init();

        double lat = tinyDB.getDouble("lat");
        double lng = tinyDB.getDouble("lng");
        findShopIn10Km(lat,lng);


    }

    private void init(){
        editAddress = findViewById(R.id.editAddress);
        addressTxt = findViewById(R.id.addressTxt);
        addressShops = new ArrayList<>();
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        recyclerView = findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        editAddress.setOnClickListener(v -> startActivity(new Intent(AddressShopActivity.this,AddressActivity.class)));
        tinyDB = new TinyDB(this);
        String userAddress = tinyDB.getString("user_address");
        addressTxt.setText(userAddress);


    }

    private void findShopIn10Km(double lat, double lng) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading... Please wait...!!");
        progressDialog.show();

        String url = UrlUtil.ADDRESS + "shops/shop-in-10-km?lat=" + lat + "&lng=" + lng;

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        try {
                            // Parse the response manually
                            JSONArray jsonArray = new JSONArray(response);
                            StringBuilder result = new StringBuilder();

                            // Iterate through the JSON array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject shopDistanceObject = jsonArray.getJSONObject(i);
                                double distance = shopDistanceObject.getDouble("distance");
                                JSONObject shopObject = shopDistanceObject.getJSONObject("shop");

                                // Extract shop details
                                int id = shopObject.getInt("id");
                                String shopName = shopObject.getString("name");
                                String shopAddress = shopObject.getString("address");
                                String shopOpenTime = shopObject.getString("openTime");
                                String shopPhone = shopObject.getString("phone");
                                // Ensure correct handling of the text
                                shopName = new String(shopName.getBytes(), StandardCharsets.UTF_8);
                                shopAddress = new String(shopAddress.getBytes(), StandardCharsets.UTF_8);
                                addressShops.add(new AddressShop(id,shopName,shopAddress,shopOpenTime,shopPhone,distance));
                            }

                            updateRecyclerView(addressShops);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddressShopActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(AddressShopActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void updateRecyclerView(List<AddressShop> addressShops) {

        // Kiểm tra xem list có trống hay không
        if (addressShops.isEmpty()) {

            // Hiển thị thông báo nếu không có dữ liệu
            findViewById(R.id.noItemsText).setVisibility(View.VISIBLE);
            findViewById(R.id.listView).setVisibility(View.GONE); // Ẩn RecyclerView
        } else {

            // Ẩn thông báo và hiển thị RecyclerView nếu có dữ liệu
            findViewById(R.id.noItemsText).setVisibility(View.GONE);
            findViewById(R.id.listView).setVisibility(View.VISIBLE);

            // Cập nhật adapter cho RecyclerView
            adapter = new AddressShopAdapter(AddressShopActivity.this, addressShops);
            recyclerView.setAdapter(adapter);
        }
    }



}