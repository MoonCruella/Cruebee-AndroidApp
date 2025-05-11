package com.example.project;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PolicyActivity extends AppCompatActivity {
    private ConstraintLayout supportBtn, paymentBtn, shipBtn, trasacBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        ConstraintLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
            return insets;
        });
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