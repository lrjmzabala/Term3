package com.mycompany.motorphpayroll.model;

public class User {
    private String username;
    private String password;
    private String role; // e.g., "Admin", "Employee"

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Optional: Add setters if these properties can be changed after creation
    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
