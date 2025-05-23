package com.example.project.activity;

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
import com.example.project.R;
import com.example.project.adapter.AddressShopAdapter;
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

public class AddressShopActivity extends AppCompatActivity {

    private ImageView editAddress;
    private TextView addressTxt;
    private TinyDB tinyDB;
    private RecyclerView recyclerView;
    private AddressShopAdapter adapter;
    double lat,lng;

    private List<AddressShop> addressShops;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_shop);
        ConstraintLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
            return insets;
        });

        init();

        lat = tinyDB.getDouble("lat");
        lng = tinyDB.getDouble("lng");
        findShopIn10Km(lat,lng);


    }
    @Override
    public void onResume() {
        super.onResume();
        boolean hasAddress = tinyDB.getAll().containsKey("address");
        boolean hasUAddress = tinyDB.getAll().containsKey("addr_no_log");
        if(hasUAddress){
            String fullAddress = tinyDB.getString("addr_no_log");
            addressTxt.setText(fullAddress);
        }
        if (tinyDB.getBoolean("is_logged_in") && hasAddress) {
            addressTxt.setText(tinyDB.getObject("address", Address.class).getAddress_details());
            lat = tinyDB.getObject("address",Address.class).getLatitude();
            lng = tinyDB.getObject("address",Address.class).getLongitude();
            tinyDB.putDouble("lat",lat);
            tinyDB.putDouble("lng",lng);
        }
    }

    private void init(){
        editAddress = findViewById(R.id.editAddress);
        addressTxt = findViewById(R.id.addressTxt);
        addressShops = new ArrayList<>();
        adapter = new AddressShopAdapter(this, addressShops);
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        recyclerView = findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        editAddress.setOnClickListener(v -> startActivity(new Intent(AddressShopActivity.this,AddressActivity.class)));
        tinyDB = new TinyDB(this);
        boolean hasAddress = tinyDB.getAll().containsKey("address");
        boolean hasUAddress = tinyDB.getAll().containsKey("addr_no_log");
        if (tinyDB.getBoolean("is_logged_in") && hasAddress) {
            addressTxt.setText(tinyDB.getObject("address", Address.class).getAddress_details());
            lat = tinyDB.getObject("address",Address.class).getLatitude();
            lng = tinyDB.getObject("address",Address.class).getLongitude();
            tinyDB.putDouble("lat",lat);
            tinyDB.putDouble("lng",lng);
        }
        else{
            if(hasUAddress){
                String fullAddress = tinyDB.getString("addr_no_log");
                addressTxt.setText(fullAddress);
            }
            else{
                addressTxt.setText("Thêm địa chỉ/cửa hàng");
            }
        }
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
                        progressDialog.dismiss();
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
                                AddressShop shop = new AddressShop(id,shopName,shopAddress,shopOpenTime,shopPhone,distance);
                                addressShops.add(shop);
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
                        progressDialog.dismiss();
                        Toast.makeText(AddressShopActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ){
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

    @Override
    public void finish() {
        setResult(RESULT_OK); // Gửi kết quả thành công về
        super.finish();
    }


}