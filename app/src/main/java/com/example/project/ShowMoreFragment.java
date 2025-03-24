package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class ShowMoreFragment extends Fragment {

    ConstraintLayout supportBtn,verifyBtn,newsBtn,settingsBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_more, container, false);
        init(view);
        return view;
    }
    private void init(View view){
        supportBtn = (ConstraintLayout) view.findViewById(R.id.supportBtn);
        verifyBtn = (ConstraintLayout) view.findViewById(R.id.verifyBtn);
        newsBtn = (ConstraintLayout) view.findViewById(R.id.newsBtn);
        settingsBtn = (ConstraintLayout) view.findViewById(R.id.settingsBtn);

        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportBtn.getBackground().setAlpha(245);
            }
        });

        supportBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                supportBtn.getBackground().setAlpha(245);
                return true;
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyBtn.getBackground().setAlpha(245);
            }
        });

        verifyBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                verifyBtn.getBackground().setAlpha(245);
                return true;
            }
        });
    }

}