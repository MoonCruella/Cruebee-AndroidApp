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
import com.example.project.helpers.ManagementCart;
import com.example.project.interfaces.ChangeNumberItemsListener;
import com.example.project.model.Food;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartListViewHolder> {

    private ArrayList<Food> foods;
    private ManagementCart managementCart;
    private ChangeNumberItemsListener changeNumberItemsListener;

    public CartListAdapter(ArrayList<Food> foods, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.foods = foods;
        this.managementCart = new ManagementCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public CartListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_in_cart,parent,false);
        return new CartListViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Food food = foods.get(position);
        holder.title.setText(food.getName());
        holder.des.setText(food.getDescription());
        holder.num.setText(String.valueOf(food.getNumberInCart()));
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(Math.round(food.getNumberInCart() * food.getPrice()) * 100/100) + " đ";
        holder.price.setText(formattedPrice);
        Glide.with(holder.itemView.getContext()).load(food.getImage()).into(holder.pic);

        holder.plusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    managementCart.plusNumberFood(foods, position, new ChangeNumberItemsListener() {
                        @Override
                        public void change() throws JSONException {
                            notifyDataSetChanged();
                            changeNumberItemsListener.change();

                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        holder.minusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    managementCart.minusNumberFood(foods, position, new ChangeNumberItemsListener() {
                        @Override
                        public void change() throws JSONException {
                            notifyDataSetChanged();
                            changeNumberItemsListener.change();
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    managementCart.deleteFood(foods, position, new ChangeNumberItemsListener() {
                        @Override
                        public void change() throws JSONException {
                            notifyDataSetChanged();
                            changeNumberItemsListener.change();
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public static class CartListViewHolder extends RecyclerView.ViewHolder{

        TextView title,price,num,des;
        ImageView pic,plusItem,minusItem;
        ImageView deleteItem,editItem;
        public CartListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            price = itemView.findViewById(R.id.priceTxt);
            des = itemView.findViewById(R.id.descriptionTxt);
            num = itemView.findViewById(R.id.numTxt);
            pic = itemView.findViewById(R.id.imageView);
            plusItem = itemView.findViewById(R.id.plusImgView);
            minusItem = itemView.findViewById(R.id.minusImgView);
            deleteItem = itemView.findViewById(R.id.deleteBtn);
            editItem = itemView.findViewById(R.id.editBtn);

        }
    }
}
