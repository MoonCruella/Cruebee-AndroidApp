package com.example.project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.example.project.helpers.TinyDB;
import com.example.project.model.Food;
import com.example.project.utils.UrlUtil;

import org.json.JSONException;

import java.text.DecimalFormat;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class ShowDetailActivity extends AppCompatActivity {
    private TextView addToCartBtn,thanhtoanBtn;
    private TextView titleTxt,feeTxt,desTxt,countTxt;
    private ImageView plusBtn,minusBtn,picFood;
    private ManagementCart managementCart;
    private Food object;
    private TinyDB tinyDB;
    private boolean hasShopAddress;

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
        thanhtoanBtn = findViewById(R.id.checkoutBtt);
        titleTxt = findViewById(R.id.titleTxt);
        feeTxt = findViewById(R.id.priceTxt);
        desTxt = findViewById(R.id.descriptionTxt);
        countTxt = findViewById(R.id.countTxt);
        plusBtn = findViewById(R.id.plusImgView);
        minusBtn = findViewById(R.id.minusImgView);
        picFood = findViewById(R.id.foodImgView);
        tinyDB = new TinyDB(this);
        hasShopAddress = tinyDB.getAll().containsKey("addressShop");
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
                blurView.setClipToOutline(true);
                blurView.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 60f); // 60dp tương đương
                    }
                });

                // Optionally, you can setup the BlurView with other configurations
                View decorView = getWindow().getDecorView();
                ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

                Drawable windowBackground = decorView.getBackground();
                blurView.setClipToOutline(true);
                blurView.setupWith(rootView)
                        .setFrameClearDrawable(windowBackground)
                        .setBlurRadius(5f);
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
                if(!hasShopAddress){
                    showErrorDialogAndFinish();
                }
                else{
                    object.setNumberInCart(numberOrder);
                    try {
                        managementCart.insertFood(object);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    //Quay tro lai man hinh chon menu
                    finish();
                }
            }
        });

        thanhtoanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasShopAddress){
                    showErrorDialogAndFinish();
                }
                else {

                    // Them san pham vao gio hang, sau do chuyen den man hinh thanh toan
                    object.setNumberInCart(numberOrder);
                    try {
                        managementCart.insertFood(object);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    Context context = v.getContext();
                    Intent intent = new Intent(context, PaymentActivity.class);
                    context.startActivity(intent);
                }
            }
        });
    }
    private void showErrorDialogAndFinish() {
        ConstraintLayout errorConstrlayout = findViewById(R.id.errrorConstraintLayout);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_error, errorConstrlayout);
        TextView errorClose = view.findViewById(R.id.errorClose);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        errorClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish(); // 👈 Chỉ gọi finish() sau khi bấm OK
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }




}