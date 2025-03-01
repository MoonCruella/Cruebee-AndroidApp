package com.example.project.model;

import java.util.List;

public class Category {
    private int id;
    private String name;
    private int image;
    private boolean active;
    private List<Product> products;

    public Category(int id, String name, int image, boolean active) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.active = active;
        //this.products = products;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public boolean isActive() {
        return active;
    }

    public List<Product> getProducts() {
        return products;
    }
}
