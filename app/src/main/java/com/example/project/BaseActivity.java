package com.example.project;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class BaseActivity extends AppCompatActivity {

    private View homeUnderline, menuUnderline, discountUnderline, moreUnderline;
    private Fragment homeFragment = new HomeFragment();
    private Fragment menuFragment = new MenuFragment();
    private Fragment discountFragment = new DiscountFragment();
    private Fragment showMoreFragment = new ShowMore_User_Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_base);

        View bottomAppBar = findViewById(R.id.bottomNavigation);
        ViewCompat.setOnApplyWindowInsetsListener(bottomAppBar, (v, insets) -> {
            int bottomPadding = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(0, 0, 0, bottomPadding);
            return insets;
        });
        // Ánh xạ gạch đỏ
        homeUnderline = findViewById(R.id.homeUnderline);
        menuUnderline = findViewById(R.id.menuUnderline);
        discountUnderline = findViewById(R.id.discountUnderline);
        moreUnderline = findViewById(R.id.moreUnderline);

        // Thêm tất cả Fragment vào FragmentManager, chỉ hiện HomeFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.contentFrame, homeFragment, "HOME");
        transaction.add(R.id.contentFrame, menuFragment, "MENU").hide(menuFragment);
        transaction.add(R.id.contentFrame, discountFragment, "DISCOUNT").hide(discountFragment);
        transaction.add(R.id.contentFrame, showMoreFragment, "MORE").hide(showMoreFragment);

        transaction.commit();

        // Gạch đỏ mặc định
        updateUI(homeUnderline);

        // Bắt sự kiện click
        findViewById(R.id.homeButton).setOnClickListener(v -> switchFragment(homeFragment, homeUnderline));
        findViewById(R.id.menuButton).setOnClickListener(v -> switchFragment(menuFragment, menuUnderline));
        findViewById(R.id.discountButton).setOnClickListener(v -> switchFragment(discountFragment, discountUnderline));
        findViewById(R.id.moreButton).setOnClickListener(v -> switchFragment(showMoreFragment, moreUnderline));
    }

    private void switchFragment(Fragment fragment, View selectedUnderline) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // ⚡ Tối ưu hiển thị
        transaction.setReorderingAllowed(true);

        // Ẩn tất cả Fragment trước khi hiện Fragment mới
        transaction.hide(homeFragment);
        transaction.hide(menuFragment);
        transaction.hide(discountFragment);
        transaction.hide(showMoreFragment);

        // Hiện Fragment mới
        transaction.show(fragment);
        transaction.commit();

        // Cập nhật giao diện gạch đỏ
        updateUI(selectedUnderline);
    }

    private void updateUI(View selectedUnderline) {
        homeUnderline.setVisibility(View.GONE);
        menuUnderline.setVisibility(View.GONE);
        discountUnderline.setVisibility(View.GONE);
        moreUnderline.setVisibility(View.GONE);

        selectedUnderline.setVisibility(View.VISIBLE);
    }
}