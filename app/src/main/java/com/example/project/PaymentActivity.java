package com.example.project;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.adapter.FoodListPaymentAdapter;
import com.example.project.helpers.ManagementCart;
import com.example.project.helpers.TinyDB;
import com.example.project.interfaces.CartResponse;
import com.example.project.interfaces.ChangeNumberItemsListener;
import com.example.project.interfaces.OnFragmentSwitchListener;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.momo.momo_partner.AppMoMoLib;
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
    private TextView giaTxt,themMonBtn,giaDKTxt,addressTxt,addressShopTxt;
    private List<String> timeList = new ArrayList<>();
    private ArrayAdapter<String> timeAdapter;
    private EditText firstName,lastName,sdt,note;
    private Spinner spinnerDate,spinnerTime;
    private Switch utensils;
    String price;
    private Button btnDatHang;
    //Momo
    private String amount = "10000";
    private String fee = "0";
    int environment = 0;//developer default
    private String merchantName = "Thanh toán đơn hàng";
    private String merchantCode = "CB01";
    private String merchantNameLabel = "CrueBee";
    private String description = "Thanh toán hóa đơn CrueBee";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        spinnerDate = findViewById(R.id.spinnerDate);
        spinnerTime = findViewById(R.id.spinnerTime);
        managementCart = new ManagementCart(this);
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
        List<String> dateList = new ArrayList<>();
        dateList.add("Hôm nay");
        dateList.add(getDayOfWeek(tomorrow.getDayOfWeek()) + ", " + tomorrow.format(formatter));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dateList);
        spinnerDate.setAdapter(adapter);
        timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timeList);
        spinnerTime.setAdapter(timeAdapter);
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position == 0) { // Nếu chọn Hôm nay
                    updateTimeListForToday();
                } else { // Nếu chọn Ngày mai
                    updateTimeListForTomorrow();
                }
                timeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        initView();
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

        firstName = findViewById(R.id.eTxtFName);
        lastName = findViewById(R.id.eTxtLName);
        sdt = findViewById(R.id.eTxtPhone);
        note = findViewById(R.id.eTxtNote);
        addressShopTxt = findViewById(R.id.txtShop);
        utensils = findViewById(R.id.switch1);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        btnDatHang = findViewById(R.id.btnDatHang);
        btnDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thanhToanZalopay();
            }
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
        if (!checkEditText())
        {
            return;
        }
        String addUser = addressTxt.getText().toString();
        String addShop = addressShopTxt.getText().toString();
        LocalDateTime receiveTime;
        String selectedDate = spinnerDate.getSelectedItem().toString();
        String selectedTime = spinnerTime.getSelectedItem().toString();
        LocalDate today = LocalDate.now();
        LocalDate selectedLocalDate = selectedDate.equals("Hôm nay") ? today : today.plusDays(1);
        if (selectedTime.equals("Bây giờ")) {
            receiveTime = LocalDateTime.now();
        } else {
            // Chuyển đổi thời gian thành LocalTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime selectedLocalTime = LocalTime.parse(selectedTime, formatter);

            // Kết hợp LocalDate và LocalTime thành LocalDateTime
            receiveTime = LocalDateTime.of(selectedLocalDate, selectedLocalTime);
        }
        String fName = firstName.getText().toString();
        String lName = lastName.getText().toString();
        String phone = sdt.getText().toString();
        String noted = note.getText().toString();
        boolean dcu = false;
        if (utensils.isChecked())
        {
            dcu = true;
        }
        List<PaymentProduct> products = adapter.getFoodList();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading... Please wait...!!");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = UrlUtil.ADDRESS + "payment";
        boolean finalDcu = dcu;
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        if(response.equals("Đặt hàng thành công!")){
                            Toast.makeText(PaymentActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PaymentActivity.this,BaseActivity.class);
                            startActivity(intent);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(PaymentActivity.this,"" + error,Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                // Create a JSONObject and put data into it
                JSONObject jsonBody = new JSONObject();
                try {
                    JSONObject userObject = new JSONObject();
                    JSONArray productsArray = new JSONArray();
                    for (PaymentProduct item : products) {
                        JSONObject productObject = new JSONObject();
                        JSONObject productIdObject = new JSONObject();

                        productIdObject.put("id", item.getProduct().getId());
                        productObject.put("product", productIdObject);
                        productObject.put("quantity", item.getQuantity());

                        productsArray.put(productObject);
                    }
                    managementCart.getTotalFee(new TotalFeeResponse() {
                        @Override
                        public void onSuccess(int totalFee) throws JSONException {
                            price = String.valueOf(totalFee);
                            jsonBody.put("totalPrice", totalFee);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Lỗi khi tính tổng tiền: " + errorMessage);
                        }
                    });
                    User user = tinyDB.getObject("savedUser", com.example.project.model.User.class);
                    userObject.put("id", user.getId());
                    jsonBody.put("user", userObject);
                    jsonBody.put("addressUser", addUser);
                    jsonBody.put("addressShop", addShop);
                    jsonBody.put("firstName", fName);
                    jsonBody.put("lastName", lName);
                    jsonBody.put("sdt", phone);
                    jsonBody.put("utensils", finalDcu);
                    jsonBody.put("note", noted);
                    jsonBody.put("receivedDate", receiveTime);
                    jsonBody.put("products", productsArray);
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
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        btnShop = findViewById(R.id.imageViewAddress);
        giaTxt = (TextView) findViewById(R.id.txtTotalCost);
        themMonBtn = (TextView) findViewById(R.id.btnAddFood);
        giaDKTxt = (TextView) findViewById(R.id.txtCost);
        addressTxt = findViewById(R.id.txtAddress);
        tinyDB = new TinyDB(this);
        addressShopTxt = findViewById(R.id.txtShop);
        AddressShop addressShop = tinyDB.getObject("addressShop", AddressShop.class);
        addressShopTxt.setText(addressShop.getName());
        String addUser = tinyDB.getString("addr_no_log");
        if(tinyDB.getBoolean("is_logged_in"))
        {
            Address addressUser = tinyDB.getObject("address", Address.class);
            addUser = addressUser.getAddress_details();
        }
        addressTxt.setText(addUser);
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentActivity.this, AddressShopActivity.class);
                startActivity(intent);
            }
        });
        themMonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PaymentActivity.this,BaseActivity.class);
                intent.putExtra("opened_fragment", "MENU");
                startActivity(intent);
            }
        });
    }

    private void initList() throws JSONException {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        managementCart.getListCart(new CartResponse() {
            @Override
            public void onSuccess(ArrayList<Food> cartList) {
                adapter = new FoodListPaymentAdapter(cartList,PaymentActivity.this, new ChangeNumberItemsListener() {
                    @Override
                    public void change() throws JSONException {
                        updateTotal();
                    }
                });
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "Thứ Hai";
            case TUESDAY: return "Thứ Ba";
            case WEDNESDAY: return "Thứ Tư";
            case THURSDAY: return "Thứ Năm";
            case FRIDAY: return "Thứ Sáu";
            case SATURDAY: return "Thứ Bảy";
            case SUNDAY: return "Chủ Nhật";
            default: return "";
        }
    }
    private void updateTimeListForToday() {
        timeList.clear();
        timeList.add("Bây giờ");
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateTimeListForTomorrow() {
        timeList.clear();
        LocalTime startTime = LocalTime.of(10, 0); // 10:00 AM
        LocalTime endTime = LocalTime.of(21, 0);   // 9:00 PM
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault());

        while (!startTime.isAfter(endTime)) {
            timeList.add(startTime.format(timeFormatter));
            startTime = startTime.plusMinutes(15);
        }
    }
    public boolean checkEditText(){
        String fName = firstName.getText().toString();
        String lName = lastName.getText().toString();
        String phone = sdt.getText().toString();
        if(fName.isEmpty()){
            firstName.setError("Vui lòng nhập thông tin!");
            return false;
        } else if (lName.isEmpty()) {
            lastName.setError("Vui lòng nhập thông tin!");
            return false;
        } else if (phone.isEmpty()) {
            sdt.setError("Vui lòng nhập thông tin!");
            return false;
        } else if (!phone.matches("^[0-9]{10}$")) {
            sdt.setError("Số điện thoại không chính xác!");
            return false;
        }
        firstName.setError(null);
        lastName.setError(null);
        sdt.setError(null);
        return true;
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
}