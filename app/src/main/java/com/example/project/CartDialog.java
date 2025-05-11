package com.example.project;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.adapter.CartListAdapter;
import com.example.project.helpers.ManagementCart;
import com.example.project.helpers.TinyDB;
import com.example.project.interfaces.CartResponse;
import com.example.project.interfaces.OnFragmentSwitchListener;
import com.example.project.interfaces.TotalFeeResponse;
import com.example.project.model.Food;
import com.example.project.volley.VolleyHelper;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartDialog extends Dialog {

    private ImageView closeBtn;
    private RecyclerView recyclerView;
    private ManagementCart managementCart;

    private CartListAdapter adapter;
    private TextView giaTxt, themMonBtn, thanhToanBtn, emptyTxt;
    private ArrayList<Food> cartList_temp;
    private OnFragmentSwitchListener listener;
    private TinyDB tinyDB;
    private boolean hasShopAddress;

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
        closeBtn.setOnClickListener(v -> dismiss());
    }

    private void initView() {
        closeBtn = findViewById(R.id.closeIview);
        recyclerView = findViewById(R.id.recyclerview);
        giaTxt = findViewById(R.id.giaTxt);
        themMonBtn = findViewById(R.id.themMonBtn);
        thanhToanBtn = findViewById(R.id.thanhToanBtn);
        emptyTxt = findViewById(R.id.emptyTxt);
        cartList_temp = new ArrayList<>();
        adapter = new CartListAdapter(cartList_temp, getContext(), CartDialog.this::updateTotalPrice);
        recyclerView.setAdapter(adapter);
        tinyDB = new TinyDB(getContext());
        hasShopAddress = tinyDB.getAll().containsKey("addressShop");
        VolleyHelper.getInstance(getContext()).setDialogCloseHandler(() -> {
            if (CartDialog.this.isShowing()) {
                CartDialog.this.dismiss(); // Đóng dialog nếu đang mở
            }
        });

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
                if(!hasShopAddress){
                    if (listener != null) {
                        listener.onSwitchToFragment("HOME"); // Gọi phương thức để chuyển Fragment
                    }
                    dismiss();
                }
                else if(cartList_temp.isEmpty()){
                    showDialog();
                }
                else {
                    dismiss();
                    Intent intent = new Intent(getContext(),PaymentActivity.class);
                    getContext().startActivity(intent);
                }

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
                cartList_temp = cartList;
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
        Context context = getContext();

        // Nếu là activity thì kiểm tra thêm
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing()) {
                return;
            }
        }

        try {
            super.show();              // Hiển thị dialog
            initList();                // Load lại giỏ hàng
            updateTotalPrice();        // Cập nhật giá
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Log.e(TAG, "Lỗi khi hiển thị dialog: " + ex.getMessage());
        }
    }


    private void showDialog() {
        Context context = getContext();
        if (!(context instanceof Activity)) return;

        Activity activity = (Activity) context;
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        ConstraintLayout errorConstrlayout = findViewById(R.id.successConstraintLayout);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_empty_cart, errorConstrlayout);
        TextView okBtn = view.findViewById(R.id.okBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        okBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (listener != null) {
                listener.onSwitchToFragment("MENU");
            }
            dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (isShowing()) {
            dismiss(); // Tránh rò rỉ
        }
    }
}
