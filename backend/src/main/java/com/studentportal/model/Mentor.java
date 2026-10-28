package com.studentportal.model;

import jakarta.persistence.*;

@Entity
public class Mentor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    private String expertise;
    private String email;
    public Mentor() {}
    public Mentor(String name, String expertise, String email) { this.name = name; this.expertise = expertise; this.email = email; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getExpertise() { return expertise; }
    public String getEmail() { return email; }
}
