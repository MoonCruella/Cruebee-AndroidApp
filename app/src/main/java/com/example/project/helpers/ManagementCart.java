package com.example.project.helpers;

import static android.content.ContentValues.TAG;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.example.project.interfaces.CartResponse;
import com.example.project.interfaces.ChangeNumberItemsListener;
import com.example.project.interfaces.InsertCartCallback;
import com.example.project.interfaces.TotalFeeResponse;
import com.example.project.interfaces.UpdateCartCallback;
import com.example.project.model.Food;
import com.example.project.model.User;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleyHelper;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;
    private Boolean is_logged_in;
    private int userId;
    String token;
    User user;


    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
        boolean hasUser = tinyDB.getAll().containsKey("savedUser");
        if(hasUser){
            user = tinyDB.getObject("savedUser",User.class);
            this.userId = user.getId();
            this.token = tinyDB.getString("token");
        }
        this.is_logged_in = tinyDB.getBoolean("is_logged_in");
    }

    public void insertFood(Food food, InsertCartCallback callback) throws JSONException {
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
                    updateCartOnServer(food, food.getNumberInCart(), new UpdateCartCallback() {
                        @Override
                        public void onSuccess() {
                            callback.onInserted();
                            Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(context, "Lỗi khi cập nhật giỏ hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    // Nếu chưa đăng nhập, lưu vào TinyDB
                    tinyDB.putListObject("CartList", listFood);
                    Toast.makeText(context, "Đã thêm vào giỏ ", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Lỗi khi lấy giỏ hàng: " + errorMessage);
            }
        });
    }

    private void updateCartOnServer(Food food, int count, UpdateCartCallback callback) {
        String url = UrlUtil.ADDRESS + "cart/add";

        // Tạo JSON body
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId); // Đảm bảo userId đã được khai báo và hợp lệ
            jsonBody.put("productId", food.getId());
            jsonBody.put("quantity", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Gửi yêu cầu đến server
        VolleyHelper volleyHelper = VolleyHelper.getInstance(context);
        volleyHelper.sendStringRequestWithAuth(
                Request.Method.POST,
                url,
                jsonBody.toString(),  // Chuyển đổi JSONObject thành String
                true, // Yêu cầu token
                response -> {
                    Log.d(TAG, "API Response: " + response);
                    callback.onSuccess();  // Gọi callback khi thành công
                },
                error -> {
                    String message = "API Error: " + error.toString();
                    Log.e(TAG, message);
                    callback.onError(message);  // Gọi callback khi gặp lỗi
                }
        );
    }


    public void getListCart(CartResponse callback) throws JSONException {
        if (is_logged_in) {
            String url = UrlUtil.ADDRESS + "cart/" + userId;

            VolleyHelper volleyHelper = VolleyHelper.getInstance(context);
            volleyHelper.sendJsonArrayRequestWithAuth(url, true,
                    response -> {

                        // Xử lý dữ liệu trả về (JSONArray)
                        Log.d("VolleyResponse", "Response: " + response.toString());
                        ArrayList<Food> foods = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject cartItem = response.getJSONObject(i);
                                int quantity = cartItem.getInt("quantity");
                                JSONObject product = cartItem.getJSONObject("product");
                                String des = product.isNull("description") ? "" : product.getString("description");
                                foods.add(new Food(
                                        product.getInt("id"),
                                        product.getString("name"),
                                        product.getInt("price"),
                                        UrlUtil.ADDRESS + "download/" + product.getString("attachmentId"),
                                        des,
                                        quantity
                                ));
                                Log.d(TAG,"San pham: " + foods.get(0).getName());
                            }

                            callback.onSuccess(foods);
                        } catch (JSONException e) {
                            callback.onError("Lỗi khi phân tích dữ liệu JSON");
                        }

                    },
                    error -> {
                        // Xử lý khi có lỗi
                        Log.e("VolleyError", "Error: " + error.getMessage());
                    });
        } else {
            callback.onSuccess(tinyDB.getListObject("CartList"));
        }
    }


    public void plusNumberFood(ArrayList<Food> listFood, int position, ChangeNumberItemsListener changeNumberItemsListener) throws JSONException {

        Food food = listFood.get(position);
        food.setNumberInCart(food.getNumberInCart() + 1);
        if (is_logged_in) {
            updateCartOnServer(food, 1, new UpdateCartCallback() {
                @Override
                public void onSuccess() {
                    try {
                        changeNumberItemsListener.change(); // ✅ chỉ gọi khi server OK
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Lỗi khi update: " + error);
                }
            });
        } else {
            tinyDB.putListObject("CartList", listFood);
            changeNumberItemsListener.change(); // local thì không cần đợi
        }
    }

    public void minusNumberFood(ArrayList<Food> listFood, int position, ChangeNumberItemsListener changeNumberItemsListener) throws JSONException {

        Food food = listFood.get(position);
        if (listFood.get(position).getNumberInCart() == 1) {

            // Kiem tra neu nguoi dung da login => Xoa sp trong Database Cart
            if(is_logged_in){
                deleteFromCart(food, new UpdateCartCallback(){
                    @Override
                    public void onSuccess() {
                        try {
                            changeNumberItemsListener.change(); // ✅ chỉ gọi khi server OK
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Lỗi khi update: " + error);
                    }
                });
                listFood.remove(position);
            }
            else {
                listFood.remove(position);
                tinyDB.putListObject("CartList", listFood);
            }

        }
        else {
            if(is_logged_in){
                food.setNumberInCart(food.getNumberInCart() - 1);
                updateCartOnServer(food, - 1, new UpdateCartCallback() {
                    @Override
                    public void onSuccess() {
                        try {
                            changeNumberItemsListener.change(); // ✅ chỉ gọi khi server OK
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Lỗi khi update: " + error);
                    }
                });
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
            deleteFromCart(food, new UpdateCartCallback() {
                @Override
                public void onSuccess() {
                    try {
                        changeNumberItemsListener.change(); // ✅ chỉ gọi khi server OK
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public void onError(String error) {

                }
            });
        }
        listFood.remove(position);
        tinyDB.putListObject("CartList", listFood);
        Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
        changeNumberItemsListener.change();

    }
    public void clearCart(ArrayList<Food> listFood,ChangeNumberItemsListener changeNumberItemsListener) throws JSONException {
        if(is_logged_in){
            clearAllFood(new UpdateCartCallback() {
                @Override
                public void onSuccess() {
                    try {
                        changeNumberItemsListener.change(); // ✅ chỉ gọi khi server OK
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public void onError(String error) {

                }
            });
        }
        else{
            listFood.clear();
            tinyDB.putListObject("CartList", listFood);
        }
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
    private void deleteFromCart(Food food, UpdateCartCallback callback) {
        String url = UrlUtil.ADDRESS + "cart/delete";

        // Tạo JSONObject để làm body
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId); // Đảm bảo userId hợp lệ
            jsonBody.put("productId", food.getId());
            jsonBody.put("quantity", food.getNumberInCart());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Gửi yêu cầu đến server
        VolleyHelper volleyHelper = VolleyHelper.getInstance(context);
        volleyHelper.sendStringRequestWithAuth(
                Request.Method.POST,
                url,
                jsonBody.toString(),  // Chuyển đổi JSONObject thành String
                true, // Yêu cầu token
                response -> {
                    Log.d(TAG, "Delete Response: " + response);
                    callback.onSuccess();  // Gọi callback khi thành công
                },
                error -> {
                    Log.e(TAG, "Delete Error: " + error.toString());
                    callback.onError(error.getMessage());  // Gọi callback khi gặp lỗi
                }
        );
    }


    // Hàm gọi API để cập nhật giỏ hàng khi người dùng mua hàng thành cong
    private void clearAllFood(UpdateCartCallback callback) {
        String url = UrlUtil.ADDRESS + "cart/clear?userId=" + userId;

        // Tạo đối tượng VolleyHelper
        VolleyHelper volleyHelper = VolleyHelper.getInstance(context);

        // Sử dụng sendStringRequestWithAuth để gửi yêu cầu với token
        volleyHelper.sendStringRequestWithAuth(
                Request.Method.POST,
                url,
                null,  // Không có body trong yêu cầu này
                true, // Cần xác thực bằng token
                response -> {
                    Log.d(TAG, "Clear Cart Response: " + response);
                    callback.onSuccess();  // Gọi callback thành công
                },
                error -> {
                    Log.e(TAG, "Clear Cart Error: " + error.toString());
                    callback.onError(error.getMessage());  // Gọi callback khi có lỗi
                }
        );
    }



}


