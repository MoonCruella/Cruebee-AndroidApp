package com.example.project;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.adapter.FoodListDetailAdapter;
import com.example.project.model.Payment;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView idDonHang, timeOrder, phiGD, chiPhiDK, totalPrice, ptThanhtoan, nameSdt, diaChiOrder,statusOrder;
    private RecyclerView recyclerView2;
    private Payment payment;
    private FoodListDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        EdgeToEdge.enable(this);
        ConstraintLayout mainLayout = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(0, 0, 0, navBarInsets.bottom); // đẩy layout lên khỏi nav bar
            return insets;
        });

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
        statusOrder = findViewById(R.id.statusOrder);
        diaChiOrder = findViewById(R.id.diachiOrder);
        recyclerView2 = findViewById(R.id.recyclerView2);

        payment = (Payment) getIntent().getSerializableExtra("object");
        int stt = getIntent().getIntExtra("stt",0);
        int color = 0;
        if(payment.getStatus().equals("PREPARING")){
            payment.setStatus("Đơn hàng của bạn đang được chuẩn bị");
            color = Color.parseColor("#FFA726");
        }
        if(payment.getStatus().equals("SHIPPING")){
            color = Color.parseColor("#42A5F5");
            payment.setStatus("Đơn hàng đang được giao đến bạn");
        }
        if(payment.getStatus().equals("DELIVERED")){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDate = payment.getOrderDate().plusMinutes(30).format(formatter);
            payment.setStatus("Đã giao thành công \n" + formattedDate );
            color = Color.parseColor("#66BB6A");
        }
        statusOrder.setText(payment.getStatus());
        statusOrder.setTextColor(color);
        idDonHang.setText("Đơn hàng #" + String.valueOf(stt));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDate = payment.getOrderDate().format(formatter);
        timeOrder.setText(formattedDate);
        phiGD.setText("10,000 đ");

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(payment.getTotalPrice()) + " đ";
        chiPhiDK.setText(formattedPrice);

        String formattedPrice2 = decimalFormat.format(payment.getTotalPrice() + 10000) + " đ";
        totalPrice.setText(formattedPrice2);

        ptThanhtoan.setText(payment.getPaymentMethod());
        nameSdt.setText(payment.getFullName() + " - " + payment.getSdt());
        diaChiOrder.setText(payment.getAddressUser());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView2.setLayoutManager(linearLayoutManager);
        adapter = new FoodListDetailAdapter(OrderDetailActivity.this,payment.getProducts());
        recyclerView2.setAdapter(adapter);
    }
}