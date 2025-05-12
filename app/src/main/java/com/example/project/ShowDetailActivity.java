package com.example.project;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.example.project.helpers.ManagementCart;
import com.example.project.helpers.TinyDB;
import com.example.project.interfaces.InsertCartCallback;
import com.example.project.model.Food;
import org.json.JSONException;
import java.text.DecimalFormat;
import eightbitlab.com.blurview.BlurView;

public class ShowDetailActivity extends AppCompatActivity {
    private TextView addToCartBtn,thanhtoanBtn;
    private TextView titleTxt,feeTxt,desTxt,countTxt,soldCount;
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
        FrameLayout mainLayout = findViewById(R.id.main);

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
        soldCount = findViewById(R.id.soldCount);
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
        soldCount.setText("Đã bán: " + String.valueOf(object.getSoldCount()) + " ");
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
                    InsertCartCallback callback1;
                    try {
                        managementCart.insertFood(object, new InsertCartCallback() {
                            @Override
                            public void onInserted() {
                            }
                        });
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
                        managementCart.insertFood(object, new InsertCartCallback() {
                            @Override
                            public void onInserted() {
                                // Khi insert xong, mới chuyển màn
                                Context context = v.getContext();
                                Intent intent = new Intent(context, PaymentActivity.class);
                                context.startActivity(intent);
                            }
                        });
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
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_error);
        View overlayView = findViewById(R.id.overlayView);
        if (overlayView != null) {
            overlayView.setVisibility(VISIBLE); // Hiển thị nền mờ
        }
        dialog.setCancelable(false);
        TextView errorClose = dialog.findViewById(R.id.errorClose);
        errorClose.setOnClickListener(v -> {
            dialog.dismiss();
            if(overlayView != null){
                overlayView.setVisibility(GONE);
            }
            finish();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0)); // Nền trong suốt
        }
        if (overlayView != null) {
            overlayView.setVisibility(VISIBLE); // Hiển thị nền mờ
        }
        dialog.show();
    }


}