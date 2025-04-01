package com.example.project.interfaces;

import org.json.JSONException;

public interface TotalFeeResponse {
    void onSuccess(int totalFee) throws JSONException;
    void onError(String errorMessage);
}
