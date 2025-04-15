package com.example.project.adapter;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
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
import com.example.project.model.Payment;

import java.util.List;

public class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.PaymentListHolder>{
    private Context context;
    private List<Payment> paymentList;

    public PaymentListAdapter(Context context, List<Payment> paymentList) {
        this.context = context;
        this.paymentList = paymentList;
    }

    @NonNull
    @Override
    public PaymentListAdapter.PaymentListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_orderlist, parent, false);
        return new PaymentListAdapter.PaymentListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentListAdapter.PaymentListHolder holder, int position) {
        Payment payment = paymentList.get(position);
        holder.shopTxt.setText(payment.getShopid());
        holder.detailTxt.setText(payment.getProducts().size() + " phần - " + payment.getTotalPrice());
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("object",payment);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public class PaymentListHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraintLayout;
        TextView shopTxt, detailTxt;
        public PaymentListHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.main_layout);
            shopTxt = itemView.findViewById(R.id.shopTxt);
            detailTxt = itemView.findViewById(R.id.detailTxt);
        }
    }
}
