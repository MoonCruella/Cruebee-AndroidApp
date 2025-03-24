package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.adapter.FoodListPaymentAdapter;
import com.example.project.helpers.ManagementCart;
import com.example.project.interfaces.ChangeNumberItemsListener;

import java.text.DecimalFormat;

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
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(managementCart.getTotalFee()) + " đ";
        giaTxt.setText(formattedPrice);
        giaDKTxt.setText(formattedPrice);
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
        adapter = new FoodListPaymentAdapter(managementCart.getListCart(),this, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedPrice = decimalFormat.format(managementCart.getTotalFee()) + " đ";
                giaTxt.setText(formattedPrice);
                giaDKTxt.setText(formattedPrice);
            }
        });
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);
    }
}