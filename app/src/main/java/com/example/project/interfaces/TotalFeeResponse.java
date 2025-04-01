package com.example.project.interfaces;

public interface TotalFeeResponse {
    void onSuccess(int totalFee);
    void onError(String errorMessage);
}
