package com.example.project.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Address implements Serializable {
    private int id;

    private int is_primary;
    private String  address_details;
    private double latitude;
    private double longitude;
    private int userId;
    private String username;
    private String note;
    private String sdt;


    public Address(int id, int is_primary, String address_details, double latitude, double longitude, int userId,String username,String note,String sdt) {
        this.id = id;
        this.is_primary = is_primary;
        this.address_details = address_details;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.username = username;
        this.note = note;
        this.sdt = sdt;
    }

    public Address(int userId, double latitude, double longitude, String address_details, int is_primary,String username,String note, String sdt) {
        this.userId = userId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address_details = address_details;
        this.is_primary = is_primary;
        this.username = username;
        this.note = note;
        this.sdt = sdt;
    }
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
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
        jsonObject.put("id",this.id);
        jsonObject.put("userId", this.userId);
        jsonObject.put("address_details", this.address_details);
        jsonObject.put("latitude", this.latitude);
        jsonObject.put("longitude", this.longitude);
        jsonObject.put("is_primary", this.is_primary);
        jsonObject.put("sdt",this.sdt);
        jsonObject.put("note",this.note);
        jsonObject.put("username",this.username);
        Log.d("JSON OBJECT :",jsonObject.toString());
        return jsonObject;
    }


    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", is_primary=" + is_primary +
                ", address_details='" + address_details + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", note='" + note + '\'' +
                ", sdt='" + sdt + '\'' +
                '}';
    }
}
