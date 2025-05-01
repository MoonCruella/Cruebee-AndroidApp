package com.example.project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.model.Food;
import com.example.project.model.PaymentProduct;
import java.text.DecimalFormat;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_order_detail, parent, false);
        return new FoodListDetailAdapter.FoodListDetailHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FoodListDetailAdapter.FoodListDetailHolder holder, int position) {
        PaymentProduct payment = paymentList.get(position);
        Food food = payment.getProduct();
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(food.getPrice()) + " đ";
        holder.titleTxt.setText(food.getName());
        holder.priceTxt.setText(formattedPrice);
        holder.descriptionTxt.setText(food.getDescription());
        Glide.with(context).load(food.getImage()).into(holder.imageView);
        holder.numTxt.setText("x" + String.valueOf(payment.getQuantity()));
        String formattedPrice2 = decimalFormat.format((long) food.getPrice() * payment.getQuantity()) + " đ";
        holder.totalPrice.setText(formattedPrice2);
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public class FoodListDetailHolder extends RecyclerView.ViewHolder{

        TextView titleTxt, descriptionTxt,priceTxt,numTxt,totalPrice;
        ImageView imageView;
        public FoodListDetailHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            descriptionTxt = itemView.findViewById(R.id.descriptionTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            numTxt = itemView.findViewById(R.id.numTxt);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            imageView = itemView.findViewById(R.id.imageview);
        }
    }
}
