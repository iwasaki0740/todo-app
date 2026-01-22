package com.example.schedule_app.controller;

public class UserForm {
    private String user;  // ← フロントから送られる "user" と一致

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}