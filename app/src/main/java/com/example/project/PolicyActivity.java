package com.example.project;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class PolicyActivity extends AppCompatActivity {
    private ConstraintLayout supportBtn, paymentBtn, shipBtn, trasacBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        setContentView(R.layout.activity_policy);
        supportBtn = findViewById(R.id.supportBtn);
        paymentBtn = findViewById(R.id.verifyBtn);
        shipBtn = findViewById(R.id.newsBtn);
        trasacBtn = findViewById(R.id.settingsBtn);
        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PolicyActivity.this,PolicyCustomerActivity.class));
            }
        });
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PolicyActivity.this,PolicyPaymentActivity.class));
            }
        });
        shipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PolicyActivity.this,PolicyShipActivity.class));
            }
        });
        trasacBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PolicyActivity.this,PolicyTransactionActivity.class));
            }
        });
    }
}