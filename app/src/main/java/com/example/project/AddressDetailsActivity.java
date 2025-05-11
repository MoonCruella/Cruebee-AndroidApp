package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.example.project.helpers.TinyDB;
import com.example.project.model.Address;
import com.example.project.utils.UrlUtil;
import com.example.project.volley.VolleyHelper;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONException;
import org.json.JSONObject;

public class AddressDetailsActivity extends AppCompatActivity {

    private TextView usernameTxt,sdtTxt, addressTxt,noteTxt,saveBtn,removeBtn;
    private TextInputLayout address_form;
    private SwitchMaterial switch_is_primary;
    int is_primary,addressId;
    private TinyDB tinyDB;
    private Address address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_details);
        ConstraintLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
            return insets;
        });

        init();

    }

    public void init(){
        usernameTxt = findViewById(R.id.usernameTxt);
        sdtTxt = findViewById(R.id.sdtTxt);
        addressTxt = findViewById(R.id.addressTxt);
        noteTxt = findViewById(R.id.noteTxt);
        address_form = findViewById(R.id.address_form);
        saveBtn =findViewById(R.id.saveTxt);
        removeBtn = findViewById(R.id.removeBtn);
        switch_is_primary = findViewById(R.id.switch_is_primary);
        address = (Address) getIntent().getSerializableExtra("object");

        assert address != null;
        usernameTxt.setText(address.getUsername());
        sdtTxt.setText(address.getSdt());
        addressTxt.setText(address.getAddress_details());
        noteTxt.setText(address.getNote());
        tinyDB = new TinyDB(this);
        addressId = address.getId();
        switch_is_primary.setChecked(address.getIs_primary() == 1);
        address_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressDetailsActivity.this, EnterAddressActivity.class);
                intent.putExtra("object",address);
                intent.putExtra("is_add", false);
                startActivityForResult(intent,133);
            }
        });
        addressTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressDetailsActivity.this, EnterAddressActivity.class);
                intent.putExtra("object",address);
                startActivityForResult(intent,133);
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAddress(address.getId());
                Intent intent = new Intent(AddressDetailsActivity.this, DeliveryAddressActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch_is_primary.isChecked())
                {
                    is_primary = 1;
                }
                else {
                    is_primary = 0;
                }

                Address addressUser = new Address(address.getId(),is_primary,address.getAddress_details(),address.getLatitude(),address.getLongitude(),address.getUserId(),usernameTxt.getText().toString(),noteTxt.getText().toString(),sdtTxt.getText().toString());
                if(is_primary == 1){
                    tinyDB.putObject("address",addressUser);
                    tinyDB.remove("addressShop");
                }
                updateAddress(addressUser);
                Intent intent = new Intent(AddressDetailsActivity.this, DeliveryAddressActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    public void removeAddress(int addressId) {
        String url = UrlUtil.ADDRESS + "addresses/delete?addressId=" + addressId;

        VolleyHelper.getInstance(this).sendStringRequestWithAuth(
                Request.Method.POST,
                url,
                null,
                true,
                response -> {
                    Toast.makeText(this, "Đã xóa địa chỉ!", Toast.LENGTH_SHORT).show();
                },
                error -> {

                    Toast.makeText(this, "Lỗi xóa địa chỉ", Toast.LENGTH_SHORT).show();
                }
        );
    }

    public void updateAddress(Address address) {
        String url = UrlUtil.ADDRESS + "addresses/update";

        JSONObject requestBody;
        try {
            requestBody = address.toJson();
        } catch (JSONException e) {
            e.printStackTrace();
            return; // Không gửi request nếu JSON lỗi
        }

        VolleyHelper.getInstance(this).sendJsonObjectRequestWithAuth(
                Request.Method.POST,
                url,
                requestBody,
                true,
                response -> {
                    Toast.makeText(this, "Cập nhật địa chỉ thành công!", Toast.LENGTH_SHORT).show();
                },
                error -> {

                }
        );
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 133) {
            if (data != null) {
                // Kiểm tra xem có dữ liệu trả về không
                Address updatedAddress = (Address) data.getSerializableExtra("updated_address");

                if (updatedAddress != null) {
                    address.setAddress_details(updatedAddress.getAddress_details());
                    address.setLatitude(updatedAddress.getLatitude());
                    address.setLongitude(updatedAddress.getLongitude());
                    addressTxt.setText(updatedAddress.getAddress_details());
                }
            }
        }
    }



}