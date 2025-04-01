package com.example.project.helpers;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.project.interfaces.CartResponse;
import com.example.project.interfaces.ChangeNumberItemsListener;
import com.example.project.interfaces.TotalFeeResponse;
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

    public void insertFood(Food food) throws JSONException {
        getListCart(new CartResponse() {
            @Override
            public void onSuccess(ArrayList<Food> listFood) {
                boolean existAlready = false;
                int existedCount = 0;
                int n = 0;

                // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
                for (int i = 0; i < listFood.size(); i++) {
                    if (listFood.get(i).getName().equals(food.getName())) {
                        existAlready = true;
                        existedCount = listFood.get(i).getNumberInCart();
                        n = i;
                        break;
                    }
                }

                // Nếu đã có sản phẩm trong giỏ, tăng số lượng
                if (existAlready) {
                    listFood.get(n).setNumberInCart(food.getNumberInCart() + existedCount);
                } else {
                    listFood.add(food); // Thêm sản phẩm mới vào giỏ
                }

                // Nếu người dùng đã đăng nhập, gọi API để cập nhật giỏ hàng trên server
                if (is_logged_in) {
                    updateCartOnServer(food,food.getNumberInCart());
                    // Hiển thị thông báo cho người dùng
                    Toast.makeText(context,"UserID : " + userId,Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Đã thêm vào giỏ API", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu chưa đăng nhập, lưu vào TinyDB
                    tinyDB.putListObject("CartList", listFood);
                    // Hiển thị thông báo cho người dùng
                    Toast.makeText(context, "Đã thêm vào giỏ ", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Lỗi khi lấy giỏ hàng: " + errorMessage);
            }
        });
    }

    // Hàm gọi API để cập nhật giỏ hàng khi người dùng đã đăng nhập
    private void updateCartOnServer(Food food,int count) {
        String url = UrlUtil.ADDRESS + "cart/add"; // Địa chỉ API

        // Tạo JSONObject để gửi lên server
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId); // ID người dùng
            jsonBody.put("productId", food.getId()); // ID sản phẩm
            jsonBody.put("quantity", count); // Số lượng
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Tạo JsonObjectRequest để gửi dữ liệu lên server
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    Log.d(TAG, "API Response: " + response.toString());
                },
                error -> {
                    Log.e(TAG, "API Error: " + error.toString());
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token); // Thêm token nếu cần
                return headers;
            }
        };

        // Thêm request vào hàng đợi của Volley
        requestQueue.add(jsonObjectRequest);
    }


    public void getListCart(CartResponse callback) throws JSONException {
        if (is_logged_in) {
            String url = UrlUtil.ADDRESS + "cart/" + tinyDB.getInt("userId");
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, url, null,
                    response -> {
                        ArrayList<Food> foods = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject cartItem = response.getJSONObject(i);
                                int quantity = cartItem.getInt("quantity");
                                JSONObject product = cartItem.getJSONObject("product");

                                foods.add(new Food(
                                        product.getInt("id"),
                                        product.getString("name"),
                                        product.getInt("price"),
                                        UrlUtil.ADDRESS + "download/" + product.getString("attachmentId"),
                                        product.getString("description"),
                                        quantity
                                ));
                                Log.d(TAG,"San pham: " + foods.get(0).getName());
                            }

                            callback.onSuccess(foods);
                        } catch (JSONException e) {
                            callback.onError("Lỗi khi phân tích dữ liệu JSON");
                        }
                    },
                    error -> callback.onError("Lỗi khi lấy giỏ hàng!")
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            requestQueue.add(jsonArrayRequest);
        } else {
            callback.onSuccess(tinyDB.getListObject("CartList"));
        }
    }


    public void plusNumberFood(ArrayList<Food> listFood, int position, ChangeNumberItemsListener changeNumberItemsListener) throws JSONException {

        if(is_logged_in){
            Food food = listFood.get(position);
            updateCartOnServer(food,1);
            food.setNumberInCart(food.getNumberInCart() + 1);
        }
        else {
            listFood.get(position).setNumberInCart(listFood.get(position).getNumberInCart() + 1);
            tinyDB.putListObject("CartList", listFood);
        }
        changeNumberItemsListener.change();
    }

    public void minusNumberFood(ArrayList<Food> listFood, int position, ChangeNumberItemsListener changeNumberItemsListener) throws JSONException {

        Food food = listFood.get(position);
        if (listFood.get(position).getNumberInCart() == 1) {

            // Kiem tra neu nguoi dung da login => Xoa sp trong Database Cart
            if(is_logged_in){
                deleteFromCart(food);
            }
            listFood.remove(position);
        }
        else {
            if(is_logged_in){
                updateCartOnServer(food,- 1);
                food.setNumberInCart(food.getNumberInCart() - 1);
            }
            else{
                food.setNumberInCart(food.getNumberInCart() - 1);
                tinyDB.putListObject("CartList", listFood);
            }

        }
        changeNumberItemsListener.change();

    }
    public void deleteFood(ArrayList<Food> listFood, int position, ChangeNumberItemsListener changeNumberItemsListener) throws JSONException {
        Food food = listFood.get(position);
        if(is_logged_in){
            deleteFromCart(food);
        }
        listFood.remove(position);
        tinyDB.putListObject("CartList", listFood);
        Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
        changeNumberItemsListener.change();

    }

    public void getTotalFee(TotalFeeResponse callback) throws JSONException {
        getListCart(new CartResponse() {
            @Override
            public void onSuccess(ArrayList<Food> foods) throws JSONException {
                int total = 0;
                if (foods != null && !foods.isEmpty()) {
                    for (Food food : foods) {
                        total += food.getNumberInCart() * food.getPrice();
                    }
                }
                callback.onSuccess(total);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Lỗi khi lấy giỏ hàng: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }

    // Hàm gọi API để cập nhật giỏ hàng khi người dùng đã đăng nhập
    private void deleteFromCart(Food food) {
        String url = UrlUtil.ADDRESS + "cart/delete"; // Địa chỉ API

        // Tạo JSONObject để gửi lên server
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId); // ID người dùng
            jsonBody.put("productId", food.getId()); // ID sản phẩm
            jsonBody.put("quantity", food.getNumberInCart()); // Số lượng
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Tạo JsonObjectRequest để gửi dữ liệu lên server
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    Log.d(TAG, "API Response: " + response.toString());
                },
                error -> {
                    Log.e(TAG, "API Error: " + error.toString());
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token); // Thêm token nếu cần
                return headers;
            }
        };

        // Thêm request vào hàng đợi của Volley
        requestQueue.add(jsonObjectRequest);
    }

}


