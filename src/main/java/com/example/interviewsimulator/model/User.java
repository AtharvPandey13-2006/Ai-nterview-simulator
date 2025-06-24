package com.example.interviewsimulator.model;

public class User {
    private String email;
    private String name;

    // Add more fields if needed

    public User() {}

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
