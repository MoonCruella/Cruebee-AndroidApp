package com.example.project;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShowMoreAcitivity extends AppCompatActivity {

    ConstraintLayout supportBtn,verifyBtn,newsBtn,settingsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_more);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

    }
    private void init(){
        supportBtn = (ConstraintLayout) findViewById(R.id.supportBtn);
        verifyBtn = (ConstraintLayout) findViewById(R.id.verifyBtn);
        newsBtn = (ConstraintLayout) findViewById(R.id.newsBtn);
        settingsBtn = (ConstraintLayout) findViewById(R.id.settingsBtn);


        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportBtn.getBackground().setAlpha(245);
            }
        });

        supportBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                supportBtn.getBackground().setAlpha(245);
                return true;
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyBtn.getBackground().setAlpha(245);
            }
        });

        verifyBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                verifyBtn.getBackground().setAlpha(245);
                return true;
            }
        });
    }
    public void openCartActivity(View view){
        CartListActivity cartListActivity = new CartListActivity(ShowMoreAcitivity.this);
        cartListActivity.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        cartListActivity.setCancelable(true);
        cartListActivity.show();
    }
    public void openMenuActivity(View view){
        Intent intent = new Intent(ShowMoreAcitivity.this,MenuActivity.class);
        startActivity(intent);
    }
    public void openHomeActivity(View view){
        Intent intent = new Intent(ShowMoreAcitivity.this,HomeActivity.class);
        startActivity(intent);
    }
    public void openShowMoreActivity(View view){
        Intent intent = new Intent(ShowMoreAcitivity.this,ShowMoreAcitivity.class);
        startActivity(intent);
    }
    public void openDiscountActivity(View view){
        Intent intent = new Intent(ShowMoreAcitivity.this,DiscountActivity.class);
        startActivity(intent);
    }
}