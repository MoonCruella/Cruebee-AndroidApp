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
import com.example.project.helpers.ManagementCart;
import com.example.project.model.Food;
import com.example.project.utils.UrlUtil;

import org.json.JSONException;

import java.text.DecimalFormat;

public class ShowDetailActivity extends AppCompatActivity {
    private TextView addToCartBtn;
    private TextView titleTxt,feeTxt,desTxt,countTxt;
    private ImageView plusBtn,minusBtn,picFood;
    private ManagementCart managementCart;
    private Food object;

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

        managementCart = new ManagementCart(this);
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



    }

    private void getBundle(){

        object = (Food) getIntent().getSerializableExtra("object");
        titleTxt.setText(object.getName());
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedPrice = decimalFormat.format(object.getPrice()) + " đ";
        feeTxt.setText(formattedPrice);
        desTxt.setText(object.getDescription());
        Glide.with(this).load(object.getImage()).into(picFood);
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

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object.setNumberInCart(numberOrder);
                try {
                    managementCart.insertFood(object);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                //Quay tro lai man hinh chon menu
                finish();
            }
        });
    }




}