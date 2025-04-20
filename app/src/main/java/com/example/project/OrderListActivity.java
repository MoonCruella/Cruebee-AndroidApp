package com.example.project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.example.project.adapter.PaymentListAdapter;
import com.example.project.helpers.TinyDB;

import com.example.project.model.Address;
import com.example.project.model.AddressShop;
import com.example.project.model.Food;
import com.example.project.model.Payment;
import com.example.project.model.PaymentProduct;
import com.example.project.model.User;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderListActivity extends AppCompatActivity {
    TinyDB tinyDB;
    private RecyclerView recyclerView;
    private PaymentListAdapter paymentListAdapter;
    List<Payment> payments;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        payments = new ArrayList<>();
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        recyclerView = findViewById(R.id.paymentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tinyDB = new TinyDB(this);

        getPayment();


    }

    private void getPayment() {
        User savedUser = tinyDB.getObject("savedUser", User.class);
        int userId = savedUser.getId();
        String url = UrlUtil.ADDRESS + "payment?userId=" + userId;
        Log.d("USER ID", String.valueOf(userId));
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("FULL_RESPONSE", response);
                            payments.clear();
                            // Parse the response manually
                            JSONArray jsonArray = new JSONArray(response);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

                            // Iterate through the JSON array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject payment = jsonArray.getJSONObject(i);

                                // Extract shop details
                                int id = payment.getInt("id");
                                JSONObject shop = payment.getJSONObject("shop");
                                AddressShop addressShop = new AddressShop(shop.getString("name"));
                                String addressUser = payment.getString("addressUser");
                                String fullname = payment.getString("fullName");
                                Long totalprice = payment.getLong("totalPrice");
                                Boolean utensils = payment.getBoolean("utensils");
                                String sdt = payment.getString("sdt");
                                String note = payment.getString("note");
                                String orDate = payment.getString("orderDate");
                                LocalDateTime orderDate = parseTime(orDate);
                                String method = payment.getString("paymentMethod");
                                JSONArray productsArray = payment.getJSONArray("products");
                                List<PaymentProduct> productList = new ArrayList<>();

                                for (int j = 0; j < productsArray.length(); j++) {
                                    JSONObject productObj = productsArray.getJSONObject(j);
                                    JSONObject product1 = productObj.getJSONObject("product");
                                    int productId = product1.getInt("id");
                                    int quantity = productObj.getInt("quantity");
                                    Food food = new Food(productId);
                                    PaymentProduct product = new PaymentProduct(food, quantity);
                                    productList.add(product);
                                }
                                User user = new User(userId);
                                Payment payment1 = new Payment(user, addressUser, addressShop, fullname, sdt, note, utensils, totalprice, orderDate,productList,method);
                                payments.add(payment1);
                            }
                            // Cập nhật adapter cho RecyclerView
                            paymentListAdapter = new PaymentListAdapter(OrderListActivity.this, payments);
                            recyclerView.setAdapter(paymentListAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSON_ERROR", "Invalid JSON response: " + response);
                            Toast.makeText(OrderListActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OrderListActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private LocalDateTime parseTime(String dateTimeJson)
    {
        dateTimeJson = dateTimeJson.replaceAll("[\\[\\]\\s]", "");
        String[] parts = dateTimeJson.split(",");

        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        int hour = Integer.parseInt(parts[3]);
        int minute = Integer.parseInt(parts[4]);
        int second = Integer.parseInt(parts[5]);
        int nano = Integer.parseInt(parts[6]);

        LocalDateTime receiveTime = LocalDateTime.of(year, month, day, hour, minute, second, nano);
        return receiveTime;
    }
}