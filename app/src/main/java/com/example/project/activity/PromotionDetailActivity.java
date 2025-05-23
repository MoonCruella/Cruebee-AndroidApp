package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.example.project.R;
import com.google.android.material.card.MaterialCardView;

public class PromotionDetailActivity extends AppCompatActivity {

    private MaterialCardView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));

        int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        setContentView(R.layout.activity_promotion_detail);
        ImageView imageView = findViewById(R.id.detailImage);
        TextView titleView = findViewById(R.id.detailTitle);
        TextView descView = findViewById(R.id.detailDescription);
        TextView buyNowButton = findViewById(R.id.buyNowButton);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v ->  getOnBackPressedDispatcher().onBackPressed());

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String image = intent.getStringExtra("image");

        titleView.setText(title);

        descView.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));

        Glide.with(this).load(image).into(imageView);

        buyNowButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(PromotionDetailActivity.this, BaseActivity.class);
            intent1.putExtra("opened_fragment", "MENU"); // Gửi flag để mở MenuFragment
            startActivity(intent1);
            finish(); // Đóng PromotionDetailActivity
        });

    }
}
