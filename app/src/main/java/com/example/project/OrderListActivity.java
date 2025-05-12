package com.example.project;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.project.adapter.PaymentListAdapter;
import com.example.project.helpers.StringHelper;
import com.example.project.helpers.TinyDB;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    private TinyDB tinyDB;
    private RecyclerView recyclerView;
    private ActivityResultLauncher<Intent> orderDetailLauncher;
    private PaymentListAdapter paymentListAdapter;
    private List<Payment> payments;
    private RequestQueue requestQueue;
    private LottieAnimationView loading_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        ConstraintLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
            return insets;
        });

        payments = new ArrayList<>();
        loading_spinner = findViewById(R.id.loading_spinner);
        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        recyclerView = findViewById(R.id.paymentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tinyDB = new TinyDB(this);

        orderDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getBooleanExtra("canceled", false)) {
                            payments.clear();
                            getPayment(0);
                        }
                    }
                }
        );
        paymentListAdapter = new PaymentListAdapter(OrderListActivity.this, payments,orderDetailLauncher);
        recyclerView.setAdapter(paymentListAdapter);
        getPayment(0);


    }

    private void getPayment(int currentPage) {
        String url;
        int userId;

        if (tinyDB.getBoolean("is_logged_in")) {
            User savedUser = tinyDB.getObject("savedUser", User.class);
            userId = savedUser.getId();
            url = UrlUtil.ADDRESS + "payment?userId=" + userId + "&page=" + currentPage + "&pageSize=" + 6;
        } else {
            ArrayList<Integer> payments = tinyDB.getListInt("paymentList");
            userId = 1;
            String tem = StringHelper.createPaymentIdsQuery(payments);
            url = UrlUtil.ADDRESS + "payment/findByIds?" + tem + "&page=" + currentPage + "&pageSize=" + 6;
        }

        int finalUserId = userId;
        loading_spinner.setVisibility(VISIBLE);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("content");
                        boolean hasMoreData = jsonArray.length() > 0; // Check if there are more items

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject payment = jsonArray.getJSONObject(i);
                            JSONObject shop = payment.getJSONObject("shop");
                            AddressShop addressShop = new AddressShop(shop.getString("name"));
                            String addressUser = payment.getString("addressUser");
                            String fullname = payment.getString("fullName");
                            Long totalprice = payment.getLong("totalPrice");
                            Boolean utensils = payment.getBoolean("utensils");
                            String sdt = payment.getString("sdt");
                            int paymentId = payment.getInt("id");
                            String note = payment.getString("note");
                            String status = payment.getString("status");
                            String orDate = payment.getString("orderDate");
                            LocalDateTime orderDate = parseTime(orDate);
                            String method = payment.getString("paymentMethod");
                            JSONArray productsArray = payment.getJSONArray("products");
                            List<PaymentProduct> productList = new ArrayList<>();

                            for (int j = 0; j < productsArray.length(); j++) {
                                JSONObject productObj = productsArray.getJSONObject(j);
                                JSONObject jsonObject = productObj.getJSONObject("product");
                                int productId = jsonObject.getInt("id");
                                String name = jsonObject.getString("name");
                                int price = jsonObject.getInt("price");
                                String imageId = jsonObject.getString("attachmentId");
                                String image = UrlUtil.ADDRESS + "download/" + imageId;
                                String des = jsonObject.isNull("description") ? "" : jsonObject.getString("description");
                                int soldCount = jsonObject.getInt("soldCount");
                                int quantity = productObj.getInt("quantity");
                                Food food = new Food(productId, name, price, image, soldCount, des);
                                PaymentProduct product = new PaymentProduct(food, quantity);
                                productList.add(product);
                            }

                            User user = new User(finalUserId);
                            Payment payment1 = new Payment(paymentId,user, addressUser, addressShop, fullname, sdt, note, utensils, totalprice, orderDate, productList, method, status);
                            Log.e("ID", String.valueOf(payment1.getId()));
                            payments.add(payment1);
                        }
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            loading_spinner.setVisibility(GONE);
                            paymentListAdapter.notifyDataSetChanged();

                            // Tự động tăng số trang khi load xong trang này
                            if (hasMoreData) {
                                getPayment(currentPage + 1);
                            }
                        }, 2000);

                    } catch (JSONException e) {
                        Toast.makeText(OrderListActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        Log.e("ERROR", e.toString());
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            loading_spinner.setVisibility(GONE);
                        }, 2000);
                    }
                },
                error -> {

                    Log.e("NETWORK_ERROR", error.toString());
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        loading_spinner.setVisibility(GONE);
                    }, 2000);
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
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

        return LocalDateTime.of(year, month, day, hour, minute, second, nano);
    }

}
