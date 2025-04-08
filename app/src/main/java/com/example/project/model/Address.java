package com.example.project.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Address {
    private int id;
    private int is_primary;
    private String  address_details;
    private double latitude;
    private double longitude;
    private int userId;

    public Address(int id, int is_primary, String address_details, double latitude, double longitude, int userId) {
        this.id = id;
        this.is_primary = is_primary;
        this.address_details = address_details;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
    }

    public Address(int userId, double latitude, double longitude, String address_details, int is_primary) {
        this.userId = userId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address_details = address_details;
        this.is_primary = is_primary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIs_primary() {
        return is_primary;
    }

    public void setIs_primary(int is_primary) {
        this.is_primary = is_primary;
    }

    public String getAddress_details() {
        return address_details;
    }

    public void setAddress_details(String address_details) {
        this.address_details = address_details;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", this.userId);
        jsonObject.put("address_details", this.address_details);
        jsonObject.put("latitude", this.latitude);
        jsonObject.put("longitude", this.longitude);
        jsonObject.put("is_primary", this.is_primary);
        return jsonObject;
    }


}
