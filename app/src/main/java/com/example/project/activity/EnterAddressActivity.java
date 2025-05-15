package com.example.project.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.R;
import com.example.project.adapter.AddressAdapter;
import com.example.project.helpers.StringHelper;
import com.example.project.helpers.TinyDB;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EnterAddressActivity extends AppCompatActivity {

    private EditText edtAddress,edtHouseNumber, edtStreet, edtWard, edtDistrict, edtProvince;
    private ImageView clearTextBtn;
    private LottieAnimationView loadingBar;
    private FrameLayout loadingOverlay;
    private ListView listView;
    private String selectedAddress;
    private Double lng,lat;
    private TinyDB tinyDB;
    private FusedLocationProviderClient fusedLocationClient;
    private AddressAdapter addressAdapter;
    private List<String> addressList = new ArrayList<>();
    private RequestQueue requestQueue;
    private List<JSONObject> addressJsonList = new ArrayList<>();
    private LinearLayout addressForm;
    private TextView btnSave,txtAccessAddress;

    com.example.project.model.Address address;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_address);
        FrameLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
            return insets;
        });
        edtAddress = findViewById(R.id.edtAddress);
        listView = findViewById(R.id.listView);
        addressForm = findViewById(R.id.address_form);
        edtHouseNumber = findViewById(R.id.edtHouseNumber);
        edtWard = findViewById(R.id.edtWard);
        edtStreet = findViewById(R.id.edtStreet);
        loadingBar = findViewById(R.id.loadingBar);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        edtDistrict = findViewById(R.id.edtDistrict);
        clearTextBtn = findViewById(R.id.clearTextBtn);
        edtProvince = findViewById(R.id.edtProvince);
        btnSave = findViewById(R.id.btnOk);
        txtAccessAddress = findViewById(R.id.txtAccessAddress);
        requestQueue = Volley.newRequestQueue(this);
        tinyDB = new TinyDB(this);

        address = (com.example.project.model.Address) getIntent().getSerializableExtra("object");
        addressAdapter = new AddressAdapter(this, addressList);
        listView.setAdapter(addressAdapter);

        txtAccessAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findAddress();
            }
        });

        edtAddress.setOnClickListener(v -> {
            addressForm.setVisibility(View.GONE); // Ẩn form nhập địa chỉ
            listView.setVisibility(View.VISIBLE); // Hiển thị danh sách gợi ý
        });

        clearTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtAddress.setText("");
                addressForm.setVisibility(View.GONE); // Ẩn form nhập địa chỉ
                addressList.clear();
                addressAdapter.notifyDataSetChanged();
                listView.setVisibility(View.VISIBLE); // Hiển thị danh sách gợi ý
            }
        });


        Handler handler = new Handler();
        final Runnable[] searchRunnable = {null};

        edtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) { // Chỉ tìm kiếm khi nhập từ 3 ký tự trở lên
                    if (searchRunnable[0] != null) {
                        handler.removeCallbacks(searchRunnable[0]);
                    }
                    searchRunnable[0] = () -> fetchAddresses(s.toString());
                    handler.postDelayed(searchRunnable[0], 500); // Đợi 500ms rồi gọi API
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                JSONObject selectedJson = addressJsonList.get(position);
                JSONObject addressDetails = selectedJson.getJSONObject("address");

                // Lấy thông tin latitude và longitude từ đối tượng JSON
                double latitude = selectedJson.getDouble("lat");
                double longitude = selectedJson.getDouble("lon");

                // Lấy từng phần của địa chỉ
                String houseNumber = addressDetails.optString("aeroway", "");// Số nhà
                if(houseNumber.isEmpty()){
                    houseNumber = addressDetails.optString("house_number", "");
                    if(houseNumber.isEmpty()){
                        houseNumber = addressDetails.optString("building", "");
                    }
                    if(houseNumber.isEmpty()){
                        houseNumber = addressDetails.optString("amenity", "");
                    }

                }
                String road = addressDetails.optString("road", "");// Tên đường
                if(road.isEmpty()){
                    road = addressDetails.optString("quarter", "");
                }

                String ward = addressDetails.optString("village", "");
                if(ward.isEmpty())// Phường/Xã
                {
                    ward = addressDetails.optString("suburb", "");
                    if(ward.isEmpty()){
                        ward = addressDetails.optString("town", "");
                    }
                }

                String district = addressDetails.optString("city_district", "");// Quận/Huyện
                String province = province = addressDetails.optString("city", "");// Thành phố/Tỉnh
                if(province.isEmpty()){
                    province = province = addressDetails.optString("state", "");
                }
                if(district.isEmpty())
                {
                    district = addressDetails.optString("city", "");
                    if(district.isEmpty()){
                        district = addressDetails.optString("county", "");
                    }
                    if(addressDetails.optString("ISO3166-2-lvl4", "").equals("VN-SG"))
                    {
                        province = "Thành Phố Hồ Chí Minh";
                    }
                }

                this.lat = latitude;
                this.lng = longitude;

                // Cập nhật EditText
                edtAddress.setText(addressList.get(position));
                edtHouseNumber.setText(houseNumber);
                edtStreet.setText(road);
                edtWard.setText(ward);
                edtDistrict.setText(district);
                edtProvince.setText(province);

            } catch (Exception e) {
                e.printStackTrace();
            }
            // Hiện Form nhập địa chỉ
            addressForm.setVisibility(View.VISIBLE );
            listView.setVisibility(View.GONE);
            selectedAddress = addressList.get(position);
            edtAddress.setText(selectedAddress);


        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String houseNumber = edtHouseNumber.getText().toString();
                String ward = edtWard.getText().toString();
                String district = edtDistrict.getText().toString();
                String province = edtProvince.getText().toString();

                // Kiểm tra nếu form nhập đầy đủ
                if (houseNumber.isEmpty() || ward.isEmpty() || district.isEmpty() || province.isEmpty()) {
                    Toast.makeText(EnterAddressActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else {
                    saveAddress(lat,lng,selectedAddress);
                    // Tạo Intent để trả lại dữ liệu đã thay đổi
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updated_address", address);  // Truyền đối tượng address đã cập nhật
                    Log.d("ADDRESS ALTJD",address.toString());
                    setResult(RESULT_OK, resultIntent);  // Trả lại kết quả cho Activity gọi (AddressDetailsActivity)
                    finish();

                }
            }
        });
    }

    private void fetchAddresses(String query) {
        String url = "https://nominatim.openstreetmap.org/search?format=json&countrycodes=VN&bounded=1&viewbox=102.14441,23.393395,109.46911,8.179066&addressdetails=1&q=" + query;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    addressList.clear();
                    addressJsonList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String address = obj.getString("display_name");
                            addressList.add(address);
                            addressJsonList.add(obj);
                        }
                        addressAdapter.notifyDataSetChanged(); // Cập nhật ListView
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> {
        });

        requestQueue.add(jsonArrayRequest);
    }

    // lay dia chi hien tai bang cach truy cap gps
    private Location lastLocation; // Lưu vị trí trước đó

    private void findAddress() {
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingBar.playAnimation();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Kiểm tra quyền truy cập vị trí
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // Ưu tiên độ chính xác cao
                .setInterval(500) // Cập nhật mỗi 500ms
                .setFastestInterval(500)
                .setSmallestDisplacement(50) // Chỉ cập nhật nếu di chuyển 50m
                .setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    Location newLocation = locationResult.getLastLocation();
                    loadingOverlay.setVisibility(View.GONE);
                    loadingBar.cancelAnimation();

                    // Kiểm tra nếu vị trí thay đổi 50m
                    lastLocation = newLocation;
                    handleLocationResult(newLocation);

                }
            }
        }, Looper.getMainLooper());
    }

    // Xử lý kết quả vị trí
    private void handleLocationResult(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        getAddressFromCoordinates(latitude, longitude);
    }

    private void getAddressFromCoordinates(double latitude, double longitude) {

        // Kiểm tra nếu Geocoder có sẵn
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {

                // Lấy danh sách các địa chỉ từ Geocoder
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String uAddress =  addresses.get(0).getAddressLine(0);

                    // Số nhà, đường
                    String streetAddress = address.getThoroughfare();
                    String subStreetAddress = address.getSubThoroughfare();

                    // Phuong/Xa
                    String addressLine = address.getAddressLine(0);
                    String precinct = StringHelper.getSubstringBetween(addressLine, ',', ',');

                    // Quan/Huyen
                    String wardOrDistrict = address.getSubAdminArea();

                    // Tinh/Thanh pho
                    String state = address.getAdminArea();

                    this.lat = latitude;
                    this.lng = longitude;
                    edtAddress.setText(uAddress);
                    selectedAddress = uAddress;
                    addressForm.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);

                    // Cập nhật EditText
                    edtHouseNumber.setText(subStreetAddress);
                    edtStreet.setText(streetAddress);
                    edtWard.setText(precinct);
                    edtDistrict.setText(wardOrDistrict);
                    edtProvince.setText(state);


                } else {
                    Toast.makeText(this, "Không thể tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi khi lấy địa chỉ", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Geocoder không có sẵn trên thiết bị", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveAddress(Double latitude,Double longitude,String addressDetails){
        tinyDB.putDouble("lat",latitude);
        tinyDB.putDouble("lng",longitude);
        tinyDB.putString("addr_no_log",selectedAddress);
        address.setAddress_details(selectedAddress);
        address.setLatitude(latitude);
        address.setLongitude(longitude);
    }

}