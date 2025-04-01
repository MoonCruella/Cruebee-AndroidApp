package com.example.project.model;

import java.time.LocalDateTime;
import java.util.List;

public class Payment {
    private String addressUser;
    private String addressShop;
    private String firstName;
    private String lastName;
    private String sdt;
    private String note;
    private Boolean utensils;
    private Long totalPrice;
    private LocalDateTime receivedDate;
    private LocalDateTime orderDate;
    private List<PaymentProduct> products;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAddressUser() {
        return addressUser;
    }

    public void setAddressUser(String addressUser) {
        this.addressUser = addressUser;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<PaymentProduct> getProducts() {
        return products;
    }

    public void setProducts(List<PaymentProduct> products) {
        this.products = products;
    }

    public String getAddressShop() {
        return addressShop;
    }

    public void setAddressShop(String addressShop) {
        this.addressShop = addressShop;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public Boolean getUtensils() {
        return utensils;
    }

    public void setUtensils(Boolean utensils) {
        this.utensils = utensils;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }
}
