package com.example.interviewsimulator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "logins")
public class LoginInfo {

    @Id
    private String id;

    private String name;
    private String email;
    private LocalDateTime loginTime;

    public LoginInfo() {}

    public LoginInfo(String name, String email, LocalDateTime loginTime) {
        this.name = name;
        this.email = email;
        this.loginTime = loginTime;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
}
