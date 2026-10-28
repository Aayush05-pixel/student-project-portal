package com.studentportal.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Milestone {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private Project project;
    private String title;
    private LocalDate dueDate;
    private String status = "Not Started";
    public Milestone() {}
    public Milestone(Project project, String title, LocalDate dueDate) { this.project = project; this.title = title; this.dueDate = dueDate; }
    public Long getId() { return id; } public Project getProject() { return project; } public String getTitle() { return title; }
    public LocalDate getDueDate() { return dueDate; } public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
