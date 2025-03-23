package com.example.project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.adapter.CartListAdapter;
import com.example.project.helpers.ManagementCart;
import com.example.project.interfaces.ChangeNumberItemsListener;

import java.text.DecimalFormat;

public class CartListActivity extends Dialog {

    private ImageView closeBtn;
    private RecyclerView recyclerView;
    private ManagementCart managementCart;

    private CartListAdapter adapter;
    private TextView giaTxt,themMonBtn,thanhToanBtn,emptyTxt;
    public CartListActivity(@NonNull Context context){
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);

        managementCart = new ManagementCart(getContext());

        initView();
        initList();
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(managementCart.getTotalFee()) + " đ";
        giaTxt.setText(formattedPrice);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    private void initView(){
        closeBtn = (ImageView) findViewById(R.id.closeIview);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        giaTxt = (TextView) findViewById(R.id.giaTxt);
        themMonBtn = (TextView) findViewById(R.id.themMonBtn);
        thanhToanBtn = (TextView) findViewById(R.id.thanhToanBtn);
        emptyTxt = (TextView) findViewById(R.id.emptyTxt);

        themMonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(getContext(),MenuActivity.class);
                getContext().startActivity(intent);
            }
        });
        thanhToanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(getContext(),Payment.class);
                getContext().startActivity(intent);
            }
        });
    }

    private void initList(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CartListAdapter(managementCart.getListCart(),getContext(), new ChangeNumberItemsListener() {
            @Override
            public void change() {
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedPrice = decimalFormat.format(managementCart.getTotalFee()) + " đ";
                giaTxt.setText(formattedPrice);
            }
        });
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);


        if(managementCart.getListCart().isEmpty()){
            emptyTxt.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else{
            emptyTxt.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}