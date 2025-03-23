package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomNavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomNavigationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomNavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomNavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomNavigationFragment newInstance(String param1, String param2) {
        BottomNavigationFragment fragment = new BottomNavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false);
        // Inflate the layout for this fragment
        view.findViewById(R.id.cartButton).setOnClickListener(v -> openCartActivity(view));
        view.findViewById(R.id.menuButton).setOnClickListener(v -> openMenuActivity(view));
        view.findViewById(R.id.homeButton).setOnClickListener(v -> openHomeActivity(view));
        view.findViewById(R.id.moreButton).setOnClickListener(v -> openShowMoreActivity(view));
        view.findViewById(R.id.discountButton).setOnClickListener(v -> openDiscountActivity(view));
        return view;
    }
    public void openCartActivity(View view) {
        CartDialog cartDialog = new CartDialog(view.getContext());
        cartDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        cartDialog.show();
    }
    public void openMenuActivity(View view){
        Intent intent = new Intent(getActivity(),MenuActivity.class);
        startActivity(intent);
    }
    public void openHomeActivity(View view){
        Intent intent = new Intent(getActivity(),HomeActivity.class);
        startActivity(intent);
    }
    public void openShowMoreActivity(View view){
        Intent intent = new Intent(getActivity(),ShowMoreAcitivity.class);
        startActivity(intent);
    }
    public void openDiscountActivity(View view){
        Intent intent = new Intent(getActivity(),DiscountActivity.class);
        startActivity(intent);
    }

}