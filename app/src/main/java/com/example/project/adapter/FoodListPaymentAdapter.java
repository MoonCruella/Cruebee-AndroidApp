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

import com.example.project.R;
import com.example.project.helpers.ManagementCart;
import com.example.project.interfaces.ChangeNumberItemsListener;
import com.example.project.model.Food;
import com.example.project.model.PaymentProduct;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodListPaymentAdapter extends RecyclerView.Adapter<FoodListPaymentAdapter.FoodListPaymentViewHolder> {

    private ArrayList<Food> foods;
    private ManagementCart managementCart;
    private ChangeNumberItemsListener changeNumberItemsListener;

    public FoodListPaymentAdapter(ArrayList<Food> foods, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.foods = foods;
        this.managementCart = new ManagementCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public FoodListPaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_payment,parent,false);
        return new FoodListPaymentViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodListPaymentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Food food = foods.get(position);
        holder.title.setText(food.getName());
        holder.des.setText(food.getDescription());
        holder.num.setText(String.valueOf(food.getNumberInCart()));
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(Math.round(food.getNumberInCart() * food.getPrice()) * 100/100) + " đ";
        holder.price.setText(formattedPrice);

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

    public class FoodListPaymentViewHolder extends RecyclerView.ViewHolder{

        TextView title,price,num,des;
        ImageView pic,plusItem,minusItem;
        ImageView deleteItem,editItem;
        public FoodListPaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            price = itemView.findViewById(R.id.priceTxt);
            des = itemView.findViewById(R.id.descriptionTxt);
            num = itemView.findViewById(R.id.numTxt);
            plusItem = itemView.findViewById(R.id.plusImgView);
            minusItem = itemView.findViewById(R.id.minusImgView);
            deleteItem = itemView.findViewById(R.id.deleteBtn);
            editItem = itemView.findViewById(R.id.editBtn);
        }
    }
    public List<PaymentProduct> getFoodList() {
        List<PaymentProduct> productList = new ArrayList<>();
        for (Food food : foods) {
            int quantity = food.getNumberInCart();
            if (quantity > 0) {
                productList.add(new PaymentProduct(food, quantity));
            }
        }
        return productList;
    }
}
