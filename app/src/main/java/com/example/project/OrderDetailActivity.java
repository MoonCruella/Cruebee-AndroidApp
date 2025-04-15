package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.model.Address;
import com.example.project.model.Payment;

public class OrderDetailActivity extends AppCompatActivity {
    TextView idDonHang, timeOrder, phiGD, chiPhiDK, totalPrice, ptThanhtoan, nameSdt, diaChiOrder;
    RecyclerView recyclerView2;
    Payment payment;

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

        idDonHang.setText("1");
        timeOrder.setText(payment.getOrderDate().toString());
        phiGD.setText("20.000đ");
        chiPhiDK.setText(payment.getTotalPrice().toString());
        totalPrice.setText(payment.getTotalPrice().toString());
        ptThanhtoan.setText(payment.getPaymentMethod());
        nameSdt.setText(payment.getFullName() + " - " + payment.getSdt());
        diaChiOrder.setText(payment.getAddressUser());
    }
}