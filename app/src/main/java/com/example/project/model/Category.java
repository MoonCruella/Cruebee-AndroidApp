package com.example.project.model;

import java.util.List;

public class Category {
    private int id;
    private String name;
    private String image;
    private boolean active;
    private List<Food> foods;

    public Category(int id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }
    public Category(String name, List<Food> foods) {
        this.name = name;
        this.foods = foods;
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

    public void setFoods(List<Food> foods) {
        this.foods = foods;
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

    public List<Food>getFoods () {
        return foods;
    }
}
