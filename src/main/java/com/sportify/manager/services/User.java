package com.sportify.manager.services;

public class User {
    private String id;
    private String pwd;
    private String name;
    private String email;
    private String role;

    // Constructeur complet pour le login et la gestion
    public User(String id, String pwd, String name, String email, String role) {
        this.id = id;
        this.pwd = pwd;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Getters
    public String getId() { return id; }
    public String getPwd() { return pwd; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}