package com.example.project.model;

import java.io.Serializable;

public class PaymentProduct implements Serializable {
    private Food product;
    private int quantity;

    public PaymentProduct(Food product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public PaymentProduct() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Food getProduct() {
        return product;
    }

    public void setProduct(Food product) {
        this.product = product;
    }
}
