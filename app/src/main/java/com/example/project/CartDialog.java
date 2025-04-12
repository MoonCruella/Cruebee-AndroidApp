package com.example.project;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.example.project.interfaces.CartResponse;
import com.example.project.interfaces.OnFragmentSwitchListener;
import com.example.project.interfaces.TotalFeeResponse;
import com.example.project.model.Food;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartDialog extends Dialog {

    private ImageView closeBtn;
    private RecyclerView recyclerView;
    private ManagementCart managementCart;

    private CartListAdapter adapter;
    private TextView giaTxt, themMonBtn, thanhToanBtn, emptyTxt;
    private ArrayList<Food> cartList;
    private OnFragmentSwitchListener listener;

    public CartDialog(@NonNull Context context, OnFragmentSwitchListener listener) {
        super(context);
        this.listener = listener;
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
        try {
            initList();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            updateTotalPrice();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        closeBtn.setOnClickListener(v -> dismiss());
    }

    private void initView() {
        closeBtn = findViewById(R.id.closeIview);
        recyclerView = findViewById(R.id.recyclerview);
        giaTxt = findViewById(R.id.giaTxt);
        themMonBtn = findViewById(R.id.themMonBtn);
        thanhToanBtn = findViewById(R.id.thanhToanBtn);
        emptyTxt = findViewById(R.id.emptyTxt);
        cartList = new ArrayList<>();
        adapter = new CartListAdapter(cartList, getContext(), CartDialog.this::updateTotalPrice);
        recyclerView.setAdapter(adapter);

        // Chuyển sang MenuFragment khi bấm nút
        themMonBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSwitchToFragment("MENU"); // Gọi phương thức để chuyển Fragment
            }
            dismiss();
        });
        thanhToanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(getContext(),PaymentActivity.class);
                getContext().startActivity(intent);
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

    private void initList() throws JSONException {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        managementCart.getListCart(new CartResponse() {
            @Override
            public void onSuccess(ArrayList<Food> cartList) {
                if (cartList == null || cartList.isEmpty()) {
                    emptyTxt.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyTxt.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new CartListAdapter(cartList, getContext(), CartDialog.this::updateTotalPrice);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Lỗi khi lấy giỏ hàng: " + errorMessage);
                emptyTxt.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }


    private void updateTotalPrice() throws JSONException {
        managementCart.getTotalFee(new TotalFeeResponse() {
            @Override
            public void onSuccess(int totalFee) {
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedPrice = decimalFormat.format(totalFee) + " đ";
                giaTxt.setText(formattedPrice);
            }
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Lỗi khi tính tổng tiền: " + errorMessage);
            }
        });

    }
    @Override
    public void show() {
        super.show();
        try {
            initList();          // Load lại danh sách giỏ hàng
            updateTotalPrice();  // Cập nhật lại tổng giá tiền
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
