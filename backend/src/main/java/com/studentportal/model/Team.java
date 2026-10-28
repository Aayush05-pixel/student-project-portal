package com.studentportal.model;

import jakarta.persistence.*;

/** A team is attached to one project; members can be added as requests are accepted in a later extension. */
@Entity
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    @OneToOne(optional = false) private Project project;
    public Team() {}
    public Team(String name, Project project) { this.name = name; this.project = project; }
    public Long getId() { return id; } public String getName() { return name; } public Project getProject() { return project; }
}
