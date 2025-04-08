package com.example.project;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.project.adapter.AddressShopAdapter;
import com.example.project.adapter.AddressUserAdapter;
import com.example.project.helpers.TinyDB;
import com.example.project.model.Address;
import com.example.project.model.AddressShop;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DeliveryAddressActivity extends AppCompatActivity {

    TinyDB tinyDB;
    private RecyclerView recyclerView;
    private AddressUserAdapter adapter;

    List<Address> addresses;
    private RequestQueue requestQueue;
    private TextView addAddressBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        addresses = new ArrayList<>();
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        recyclerView = findViewById(R.id.addressList);
        addAddressBtn = findViewById(R.id.addAddressBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tinyDB = new TinyDB(this);

        getAddressList();

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void getAddressList() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading... Please wait...!!");
        progressDialog.show();

        int userId = tinyDB.getInt("userId");
        String url = UrlUtil.ADDRESS + "addresses?userId=" + userId;

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

                            // Iterate through the JSON array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject address = jsonArray.getJSONObject(i);

                                // Extract shop details
                                int id = address.getInt("id");
                                int isPrimary = address.getInt("isPrimary");
                                String addressDetails = address.getString("addressDetails");
                                int latitude = address.getInt("latitude");
                                int longitude = address.getInt("longitude");

                                addresses.add(new Address(id,isPrimary,addressDetails,latitude,longitude,userId));
                            }
                            // Cập nhật adapter cho RecyclerView
                            adapter = new AddressUserAdapter(DeliveryAddressActivity.this,addresses);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DeliveryAddressActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(DeliveryAddressActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

}