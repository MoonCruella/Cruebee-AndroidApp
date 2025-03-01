package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.GenericLifecycleObserver;

import com.bumptech.glide.Glide;
import com.example.project.utils.UrlUtil;

import java.text.DecimalFormat;

public class ShowDetailActivity extends AppCompatActivity {
    private TextView addToCartBtn;
    private TextView titleTxt,feeTxt,desTxt,countTxt;
    private ImageView plusBtn,minusBtn,picFood;

    private int numberOrder = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        initView();
        getBundle();
    }

    private void initView(){
        addToCartBtn = findViewById(R.id.addToCartBtt);
        titleTxt = findViewById(R.id.titleTxt);
        feeTxt = findViewById(R.id.priceTxt);
        desTxt = findViewById(R.id.descriptionTxt);
        countTxt = findViewById(R.id.countTxt);
        plusBtn = findViewById(R.id.plusImgView);
        minusBtn = findViewById(R.id.minusImgView);
        picFood = findViewById(R.id.foodImgView);

        Bundle bundle = getIntent().getExtras();
        String fName = bundle.getString("name");
        int fPrice = bundle.getInt("price");
        int fId = bundle.getInt("foodId");
        String img = bundle.getString("image");
        String des = bundle.getString("description");
        titleTxt.setText(fName.toString());
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(fPrice) + " đ";
        feeTxt.setText(formattedPrice);
        desTxt.setText(des);
        Glide.with(this).load(img).into(picFood);

    }

    private void getBundle(){
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOrder += 1;
                countTxt.setText(String.valueOf(numberOrder));
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberOrder > 1){
                    numberOrder -= 1;
                }
                countTxt.setText(String.valueOf(numberOrder));
            }
        });
    }




}