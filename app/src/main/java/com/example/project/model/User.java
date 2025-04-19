package com.example.project.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private String email;
    private String role;
    private String sdt;
    private String gender;
    private String otp;
    private boolean active;
    private LocalDateTime optGeneratedTime;

    public User(int id) {
        this.id = id;
    }

    public User(int id, String username, String password, String email, String sdt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.sdt = sdt;
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getOptGeneratedTime() {
        return optGeneratedTime;
    }

    public void setOptGeneratedTime(LocalDateTime optGeneratedTime) {
        this.optGeneratedTime = optGeneratedTime;
    }
}
