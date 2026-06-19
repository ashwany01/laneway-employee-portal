package com.laneway.employee_portal.controller;

public class LoginResponse {

    private String token;
    private String name;
    private String accessRole;

    public LoginResponse(String token, String name, String accessRole) {
        this.token = token;
        this.name = name;
        this.accessRole = accessRole;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessRole() {
        return accessRole;
    }

    public void setAccessRole(String accessRole) {
        this.accessRole = accessRole;
    }
}