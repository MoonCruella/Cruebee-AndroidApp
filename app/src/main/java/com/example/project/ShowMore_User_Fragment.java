package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.project.helpers.TinyDB;
import com.example.project.model.User;
import com.google.android.material.card.MaterialCardView;

public class ShowMore_User_Fragment extends Fragment {
    ConstraintLayout supportBtn,verifyBtn,settingsBtn,couponBtn,specialBtn,addressBtn,orderBtn;
    TextView usernameTxt;
    TinyDB tinyDB;
    LinearLayout editAccountBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_more_user, container, false);
        init(view);
        return view;
    }
    private void init(View view){
        supportBtn = (ConstraintLayout) view.findViewById(R.id.supportBtn);
        verifyBtn = (ConstraintLayout) view.findViewById(R.id.verifyBtn);
        settingsBtn = (ConstraintLayout) view.findViewById(R.id.settingsBtn);
        couponBtn = (ConstraintLayout) view.findViewById(R.id.couponBtn);
        specialBtn = (ConstraintLayout) view.findViewById(R.id.specialBtn);
        addressBtn = (ConstraintLayout) view.findViewById(R.id.addressBtn);
        orderBtn = (ConstraintLayout) view.findViewById(R.id.orderBtn);
        usernameTxt = view.findViewById(R.id.usernameTxt);
        editAccountBtn = view.findViewById(R.id.editAccountBtn);


        editAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),EditAccountActivity.class));
            }
        });

        tinyDB = new TinyDB(getContext());
        User user = tinyDB.getObject("savedUser", User.class);
        String uname = user.getUsername();
        usernameTxt.setText(uname);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verifyBtn.getBackground().setAlpha(245);
                startActivity(new Intent(getActivity(), PolicyActivity.class));
            }
        });

        verifyBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                verifyBtn.getBackground().setAlpha(245);
                return false;
            }
        });
        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verifyBtn.getBackground().setAlpha(245);
                startActivity(new Intent(getActivity(), SupportActivity.class));
            }
        });
        couponBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verifyBtn.getBackground().setAlpha(245);
                startActivity(new Intent(getActivity(), CouponActivity.class));
            }
        });
        specialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verifyBtn.getBackground().setAlpha(245);
                startActivity(new Intent(getActivity(), AdvantageActivity.class));
            }
        });
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),SettingUserActivity.class));
            }
        });

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),DeliveryAddressActivity.class));
            }
        });

    }

}