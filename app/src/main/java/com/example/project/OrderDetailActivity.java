package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.adapter.FoodListDetailAdapter;
import com.example.project.adapter.FoodListPaymentAdapter;
import com.example.project.interfaces.ChangeNumberItemsListener;
import com.example.project.model.Address;
import com.example.project.model.Payment;

import org.json.JSONException;

public class OrderDetailActivity extends AppCompatActivity {
    TextView idDonHang, timeOrder, phiGD, chiPhiDK, totalPrice, ptThanhtoan, nameSdt, diaChiOrder;
    RecyclerView recyclerView2;
    Payment payment;
    FoodListDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        init();
    }
    private void init(){
        idDonHang = findViewById(R.id.idDonhang);
        timeOrder = findViewById(R.id.timeOrder);
        phiGD = findViewById(R.id.phiGD);
        chiPhiDK = findViewById(R.id.chiPhiDK);
        totalPrice = findViewById(R.id.totalPrice);
        ptThanhtoan = findViewById(R.id.ptThanhtoan);
        nameSdt = findViewById(R.id.nameSdt);
        diaChiOrder = findViewById(R.id.diaChiOrder);
        recyclerView2 = findViewById(R.id.recyclerView2);
        payment = (Payment) getIntent().getSerializableExtra("object");

        idDonHang.setText("Đơn hàng #1");
        timeOrder.setText(payment.getOrderDate().toString());
        phiGD.setText("20.000");
        chiPhiDK.setText(payment.getTotalPrice().toString());
        totalPrice.setText(payment.getTotalPrice().toString());
        ptThanhtoan.setText(payment.getPaymentMethod());
        nameSdt.setText(payment.getFullName() + " - " + payment.getSdt());
        diaChiOrder.setText(payment.getAddressUser());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView2.setLayoutManager(linearLayoutManager);
        adapter = new FoodListDetailAdapter(OrderDetailActivity.this,payment.getProducts());
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(OrderDetailActivity.this,DividerItemDecoration.VERTICAL);
        recyclerView2.addItemDecoration(decoration);
        recyclerView2.setAdapter(adapter);
    }
}