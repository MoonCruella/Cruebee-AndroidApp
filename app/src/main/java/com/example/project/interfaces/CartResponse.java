package com.example.project.interfaces;

import com.example.project.model.Food;

import org.json.JSONException;

import java.util.ArrayList;

public interface CartResponse {
    void onSuccess(ArrayList<Food> cartList) throws JSONException;
    void onError(String errorMessage);
}
