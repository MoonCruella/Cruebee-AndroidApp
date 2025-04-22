package com.example.project.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.project.DiscountFragment;
import com.example.project.HomeFragment;
import com.example.project.MenuFragment;
import com.example.project.R;
import com.example.project.ShowMoreFragment;
import com.example.project.ShowMore_User_Fragment;
import com.example.project.helpers.TinyDB;
import com.example.project.interfaces.OnFragmentSwitchListener;

public class ViewPager2Adapter extends FragmentStateAdapter {

    private final Context context;
    private TinyDB tinyDB;
    boolean hasShopAddress;

    public ViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context) {
        super(fragmentManager, lifecycle);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        tinyDB = new TinyDB(context);
        hasShopAddress = tinyDB.getAll().containsKey("addressShop");
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
    private void showErrorDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_error, null);
        Button errorClose = view.findViewById(R.id.errorClose);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        errorClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

}
