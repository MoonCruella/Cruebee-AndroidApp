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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.ShowDetailActivity;
import com.example.project.model.Food;

import java.text.DecimalFormat;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodHoder> {

    private Context context;
    private List<Food> foodList;
    public FoodAdapter(Context context,List<Food> foods){
        this.context = context;
        this.foodList = foods;
    }
    @NonNull
    @Override
    public FoodHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodHoder holder, int position) {

        Food food = foodList.get(position);
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(food.getPrice()) + " đ";
        holder.name.setText(food.getName());
        holder.price.setText(formattedPrice);
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

    public class FoodHoder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name,price;
        CardView constraintLayout;
        public FoodHoder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            constraintLayout = itemView.findViewById(R.id.main_layout);
        }

    }

}


