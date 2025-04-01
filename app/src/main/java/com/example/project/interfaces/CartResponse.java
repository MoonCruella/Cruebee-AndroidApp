package com.example.project.interfaces;

import com.example.project.model.Food;

import java.util.ArrayList;

public interface CartResponse {
    void onSuccess(ArrayList<Food> cartList);
    void onError(String errorMessage);
}
