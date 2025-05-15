package com.example.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.activity.ShowDetailActivity;
import com.example.project.model.Food;

import java.text.DecimalFormat;
import java.util.List;

public class FoodTopTenAdapter extends RecyclerView.Adapter<FoodTopTenAdapter.FoodToptenHoder> {
    private Context context;
    private List<Food> foodList;
    public FoodTopTenAdapter(Context context, List<Food> foods) {
        this.context = context;
        this.foodList = foods;
    }
    @NonNull
    @Override
    public FoodToptenHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_top_ten, parent, false);
        return new FoodToptenHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodToptenHoder holder, int position) {

        Food food = foodList.get(position);
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(food.getPrice()) + " đ";
        holder.name.setText(food.getName());
        holder.price.setText(formattedPrice);
        holder.soldCount.setText(String.valueOf(food.getSoldCount()));
        Glide.with(context).load(food.getImage()).into(holder.imageView);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowDetailActivity.class);
                intent.putExtra("object",food);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class FoodToptenHoder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name,price,soldCount;
        CardView constraintLayout;
        public FoodToptenHoder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            constraintLayout = itemView.findViewById(R.id.main_layout);
            soldCount = itemView.findViewById(R.id.soldCount);
        }

    }

}


