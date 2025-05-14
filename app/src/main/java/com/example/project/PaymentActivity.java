package com.example.project;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.adapter.FoodListPaymentAdapter;
import com.example.project.helpers.ManagementCart;
import com.example.project.helpers.StringHelper;
import com.example.project.helpers.TinyDB;
import com.example.project.interfaces.CartResponse;
import com.example.project.interfaces.ChangeNumberItemsListener;
import com.example.project.interfaces.TotalFeeResponse;
import com.example.project.model.Address;
import com.example.project.model.AddressShop;
import com.example.project.model.CreateOrder;
import com.example.project.model.Food;
import com.example.project.model.PaymentProduct;
import com.example.project.model.User;
import com.example.project.utils.UrlUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView btnShop;
    private ManagementCart managementCart;
    private TinyDB tinyDB;
    private FoodListPaymentAdapter adapter;
    private TextView giaTxt,themMonBtn,giaDKTxt,addressTxt,addressShopTxt,btnDatHang,tvError,tvError1;
    private EditText fullName,sdt,note;
    private Switch utensils;
    private CheckBox checkBox;
    private String price;
    private String paymentMethod;
    private ActivityResultLauncher<Intent> paymentMethodLauncher;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ConstraintLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
            return insets;
        });

        managementCart = new ManagementCart(this);
        initView();

        if(tinyDB.getBoolean("is_logged_in")){
            User user = tinyDB.getObject("savedUser", User.class);
            fullName.setText(user.getUsername());
            sdt.setText(user.getSdt());
            paymentMethod = "Tiền mặt";
        }
        else{
            paymentMethod = "ZaloPay";
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        btnDatHang.setOnClickListener(v -> {
            if(sdt.getText().toString().isEmpty()){
                tvError1.setText("Không được để trống");
                tvError1.setVisibility(View.VISIBLE);
            }
            if(fullName.getText().toString().isEmpty()){
                tvError.setText("Không được để trống");
                tvError.setVisibility(View.VISIBLE);
            }
            if(tvError.isShown() || tvError1.isShown()){
                return;
            }else{
                if ("ZaloPay".equals(paymentMethod)) {
                    thanhToanZalopay();
                }
                else{
                    payment();
                }
            }
        });

        paymentMethodLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String updatedMethod = result.getData().getStringExtra("method");
                        if (updatedMethod != null) {
                            paymentMethod = updatedMethod;
                            TextView method = findViewById(R.id.btnPayment);
                            method.setText(paymentMethod);
                        }
                    }
                }
        );
        TextView method = findViewById(R.id.btnPayment);
        method.setText( paymentMethod);
        method.setOnClickListener(view -> {
            Intent intent = new Intent(PaymentActivity.this, PaymentMethodActivity.class);
            intent.putExtra("method", paymentMethod);
            paymentMethodLauncher.launch(intent);
        });
    }
    private void thanhToanZalopay()
    {
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder(price);
            String code = data.getString("return_code");

            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                        Log.d("Zalopay","thanh toan thanh cong");
                        payment();
                    }

                    @Override
                    public void onPaymentCanceled(String zpTransToken, String appTransID) {
                        Log.d("Zalopay","thanh toan da huy");
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                        Log.e("ZaloPay", "Payment Error: " + zaloPayError.toString());
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void payment(){
        String addUser = addressTxt.getText().toString();
        AddressShop addressShop = tinyDB.getObject("addressShop", AddressShop.class);

        String fName = fullName.getText().toString();
        String phone = sdt.getText().toString();
        String noted = note.getText().toString();
        boolean dcu = false;
        if (utensils.isChecked())
        {
            dcu = true;
        }
        List<PaymentProduct> products = adapter.getFoodList();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = UrlUtil.ADDRESS + "payment/order";
        boolean finalDcu = dcu;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if(jsonResponse.has("paymentId"))
                        {
                            int id = jsonResponse.getInt("paymentId");
                            if(!tinyDB.getBoolean("is_logged_in")){

                                // Neu chua cho listpayment, tao moi 1 listpayment
                                if(!tinyDB.getAll().containsKey("paymentList")){
                                    ArrayList<Integer> payments = new ArrayList<>();
                                    tinyDB.putListInt("paymentList",payments);
                                }
                                ArrayList<Integer> payments = tinyDB.getListInt("paymentList");
                                payments.add(id);
                                tinyDB.putListInt("paymentList",payments);
                            }
                            try {
                                managementCart.getListCart(new CartResponse() {
                                    @Override
                                    public void onSuccess(ArrayList<Food> listFood) {
                                        try {
                                            managementCart.clearCart(listFood, new ChangeNumberItemsListener() {
                                                @Override
                                                public void change() throws JSONException {

                                                }
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        Toast.makeText(PaymentActivity.this, "Không thể lấy giỏ hàng: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            showDialog();
                        }
                        else{
                            Toast.makeText(PaymentActivity.this, "Thanh toán that bai", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PaymentActivity.this, "Error processing login response.", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {
                    Toast.makeText(PaymentActivity.this,"Error :" + error,Toast.LENGTH_SHORT).show();
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {

                // Create a JSONObject and put data into it
                JSONObject jsonBody = new JSONObject();
                try {
                    JSONObject userObject = new JSONObject();
                    JSONObject shopObject = new JSONObject();
                    JSONArray productsArray = new JSONArray();
                    for (PaymentProduct item : products) {
                        JSONObject productObject = new JSONObject();
                        JSONObject productIdObject = new JSONObject();

                        productIdObject.put("id", item.getProduct().getId());
                        productIdObject.put("name", item.getProduct().getName());
                        productObject.put("product", productIdObject);
                        productObject.put("quantity", item.getQuantity());

                        productsArray.put(productObject);
                    }
                    managementCart.getTotalFee(new TotalFeeResponse() {
                        @Override
                        public void onSuccess(int totalFee) {
                            price = String.valueOf(totalFee);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Lỗi khi tính tổng tiền: " + errorMessage);
                        }
                    });
                    int id = 1;
                    if(tinyDB.getAll().containsKey("savedUser")){
                        User user = tinyDB.getObject("savedUser", com.example.project.model.User.class);
                        id = user.getId();
                    }
                    userObject.put("id", id);
                    jsonBody.put("user", userObject);
                    jsonBody.put("addressUser", addUser);
                    shopObject.put("id", addressShop.getId());
                    jsonBody.put("shop", shopObject);
                    jsonBody.put("totalPrice", Long.parseLong(price));
                    jsonBody.put("fullName", fName);
                    jsonBody.put("sdt", phone);
                    jsonBody.put("utensils", finalDcu);
                    jsonBody.put("note", noted);
                    jsonBody.put("status","");
                    jsonBody.put("products", productsArray);
                    jsonBody.put("paymentMethod", paymentMethod);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Return the byte[] of the JSON string
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                return headerMap;
            }
        };

        requestQueue.add(stringRequest);
    }
    private void initView(){
        recyclerView = findViewById(R.id.recyclerView);
        btnShop = findViewById(R.id.imageViewAddress);
        giaTxt = findViewById(R.id.txtTotalCost);
        themMonBtn = findViewById(R.id.btnAddFood);
        giaDKTxt = findViewById(R.id.txtCost);
        addressTxt = findViewById(R.id.txtAddress);
        checkBox = findViewById(R.id.checkBox);
        btnDatHang = findViewById(R.id.btnDatHang);
        fullName = findViewById(R.id.fullName);
        sdt = findViewById(R.id.eTxtPhone);
        note = findViewById(R.id.eTxtNote);
        addressShopTxt = findViewById(R.id.txtShop);
        utensils = findViewById(R.id.switch1);
        tinyDB = new TinyDB(this);
        tvError = findViewById(R.id.tvError);
        tvError1 = findViewById(R.id.tvError1);
        addressShopTxt = findViewById(R.id.txtShop);
        AddressShop addressShop = tinyDB.getObject("addressShop", AddressShop.class);
        addressShopTxt.setText(addressShop.getName());
        String addUser = tinyDB.getString("addr_no_log");
        if(tinyDB.getBoolean("is_logged_in"))
        {
            Address addressUser = tinyDB.getObject("address", Address.class);
            addUser = addressUser.getAddress_details();
        }
        if(checkBox.isChecked()){
            btnDatHang.setAlpha(1f);
        }
        else {
            btnDatHang.setAlpha(0.5f);
        }
        try {
            initList();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            updateTotal();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        addressTxt.setText(addUser);
        btnShop.setOnClickListener(view -> {
            Intent intent = new Intent(PaymentActivity.this, AddressShopActivity.class);
            startActivity(intent);
        });
        themMonBtn.setOnClickListener(v -> {
            Intent intent=new Intent(PaymentActivity.this,BaseActivity.class);
            intent.putExtra("opened_fragment", "MENU");
            startActivity(intent);
        });
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                btnDatHang.setEnabled(true);
                btnDatHang.setAlpha(1f);
            } else {
                btnDatHang.setEnabled(false);
                btnDatHang.setAlpha(0.5f);
            }
        });
        sdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString().trim();

                if (phone.isEmpty()) {
                    tvError1.setText("* Không được để trống");
                    tvError1.setVisibility(VISIBLE);
                }
                else if(!StringHelper.isValidVietnamPhone(phone)){
                    tvError1.setText("* Số điện thoại không hợp lệ");
                    tvError1.setVisibility(VISIBLE);
                }
                else {
                    tvError1.setVisibility(GONE);
                }
            }
        });
        fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString().trim();

                if (phone.isEmpty()) {
                    tvError.setText("* Không được để trống");
                    tvError.setVisibility(VISIBLE);
                }
                else {
                    tvError.setVisibility(GONE);
                }
            }
        });
    }

    private void initList() throws JSONException {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        managementCart.getListCart(new CartResponse() {
            @Override
            public void onSuccess(ArrayList<Food> cartList) {
                adapter = new FoodListPaymentAdapter(cartList,PaymentActivity.this, () -> updateTotal());
                RecyclerView.ItemDecoration decoration = new DividerItemDecoration(PaymentActivity.this,DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(decoration);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Lỗi khi lấy giỏ hàng: " + errorMessage);
            }
        });
    }

     public void updateTotal() throws JSONException {
            managementCart.getTotalFee(new TotalFeeResponse() {
                @Override
                public void onSuccess(int totalFee) {
                    price = String.valueOf(totalFee);
                    DecimalFormat decimalFormat = new DecimalFormat("#,###");
                    String formattedPrice = decimalFormat.format(totalFee) + " đ";
                    giaTxt.setText(formattedPrice);
                    giaDKTxt.setText(formattedPrice);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Lỗi khi tính tổng tiền: " + errorMessage);
                }
            });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
    private void showDialog(){
        ConstraintLayout errorConstrlayout = findViewById(R.id.successConstraintLayout);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_order_success,errorConstrlayout);
        TextView okBtn = view.findViewById(R.id.okBtn);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        okBtn.findViewById(R.id.okBtn).setOnClickListener(v -> {
            alertDialog.dismiss();
            Intent intent = new Intent(PaymentActivity.this,BaseActivity.class);
            startActivity(intent);
            finish();
        });
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    @Override
    public void onResume() {
        super.onResume();
        initView();
    }
}