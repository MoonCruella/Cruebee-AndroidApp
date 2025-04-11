package com.example.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.helpers.TinyDB;
import com.example.project.model.Address;
import com.example.project.utils.UrlUtil;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class AddAddressActivity extends AppCompatActivity {

    TextView usernameTxt,sdtTxt, addressTxt,noteTxt,saveBtn;
    TextInputLayout address_form;
    SwitchMaterial switch_is_primary;
    int is_primary,addressId;
    private ProgressDialog progressDialog;
    TinyDB tinyDB;
    RequestQueue requestQueue;
    Address address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        init();

    }

    public void init(){
        usernameTxt = findViewById(R.id.usernameTxt);
        sdtTxt = findViewById(R.id.sdtTxt);
        addressTxt = findViewById(R.id.addressTxt);
        noteTxt = findViewById(R.id.noteTxt);
        address_form = findViewById(R.id.address_form);
        saveBtn =findViewById(R.id.saveTxt);
        switch_is_primary = findViewById(R.id.switch_is_primary);
        address = (Address) getIntent().getSerializableExtra("object");

        assert address != null;
        usernameTxt.setText(address.getUsername());
        sdtTxt.setText(address.getSdt());
        addressTxt.setText(address.getAddress_details());
        noteTxt.setText(address.getNote());
        requestQueue = Volley.newRequestQueue(this);
        tinyDB = new TinyDB(this);
        addressId = address.getId();
        switch_is_primary.setChecked(address.getIs_primary() == 1);
        address_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAddressActivity.this, EnterAddressActivity.class);
                intent.putExtra("object",address);
                startActivityForResult(intent,133);
            }
        });
        addressTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAddressActivity.this, EnterAddressActivity.class);
                intent.putExtra("object",address);
                startActivityForResult(intent,133);
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
                }
                updateAddress(addressUser);
                Intent intent = new Intent(AddAddressActivity.this, DeliveryAddressActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    public void updateAddress(Address address){

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Saving your address...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        String url = UrlUtil.ADDRESS + "addresses/add";
        // Create the JSONObject for the POST request body
        JSONObject requestBody = null;
        try {
            requestBody = address.toJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle response from the server
                        progressDialog.dismiss(); // Dismiss loading dialog
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        progressDialog.dismiss(); // Dismiss loading dialog
                    }
                }
        );

        // Set a retry policy if necessary
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20 * 1000, // 20 seconds timeout
                2,          // Max retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
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