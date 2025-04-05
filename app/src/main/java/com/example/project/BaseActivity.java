package com.example.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project.adapter.ViewPager2Adapter;
import com.example.project.interfaces.OnFragmentSwitchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BaseActivity extends AppCompatActivity implements OnFragmentSwitchListener  {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    private float dX, dY;
    private int lastAction;
    private final int[] tabIcons = {
            R.drawable.home,       // Icon cho Trang chủ
            R.drawable.menu,       // Icon cho Thực đơn
            R.drawable.discount,   // Icon cho Khuyến mãi
            R.drawable.three_dots  // Icon cho Thêm
    };

    private final String[] tabTitles = {
            "Trang chủ",
            "Thực đơn",
            "Khuyến mãi",
            "Thêm"
    };

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.white, getTheme()));
        getWindow().setStatusBarColor(getResources().getColor(R.color.red, getTheme()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setUserInputEnabled(false);
        tabLayout = findViewById(R.id.tabLayout);
        fab = findViewById(R.id.fabAction);




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartDialog cartDialog = new CartDialog( BaseActivity.this, (OnFragmentSwitchListener) BaseActivity.this);
                cartDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                cartDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
                cartDialog.setCancelable(false);
                cartDialog.show();
            }
        });


        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                // Lấy kích thước FAB
                int fabWidth = view.getWidth();
                int fabHeight = view.getHeight();
                int tabLayoutHeight = fab.getHeight();
                // Lấy chiều cao của Toolbar
                int toolbarHeight = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()
                );
                int margin = (int) (20 * getResources().getDisplayMetrics().density);
                // Lấy kích thước màn hình
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int screenHeight = getResources().getDisplayMetrics().heightPixels;


                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;


                        // **Giới hạn với margin 20dp**
                        if (newX < margin) newX = margin;  // Không vượt qua mép trái
                        if (newX > screenWidth - fabWidth - margin) newX = screenWidth - fabWidth - margin; // Không vượt qua mép phải
                        if (newY < toolbarHeight + margin) newY = toolbarHeight;  // Không kéo lên trên Toolbar
                        if (newY > screenHeight - tabLayoutHeight - fabHeight - margin ) {
                            newY = screenHeight - tabLayoutHeight - fabHeight - margin;
                        }


                        view.setX(newX);
                        view.setY(newY);
                        lastAction = MotionEvent.ACTION_MOVE;
                        return true;

                    case MotionEvent.ACTION_UP:
                        // Nếu là kéo thả, không kích hoạt sự kiện click
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            view.performClick();
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });

        viewPager.setAdapter(new ViewPager2Adapter(getSupportFragmentManager(),getLifecycle(),this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Gán layout tùy chỉnh cho từng tab
            View customTab = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
            ImageView tabIcon = customTab.findViewById(R.id.tabIcon);
            TextView tabText = customTab.findViewById(R.id.tabText);

            tabIcon.setImageResource(tabIcons[position]);
            tabText.setText(tabTitles[position]);

            tab.setCustomView(customTab);
        }).attach();

        TabLayout.Tab defaultTab = tabLayout.getTabAt(0); // Tab "Trang chủ"
        if (defaultTab != null) {
            View tabView = defaultTab.getCustomView();
            if (tabView != null) {
                View tabUnderline = tabView.findViewById(R.id.tabUnderline);
                tabUnderline.setAlpha(1); // Hiển thị underline mặc định
            }
        }

        // Xử lý sự kiện khi chọn tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition()); // Chuyển Fragment theo vị trí tab
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    View tabUnderline = tabView.findViewById(R.id.tabUnderline);
                    tabUnderline.setAlpha(1);  // Hiện underline
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    View tabUnderline = tabView.findViewById(R.id.tabUnderline);
                    tabUnderline.setAlpha(0);  // Ẩn underline
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position)); // Đồng bộ tab
            }
        });


    }
    @Override
    public void onSwitchToFragment(String fragmentTag) {
        if (fragmentTag.equals("MENU")) {
            viewPager.setCurrentItem(1, true); // Chuyển đến tab "Thực Đơn"
        }
    }


}