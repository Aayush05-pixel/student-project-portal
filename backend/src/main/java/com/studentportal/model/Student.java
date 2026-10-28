package com.studentportal.model;

import jakarta.persistence.*;

@Entity
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    @Column(unique = true) private String email;
    private String skills;
    public Student() {}
    public Student(String name, String email, String skills) { this.name = name; this.email = email; this.skills = skills; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSkills() { return skills; }
}
