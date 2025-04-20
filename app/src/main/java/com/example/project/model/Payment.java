package com.example.project.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Payment implements Serializable {
    private User user;
    private String addressUser;
    private AddressShop shop;
    private String fullName;
    private String sdt;
    private String note;
    private Boolean utensils;
    private Long totalPrice;
    private LocalDateTime orderDate;
    private List<PaymentProduct> products;
    private String paymentMethod;

    public Payment(User user, String addressUser, AddressShop shop, String fullName, String sdt, String note, Boolean utensils, Long totalPrice, LocalDateTime orderDate, List<PaymentProduct> products, String paymentMethod) {
        this.user = user;
        this.addressUser = addressUser;
        this.shop = shop;
        this.fullName = fullName;
        this.sdt = sdt;
        this.note = note;
        this.utensils = utensils;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.products = products;
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AddressShop getShop() {
        return shop;
    }

    public void setShop(AddressShop shop) {
        this.shop = shop;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

}
