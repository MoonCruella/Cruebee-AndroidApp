package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.project.R;


public class ShowMoreFragment extends Fragment {

    private ConstraintLayout supportBtn,verifyBtn,settingsBtn,orderBtn;
    private LinearLayout btn_login;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_more, container, false);
        init(view);
        return view;
    }
    private void init(View view){
        supportBtn = view.findViewById(R.id.supportBtn);
        verifyBtn = view.findViewById(R.id.verifyBtn);
        settingsBtn = view.findViewById(R.id.settingsBtn);
        orderBtn = view.findViewById(R.id.orderBtn);
        btn_login = view.findViewById(R.id.btn_login);
        orderBtn.setOnClickListener(v -> startActivity(new Intent(getContext(),OrderListActivity.class)));
        btn_login.setOnClickListener(v -> startActivity(new Intent(getContext(),LoginActivity.class)));
        supportBtn.setOnClickListener(v -> startActivity(new Intent(getContext(),SupportActivity.class)));
        verifyBtn.setOnClickListener(v -> startActivity(new Intent(getContext(),PolicyActivity.class)));
        settingsBtn.setOnClickListener(v -> startActivity(new Intent(getContext(),SettingActivity.class)));

    }

}