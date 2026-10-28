package com.studentportal.controller;

import com.studentportal.dto.*;
import com.studentportal.model.*;
import com.studentportal.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class PortalController {
    private final ProjectRepository projects; private final StudentRepository students; private final MentorRepository mentors;
    private final JoinRequestRepository requests; private final MilestoneRepository milestones; private final TeamRepository teams;
    public PortalController(ProjectRepository projects, StudentRepository students, MentorRepository mentors, JoinRequestRepository requests, MilestoneRepository milestones, TeamRepository teams) {
        this.projects = projects; this.students = students; this.mentors = mentors; this.requests = requests; this.milestones = milestones; this.teams = teams;
    }
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return Map.of("projectCount", projects.count(), "studentCount", students.count(), "teamCount", teams.count(), "requestCount", requests.count(), "milestoneCount", milestones.count(), "recentProjects", projects.findAllByOrderByCreatedAtDesc().stream().limit(4).toList());
    }
    @GetMapping("/students") public List<Student> students() { return students.findAll(); }
    @GetMapping("/projects")
    public List<Project> listProjects(@RequestParam(required = false) String q, @RequestParam(required = false) String status) {
        return projects.findAllByOrderByCreatedAtDesc().stream().filter(p -> status == null || status.isBlank() || p.getStatus().equalsIgnoreCase(status))
            .filter(p -> q == null || q.isBlank() || (p.getTitle()+" "+p.getDescription()+" "+p.getSkills()+" "+p.getCategory()).toLowerCase().contains(q.toLowerCase())).toList();
    }
    @PostMapping("/projects")
    public ResponseEntity<Project> createProject(@Valid @RequestBody CreateProjectRequest body) {
        Student owner = students.findById(body.ownerId).orElseThrow(() -> new NoSuchElementException("Project owner was not found"));
        return ResponseEntity.status(HttpStatus.CREATED).body(projects.save(new Project(body.title.trim(), body.description.trim(), body.skills.trim(), body.category.trim(), body.maxMembers, owner)));
    }
    @GetMapping("/projects/{id}")
    public Map<String, Object> project(@PathVariable Long id) {
        Project project = getProject(id); return Map.of("project", project, "milestones", milestones.findByProjectIdOrderByDueDateAsc(id));
    }
    @PostMapping("/join-requests")
    public ResponseEntity<TeamJoinRequest> join(@Valid @RequestBody CreateJoinRequest body) {
        Project p = getProject(body.projectId);
        if (!"Open".equalsIgnoreCase(p.getStatus())) throw new IllegalArgumentException("This project is not currently accepting new members");
        return ResponseEntity.status(HttpStatus.CREATED).body(requests.save(new TeamJoinRequest(p, body.studentName.trim(), body.studentEmail.trim(), body.message.trim())));
    }
    @GetMapping("/mentors/recommendations")
    public List<Mentor> recommendedMentors(@RequestParam(defaultValue = "") String skills) {
        Set<String> wanted = Arrays.stream(skills.toLowerCase().split(",")).map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toSet());
        return mentors.findAll().stream().filter(m -> wanted.isEmpty() || wanted.stream().anyMatch(skill -> m.getExpertise().toLowerCase().contains(skill))).toList();
    }
    @PostMapping("/milestones")
    public ResponseEntity<Milestone> addMilestone(@Valid @RequestBody CreateMilestoneRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(milestones.save(new Milestone(getProject(body.projectId), body.title.trim(), body.dueDate)));
    }
    @PatchMapping("/milestones/{id}/status")
    public Milestone updateMilestone(@PathVariable Long id, @Valid @RequestBody StatusRequest body) {
        Set<String> valid = Set.of("Not Started", "In Progress", "Completed");
        if (!valid.contains(body.status)) throw new IllegalArgumentException("Choose a valid milestone status");
        Milestone milestone = milestones.findById(id).orElseThrow(() -> new NoSuchElementException("Milestone was not found")); milestone.setStatus(body.status); return milestones.save(milestone);
    }
    private Project getProject(Long id) { return projects.findById(id).orElseThrow(() -> new NoSuchElementException("Project was not found")); }
}
