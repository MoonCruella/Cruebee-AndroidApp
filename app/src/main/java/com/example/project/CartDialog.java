package com.example.project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.adapter.CartListAdapter;
import com.example.project.helpers.ManagementCart;

import java.text.DecimalFormat;

public class CartDialog extends Dialog {

    private ImageView closeBtn;
    private RecyclerView recyclerView;
    private ManagementCart managementCart;

    private CartListAdapter adapter;
    private TextView giaTxt, themMonBtn, thanhToanBtn, emptyTxt;

    public CartDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        if (getWindow() != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = (int) (metrics.widthPixels * 0.9); // 90% chiều rộng màn hình
            getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        managementCart = new ManagementCart(getContext());

        initView();
        initList();
        updateTotalPrice();

        closeBtn.setOnClickListener(v -> dismiss());
    }

    private void initView() {
        closeBtn = findViewById(R.id.closeIview);
        recyclerView = findViewById(R.id.recyclerview);
        giaTxt = findViewById(R.id.giaTxt);
        themMonBtn = findViewById(R.id.themMonBtn);
        thanhToanBtn = findViewById(R.id.thanhToanBtn);
        emptyTxt = findViewById(R.id.emptyTxt);

        themMonBtn.setOnClickListener(v -> {
            Context context = getContext();
            if (context != null) {
                Intent intent = new Intent(context, MenuActivity.class);
                context.startActivity(intent);
            }
        });
        thanhToanBtn.setOnClickListener(v -> {
            Context context = getContext();
            if (context != null) {
                Intent intent = new Intent(context, PaymentActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CartListAdapter(managementCart.getListCart(), getContext(), this::updateTotalPrice);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        if (managementCart.getListCart().isEmpty()) {
            emptyTxt.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyTxt.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateTotalPrice() {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(managementCart.getTotalFee()) + " đ";
        giaTxt.setText(formattedPrice);
    }
}
