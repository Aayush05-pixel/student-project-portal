package com.studentportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TeamJoinRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private Project project;
    private String studentName;
    private String studentEmail;
    @Column(length = 1000) private String message;
    private String status = "Pending";
    private LocalDateTime createdAt = LocalDateTime.now();
    public TeamJoinRequest() {}
    public TeamJoinRequest(Project project, String studentName, String studentEmail, String message) { this.project = project; this.studentName = studentName; this.studentEmail = studentEmail; this.message = message; }
    public Long getId() { return id; } public Project getProject() { return project; } public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; } public String getMessage() { return message; } public String getStatus() { return status; } public LocalDateTime getCreatedAt() { return createdAt; }
}
