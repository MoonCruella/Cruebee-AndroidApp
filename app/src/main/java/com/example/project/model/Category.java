package com.example.project.model;

import java.util.List;

public class Category {
    private int id;
    private String name;
    private String image;
    private boolean active;
    private List<Product> products;

    public Category(int id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
        //this.products = products;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
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

    public String getImage() {
        return image;
    }

    public boolean isActive() {
        return active;
    }

    public List<Product> getProducts() {
        return products;
    }
}
