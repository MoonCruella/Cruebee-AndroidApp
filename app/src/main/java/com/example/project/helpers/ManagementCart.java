package com.example.project.helpers;

import android.content.Context;
import android.widget.Toast;

import com.example.project.interfaces.ChangeNumberItemsListener;
import com.example.project.model.Food;

import java.util.ArrayList;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;

    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public void insertFood(Food food) {
        ArrayList<Food> listFood = getListCart();
        boolean existAlready = false;
        int existedCount = 0;
        int n = 0;
        for (int i = 0; i < listFood.size(); i++) {
            if (listFood.get(i).getName().equals(food.getName())) {
                existAlready = true;
                existedCount = listFood.get(i).getNumberInCart();
                n = i;
                break;
            }
        }
        if (existAlready) {
            listFood.get(n).setNumberInCart(food.getNumberInCart() + existedCount);
        } else {
            listFood.add(food);
        }
        tinyDB.putListObject("CartList", listFood);
        Toast.makeText(context, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<Food> getListCart() {
        return tinyDB.getListObject("CartList");
    }

    public void plusNumberFood(ArrayList<Food> listFood, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listFood.get(position).setNumberInCart(listFood.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList", listFood);
        changeNumberItemsListener.change();
    }

    public void minusNumberFood(ArrayList<Food> listFood, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        if (listFood.get(position).getNumberInCart() == 1) {
            listFood.remove(position);
        } else {
            listFood.get(position).setNumberInCart(listFood.get(position).getNumberInCart() - 1);
        }
        tinyDB.putListObject("CartList", listFood);
        changeNumberItemsListener.change();

    }
    public void deleteFood(ArrayList<Food> listFood, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listFood.remove(position);
        tinyDB.putListObject("CartList", listFood);
        Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
        changeNumberItemsListener.change();
    }

    public int getTotalFee() {
        int total = 0;
        Food food;
        ArrayList<Food> foods = getListCart();
        for(int i = 0; i < foods.size();i++){
            food = foods.get(i);
            total += food.getNumberInCart() * food.getPrice();

        }
        return total;
    }
}


