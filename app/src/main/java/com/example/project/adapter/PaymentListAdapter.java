package com.example.project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.activity.OrderDetailActivity;
import com.example.project.R;
import com.example.project.model.Payment;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.PaymentListHolder>{
    private Context context;
    private ActivityResultLauncher<Intent> launcher;
    private List<Payment> paymentList;

    public PaymentListAdapter(Context context, List<Payment> paymentList, ActivityResultLauncher<Intent> launcher) {
        this.context = context;
        this.paymentList = paymentList;
        this.launcher = launcher;
    }

    @NonNull
    @Override
    public PaymentListAdapter.PaymentListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_orderlist, parent, false);
        return new PaymentListAdapter.PaymentListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentListAdapter.PaymentListHolder holder, @SuppressLint("RecyclerView") int position) {
        Payment payment = paymentList.get(position);
        holder.shopTxt.setText(payment.getShop().getName());
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(payment.getTotalPrice()) + " đ";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDate = payment.getOrderDate().format(formatter);
        holder.timeOrder.setText(formattedDate);
        holder.detailTxt.setText(payment.getProducts().size() + " phần - " + formattedPrice);
        holder.constraintLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("object", payment);
            intent.putExtra("stt", position + 1);
            launcher.launch(intent);
        });
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public class PaymentListHolder extends RecyclerView.ViewHolder{
        CardView constraintLayout;
        TextView shopTxt, detailTxt,timeOrder;
        public PaymentListHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.main_layout);
            shopTxt = itemView.findViewById(R.id.shopTxt);
            detailTxt = itemView.findViewById(R.id.detailTxt);
            timeOrder = itemView.findViewById(R.id.timeOrder);
        }
    }
}
