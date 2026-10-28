package com.studentportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String title;
    @Column(length = 2000) private String description;
    private String skills;
    private String category;
    private String status = "Open";
    private int maxMembers;
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne(optional = false) private Student owner;
    public Project() {}
    public Project(String title, String description, String skills, String category, int maxMembers, Student owner) {
        this.title = title; this.description = description; this.skills = skills; this.category = category; this.maxMembers = maxMembers; this.owner = owner;
    }
    public Long getId() { return id; } public String getTitle() { return title; } public String getDescription() { return description; }
    public String getSkills() { return skills; } public String getCategory() { return category; } public String getStatus() { return status; }
    public int getMaxMembers() { return maxMembers; } public LocalDateTime getCreatedAt() { return createdAt; } public Student getOwner() { return owner; }
    public void setStatus(String status) { this.status = status; }
}
