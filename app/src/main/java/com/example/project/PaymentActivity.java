package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.project.adapter.FoodListPaymentAdapter;
import com.example.project.helpers.ManagementCart;
import com.example.project.helpers.TinyDB;
import com.example.project.interfaces.ChangeNumberItemsListener;
import com.example.project.model.PaymentProduct;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ManagementCart managementCart;
    private TinyDB tinyDB;
    private FoodListPaymentAdapter adapter;
    private TextView giaTxt,themMonBtn,giaDKTxt,addressTxt,addressShopTxt;
    private List<String> timeList = new ArrayList<>();
    private ArrayAdapter<String> timeAdapter;
    private EditText firstName,lastName,sdt,note;
    private Spinner spinnerDate,spinnerTime;
    private Switch utensils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
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
        initList();
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(managementCart.getTotalFee()) + " đ";
        giaTxt.setText(formattedPrice);
        giaDKTxt.setText(formattedPrice);

        firstName = findViewById(R.id.eTxtFName);
        lastName = findViewById(R.id.eTxtLName);
        sdt = findViewById(R.id.eTxtPhone);
        note = findViewById(R.id.eTxtNote);
        addressShopTxt = findViewById(R.id.txtShop);
        utensils = findViewById(R.id.switch1);
    }
    private void payment(){
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
        Boolean dcu = false;
        if (utensils.isChecked())
        {
            dcu = true;
        }
        Long totalPrice = Long.parseLong(giaTxt.getText().toString().trim());
        List<PaymentProduct> products = adapter.getFoodList();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
    }
    private void initView(){
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        giaTxt = (TextView) findViewById(R.id.txtTotalCost);
        themMonBtn = (TextView) findViewById(R.id.btnAddFood);
        giaDKTxt = (TextView) findViewById(R.id.txtCost);
        addressTxt = findViewById(R.id.txtAddress);
        tinyDB = new TinyDB(this);
        String fullAddress = tinyDB.getString("UserAddress");
        addressTxt.setText(fullAddress);
        themMonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, MenuFragment.class);
                startActivity(intent);
            }
        });
    }

    private void initList(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FoodListPaymentAdapter(managementCart.getListCart(),this, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedPrice = decimalFormat.format(managementCart.getTotalFee()) + " đ";
                giaTxt.setText(formattedPrice);
                giaDKTxt.setText(formattedPrice);
            }
        });
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);
    }
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
}