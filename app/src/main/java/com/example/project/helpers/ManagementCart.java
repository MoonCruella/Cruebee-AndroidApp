package com.example.project.helpers;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.project.interfaces.ChangeNumberItemsListener;
import com.example.project.model.Food;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleySingleton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;
    private Boolean is_logged_in;
    private int userId;
    private RequestQueue requestQueue;
    String token;

    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
        this.is_logged_in = tinyDB.getBoolean("is_logged_in");
        requestQueue = VolleySingleton.getmInstance(context).getRequestQueue();
        this.userId = tinyDB.getInt("userId");
        this.token = tinyDB.getString("token");
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

        // Neu user da log in => Truy xuat san pham tu database
        if(is_logged_in){
            ArrayList<Food> foods = new ArrayList<>();
            String url = UrlUtil.ADDRESS + "cart/" + tinyDB.getInt("userId");
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, "Response: " + response.toString());
                            Toast.makeText(context, "Lấy giỏ hàng thành công!", Toast.LENGTH_SHORT).show();

                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject cartItem = response.getJSONObject(i);
                                    int cart_id = cartItem.getInt("id");
                                    int quantity = cartItem.getInt("quantity");

                                    JSONObject product = cartItem.getJSONObject("product");
                                    int id = product.getInt("id");
                                    String name = product.getString("name");
                                    int price = product.getInt("price");
                                    String imageId = product.getString("attachmentId");
                                    String image = UrlUtil.ADDRESS + "download/" + imageId;
                                    String des = product.getString("description");

                                    Food food = new Food(id, name, price, image, des, quantity);
                                    foods.add(food);
                                    Log.d(TAG, "Sản phẩm: " + name + ", Số lượng: " + quantity + ", Giá: " + price);
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Lỗi khi parse JSON: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Lỗi API: " + error.toString());
                            Toast.makeText(context, "Lỗi khi lấy giỏ hàng!", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token); // Gửi token trong Header
                    return headers;
                }
            };

            requestQueue.add(jsonArrayRequest);

            return foods;
        }
        else{
            return tinyDB.getListObject("CartList");
        }
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


