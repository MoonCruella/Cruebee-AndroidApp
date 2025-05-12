package com.example.project;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.adapter.FoodListDetailAdapter;
import com.example.project.model.Payment;
import com.example.project.utils.UrlUtil;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;


public class OrderDetailActivity extends AppCompatActivity {
    private TextView idDonHang, timeOrder, phiGD, chiPhiDK, totalPrice, ptThanhtoan, nameSdt, diaChiOrder,statusOrder,cancelOrder;
    private RecyclerView recyclerView2;
    private Payment payment;
    private FoodListDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        FrameLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
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
        cancelOrder = findViewById(R.id.cancelOrder);
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
            cancelOrder.setVisibility(VISIBLE);
        }
        if(payment.getStatus().equals("SHIPPING")){
            color = Color.parseColor("#42A5F5");
            payment.setStatus("Đơn hàng đang được giao đến bạn");
            cancelOrder.setVisibility(GONE);
        }
        if(payment.getStatus().equals("DELIVERED")){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDate = payment.getOrderDate().plusMinutes(20).format(formatter);
            payment.setStatus("Đã giao thành công \n" + formattedDate );
            color = Color.parseColor("#66BB6A");
            cancelOrder.setVisibility(GONE);
        }
        if(payment.getStatus().equals("CANCELED")){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDate = payment.getOrderDate().format(formatter);
            payment.setStatus("Đơn hàng đã bị hủy \n" + formattedDate );
            color = Color.parseColor("#CD5C5C");
            cancelOrder.setVisibility(GONE);
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

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showErrorDialogAndFinish();
            }
        });


    }
    private void updateOrder(int id){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = UrlUtil.ADDRESS + "payment/update?id=" + id;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    if(response.equals("Cancel successfully")){
                        Toast.makeText(OrderDetailActivity.this,"Đã hủy đơn hàng",Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(OrderDetailActivity.this,"Error :" + error,Toast.LENGTH_SHORT).show();
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    private void showErrorDialogAndFinish() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_delete_order);
        View overlayView = findViewById(R.id.overlayView);
        if (overlayView != null) {
            overlayView.setVisibility(VISIBLE); // Hiển thị nền mờ
        }
        TextView errorClose = dialog.findViewById(R.id.cancelBtn);
        TextView okBtn = dialog.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            if(overlayView != null){
                overlayView.setVisibility(GONE);
            }
            updateOrder(payment.getId());
            Intent returnIntent = new Intent();
            returnIntent.putExtra("canceled", true);
            setResult(RESULT_OK, returnIntent);
            finish();
        });
        errorClose.setOnClickListener(v -> {
            dialog.dismiss();
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0)); // Nền trong suốt
        }
        if (overlayView != null) {
            overlayView.setVisibility(VISIBLE); // Hiển thị nền mờ
        }
        dialog.show();
    }
}