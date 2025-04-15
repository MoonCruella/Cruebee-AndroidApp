package com.example.project.model;

public class AddressShop {
    private int id;
    private String name;
    private String address;
    private String openTime;
    private String phone;
    private int active;
    private double latitude;
    private double longitude;
    private double distance;

    public AddressShop(int id, String name, String address, String openTime, String phone, int active, double latitude, double longitude, double distance) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openTime = openTime;
        this.phone = phone;
        this.active = active;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public AddressShop(int id, String name, String address, String openTime, String phone, double distance) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openTime = openTime;
        this.phone = phone;
        this.distance = distance;
    }

    public AddressShop(int id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
