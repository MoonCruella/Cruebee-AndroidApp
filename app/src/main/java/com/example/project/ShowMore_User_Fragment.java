package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.project.helpers.TinyDB;
import com.example.project.model.User;

public class ShowMore_User_Fragment extends Fragment {
    private ConstraintLayout supportBtn,verifyBtn,settingsBtn,couponBtn,specialBtn,addressBtn,orderBtn;
    private TextView usernameTxt;
    private TinyDB tinyDB;
    private LinearLayout editAccountBtn;

    public ShowMore_User_Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_more_user, container, false);
        init(view);
        return view;
    }
    private void init(View view){
        supportBtn = view.findViewById(R.id.supportBtn);
        verifyBtn =  view.findViewById(R.id.verifyBtn);
        settingsBtn = view.findViewById(R.id.settingsBtn);
        couponBtn =  view.findViewById(R.id.couponBtn);
        specialBtn = view.findViewById(R.id.specialBtn);
        addressBtn = view.findViewById(R.id.addressBtn);
        orderBtn = view.findViewById(R.id.orderBtn);
        usernameTxt = view.findViewById(R.id.usernameTxt);
        editAccountBtn = view.findViewById(R.id.editAccountBtn);


        editAccountBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(),EditAccountActivity.class)));

        tinyDB = new TinyDB(getContext());
        User user = tinyDB.getObject("savedUser", User.class);
        String uname = user.getUsername();
        usernameTxt.setText(uname);

        verifyBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), PolicyActivity.class)));
        supportBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), SupportActivity.class)));
        couponBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), CouponActivity.class)));
        specialBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), AdvantageActivity.class)));
        settingsBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(),SettingUserActivity.class)));
        addressBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(),DeliveryAddressActivity.class)));
        orderBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(),OrderListActivity.class)));
    }

}