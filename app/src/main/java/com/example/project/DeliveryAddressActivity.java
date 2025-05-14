package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.project.adapter.AddressUserAdapter;
import com.example.project.helpers.TinyDB;
import com.example.project.model.Address;
import com.example.project.model.User;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DeliveryAddressActivity extends AppCompatActivity {

    private TinyDB tinyDB;
    private RecyclerView recyclerView;
    private AddressUserAdapter adapter;
     private List<Address> addresses;
    private RequestQueue requestQueue;
    private TextView addAddressBtn;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);
        ConstraintLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
            return insets;
        });


        addresses = new ArrayList<>();
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        recyclerView = findViewById(R.id.addressList);
        addAddressBtn = findViewById(R.id.addAddressBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressUserAdapter(DeliveryAddressActivity.this,addresses);
        recyclerView.setAdapter(adapter);
        tinyDB = new TinyDB(this);
        user = tinyDB.getObject("savedUser",User.class);

        getAddressList();

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user.getUsername();
                int userId = user.getId();
                String sdt = user.getSdt();
                Intent intent = new Intent(DeliveryAddressActivity.this, AddAddressActivity.class);
                Address address = new Address(0,0,null,0,0,userId,username,null,sdt);
                intent.putExtra("object",address);
                startActivity(intent);
            }
        });

    }

    private void getAddressList() {
        int userId = user.getId();
        Log.d("USERID : ", String.valueOf(userId));
        String url = UrlUtil.ADDRESS + "addresses?userId=" + userId;

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            addresses.clear();
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject address = jsonArray.getJSONObject(i);
                                int id = address.getInt("id");
                                int isPrimary = address.getInt("isPrimary");
                                String addressDetails = address.getString("addressDetails");
                                double latitude = address.getDouble("latitude");
                                double longitude = address.getDouble("longitude");
                                String username = address.getString("username");
                                String sdt = address.getString("sdt");
                                String note = address.getString("note");
                                Address address1 = new Address(id,isPrimary,addressDetails,latitude,longitude,userId,username,note,sdt);
                                Log.d("ADDRESS",address1.toString());
                                addresses.add(address1);
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
                        Toast.makeText(DeliveryAddressActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String utf8 = new String(response.data, StandardCharsets.UTF_8);
                    return Response.success(utf8, HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    @Override
    protected void onResume() {
        super.onResume();
        getAddressList();// Gọi lại hàm để lấy dữ liệu mới
    }
}
