package com.example.project;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.project.adapter.ViewPager2Adapter;
import com.example.project.helpers.TinyDB;
import com.example.project.interfaces.OnFragmentSwitchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class BaseActivity extends AppCompatActivity implements OnFragmentSwitchListener  {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    private float dX, dY;
    private TinyDB tinyDB;
    boolean hasShopAddress;
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

        FrameLayout mainLayout = findViewById(R.id.main);
        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.red));
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, systemInsets.bottom); // tránh cả status và navigation bar
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        viewPager.setUserInputEnabled(false);
        tabLayout = findViewById(R.id.tabLayout);
        fab = findViewById(R.id.fabAction);
        tinyDB = new TinyDB(this);
        hasShopAddress = tinyDB.getAll().containsKey("addressShop");

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

                int fabWidth = view.getWidth();
                int fabHeight = view.getHeight();
                int tabHeight = tabLayout.getHeight();
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                int toolbarHeight = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()
                );
                int margin = (int) (20 * getResources().getDisplayMetrics().density);

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;

                        // Giới hạn trong màn hình
                        if (newX < margin) newX = margin;
                        if (newX > screenWidth - fabWidth - margin) newX = screenWidth - fabWidth - margin;
                        if (newY < toolbarHeight + margin) newY = toolbarHeight + margin;
                        if (newY > screenHeight - tabHeight - fabHeight - margin) newY = screenHeight - tabHeight - fabHeight - margin;

                        view.setX(newX);
                        view.setY(newY);
                        lastAction = MotionEvent.ACTION_MOVE;
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            view.performClick();
                        } else {
                            // Tính toán vị trí hiện tại để snap về lề trái hoặc phải
                            float currentX = view.getX();
                            float centerScreen = screenWidth / 2f;
                            float targetX;

                            if (currentX + fabWidth / 2 <= centerScreen) {
                                targetX = margin; // Snap về trái
                            } else {
                                targetX = screenWidth - fabWidth - margin; // Snap về phải
                            }

                            // Animate tới vị trí snap
                            view.animate()
                                    .x(targetX)
                                    .setDuration(200)
                                    .start();
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
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            final int index = i;
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null && tab.getCustomView() != null) {
                tab.getCustomView().setOnClickListener(v -> {
                    if (index == 1) { // Tab "Thực đơn"
                        if (!hasShopAddress) {
                            showErrorDialog("Bạn vui lòng nhập địa chỉ/chọn cửa hàng nhé!");
                            return;
                        }
                    }
                    tabLayout.selectTab(tab); // Chỉ select nếu hợp lệ
                });
            }
        }
        // Xử lý sự kiện khi chọn tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position); // chuyển fragment
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    View tabUnderline = tabView.findViewById(R.id.tabUnderline);
                    tabUnderline.setAlpha(1);
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
                if (tab.getPosition() == 0 ) {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("f" + viewPager.getCurrentItem());
                    if (currentFragment instanceof HomeFragment) {
                        HomeFragment homeFragment = (HomeFragment) currentFragment;
                        NestedScrollView nestedScrollView = homeFragment.getView().findViewById(R.id.scrollview);
                        nestedScrollView.smoothScrollTo(0, 0);
                    }
                }
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position)); // Đồng bộ tab
            }
        });

        Bundle b = getIntent().getExtras();
        String name = (b != null) ? b.getString("opened_fragment") : null;
        if ("MENU".equals(name)) {
            if(!hasShopAddress){
                showErrorDialog("Bạn vui lòng nhập địa chỉ/chọn cửa hàng nhé!");
                return;
            }
            viewPager.setCurrentItem(1, true);
        }
        else if("SHOW_MORE".equals(name)){
            viewPager.setCurrentItem(3,false);
        } else {
            viewPager.setCurrentItem(0, true);
        }

    }
    @Override
    public void onSwitchToFragment(String fragmentTag) {
        if (fragmentTag.equals("MENU")) {
            if(!hasShopAddress){
                showErrorDialog("Bạn vui lòng nhập địa chỉ/chọn cửa hàng nhé!");
                return;
            }
            viewPager.setCurrentItem(1, true); // Chuyển đến tab "Thực Đơn"
        }
        else if (fragmentTag.equals("HOME")) {
            if(!hasShopAddress){
                showErrorDialog("Bạn vui lòng nhập địa chỉ/chọn cửa hàng nhé!");
                return;
            }
            viewPager.setCurrentItem(0, true); // Chuyển đến tab "Thêm"
        }
        else if (fragmentTag.equals("SHOW_MORE")) {
            viewPager.setCurrentItem(3, true); // Chuyển đến tab "Thêm"
        }
    }

    @Override
    public void onBackPressed() {
        // Check if you're on a specific fragment
        int currentItem = viewPager.getCurrentItem();  // Get the current item of the ViewPager2

        if (currentItem != 0) {
            viewPager.setCurrentItem(0, true);  // Switch to HomeFragment
        } else {
            super.onBackPressed();  // Default back press behavior
        }
    }
    private void showErrorDialog(String mess) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_error, null);

        TextView errorClose = view.findViewById(R.id.errorClose);
        TextView errorDes = view.findViewById(R.id.errorDes);
        errorDes.setText(mess);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);

        final AlertDialog alertDialog = builder.create();

        View overlayView = findViewById(R.id.overlayView);
        if (overlayView != null) {
            overlayView.setVisibility(VISIBLE); // Hiển thị nền mờ
        }

        errorClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (overlayView != null) {
                    overlayView.setVisibility(GONE); // Ẩn nền mờ
                }
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0)); // Nền trong suốt
        }
        alertDialog.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        hasShopAddress = tinyDB.getAll().containsKey("addressShop");
    }
}