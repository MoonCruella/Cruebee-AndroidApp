package com.example.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.ShowDetailActivity;
import com.example.project.model.Category;
import com.example.project.model.Food;

import java.text.DecimalFormat;
import java.util.List;

public class RcmFoodAdapter extends RecyclerView.Adapter<RcmFoodAdapter.FoodViewHolder> {
    private List<Food> foodList;
    private Context context;

    private AdapterView.OnItemClickListener listener;


    public RcmFoodAdapter(Context context,List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    //Tạo ViewHolder cho từng item
    @NonNull
    @Override
    public RcmFoodAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcm_food, parent, false);
        return new RcmFoodAdapter.FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RcmFoodAdapter.FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(food.getPrice()) + " đ";
        holder.txtName.setText(food.getName());
        holder.txtPrice.setText(formattedPrice);
        Glide.with(context).load(food.getImage()).into(holder.imgFood);

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowDetailActivity.class);
            intent.putExtra("object",food);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView txtName;
        TextView txtPrice;
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.foodImg);
            txtName = itemView.findViewById(R.id.foodName);
            txtPrice = itemView.findViewById(R.id.foodPrice);
        }
    }
}