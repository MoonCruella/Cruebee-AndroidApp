package com.example.project;

import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.GenericLifecycleObserver;

import com.bumptech.glide.Glide;
import com.example.project.helpers.ManagementCart;
import com.example.project.model.Food;
import com.example.project.utils.UrlUtil;

import org.json.JSONException;

import java.text.DecimalFormat;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

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
        ConstraintLayout mainLayout = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(0, 0, 0, navBarInsets.bottom); // đẩy layout lên khỏi nav bar
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
        BlurView blurView = findViewById(R.id.blurView);
        LinearLayout contentLayout = findViewById(R.id.content);

        // Wait for the layout to be fully measured
        contentLayout.post(new Runnable() {
            @Override
            public void run() {
                // Get the height of the LinearLayout
                int contentHeight = contentLayout.getHeight();

                // Set the BlurView's height to match the content's height
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) blurView.getLayoutParams();
                params.height = contentHeight;
                blurView.setLayoutParams(params);

                // Optionally, you can setup the BlurView with other configurations
                View decorView = getWindow().getDecorView();
                ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

                Drawable windowBackground = decorView.getBackground();
                blurView.setClipToOutline(true);
                blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurRadius(20f);  // Adjust blur radius as needed
            }
        });


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