package com.example.project;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.adapter.FoodListPaymentAdapter;
import com.example.project.helpers.ManagementCart;
import com.example.project.interfaces.CartResponse;
import com.example.project.interfaces.ChangeNumberItemsListener;
import com.example.project.interfaces.TotalFeeResponse;
import com.example.project.model.Food;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ManagementCart managementCart;

    private FoodListPaymentAdapter adapter;
    private TextView giaTxt,themMonBtn,giaDKTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        managementCart = new ManagementCart(this);

        initView();
        initList();
        updateTotal();

    }
    private void initView(){
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        giaTxt = (TextView) findViewById(R.id.txtTotalCost);
        themMonBtn = (TextView) findViewById(R.id.btnAddFood);
        giaDKTxt = (TextView) findViewById(R.id.txtCost);
        themMonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, MenuFragment.class);
                startActivity(intent);
            }
        });
    }

    private void initList(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        managementCart.getListCart(new CartResponse() {
            @Override
            public void onSuccess(ArrayList<Food> cartList) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PaymentActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);

                adapter = new FoodListPaymentAdapter(cartList, PaymentActivity.this, new ChangeNumberItemsListener() {
                    @Override
                    public void change() {
                        updateTotal();
                    }
                });
                recyclerView.setAdapter(adapter);
                updateTotal();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(PaymentActivity.this, "Lỗi khi lấy giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);
    }

    public void updateTotal(){
        managementCart.getTotalFee(new TotalFeeResponse() {
            @Override
            public void onSuccess(int totalFee) {
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedPrice = decimalFormat.format(totalFee) + " đ";
                giaTxt.setText(formattedPrice);
                giaDKTxt.setText(formattedPrice);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Lỗi khi tính tổng tiền: " + errorMessage);
            }
        });
    }

}