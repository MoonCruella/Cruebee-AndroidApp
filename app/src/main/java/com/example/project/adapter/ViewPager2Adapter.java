package com.example.project.adapter;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.project.DiscountFragment;
import com.example.project.HomeFragment;
import com.example.project.MenuFragment;
import com.example.project.ShowMoreFragment;
import com.example.project.ShowMore_User_Fragment;
import com.example.project.helpers.TinyDB;
import com.example.project.interfaces.OnFragmentSwitchListener;

public class ViewPager2Adapter extends FragmentStateAdapter {

    private final Context context;
    private TinyDB tinyDB;


    public ViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context) {
        super(fragmentManager, lifecycle);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        tinyDB = new TinyDB(context);
        Boolean is_logged_in = tinyDB.getBoolean("is_logged_in");
        switch (position){
            case 0:
                return new HomeFragment((OnFragmentSwitchListener) context);
            case 1:
                return new MenuFragment((OnFragmentSwitchListener) context);
            case 2:
                return new DiscountFragment();
            case 3:
                if (is_logged_in) {
                    return new ShowMore_User_Fragment();
                } else {
                    return new ShowMoreFragment();
                }
            default:
                return new HomeFragment((OnFragmentSwitchListener) context);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
