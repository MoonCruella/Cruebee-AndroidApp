package com.example.project.model;

import android.os.Parcelable;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Food implements Serializable {
    private int id;
    private String name;
    private String description;
    private int price;
    private int soldCount;
    private boolean active;
    private LocalDateTime generatedTime;

    private int numberInCart;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(LocalDateTime generatedTime) {
        this.generatedTime = generatedTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }

    public Food(int id, String name, String description, int price, int soldCount, boolean active, LocalDateTime generatedTime, String image){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.soldCount = soldCount;
        this.active = active;
        this.generatedTime = generatedTime;
        this.image = image;
    }

    public Food(int id,String name,int price,String image,String description,int numberInCart){
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
        this.numberInCart = numberInCart;
    }
    public Food(int id,String name,int price,String image,String description){
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
    }


}
