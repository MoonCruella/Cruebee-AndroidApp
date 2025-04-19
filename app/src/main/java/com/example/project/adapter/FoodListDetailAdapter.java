package com.example.project.adapter;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.AddressDetailsActivity;
import com.example.project.OrderDetailActivity;
import com.example.project.R;
import com.example.project.model.Address;
import com.example.project.model.AddressShop;
import com.example.project.model.Food;
import com.example.project.model.Payment;
import com.example.project.model.PaymentProduct;

import java.util.List;

public class FoodListDetailAdapter extends RecyclerView.Adapter<FoodListDetailAdapter.FoodListDetailHolder>{
    private Context context;
    private List<PaymentProduct> paymentList;

    public FoodListDetailAdapter(Context context, List<PaymentProduct> paymentList) {
        this.context = context;
        this.paymentList = paymentList;
    }

    @NonNull
    @Override
    public FoodListDetailAdapter.FoodListDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_orderlist, parent, false);
        return new FoodListDetailAdapter.FoodListDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodListDetailAdapter.FoodListDetailHolder holder, int position) {
        PaymentProduct payment = paymentList.get(position);
        Food food = payment.getProduct();
        Log.d("food: ",food.toString());
        holder.shopTxt.setText(food.getName());
        holder.detailTxt.setText("Số lượng: " + payment.getQuantity());
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public class FoodListDetailHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraintLayout;
        TextView shopTxt, detailTxt;
        public FoodListDetailHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.main_layout);
            shopTxt = itemView.findViewById(R.id.shopTxt);
            detailTxt = itemView.findViewById(R.id.detailTxt);
        }
    }
}
