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
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://student-project-portal-eta.vercel.app"
})
public class PortalController {

    private final ProjectRepository projects;
    private final StudentRepository students;
    private final MentorRepository mentors;
    private final JoinRequestRepository requests;
    private final MilestoneRepository milestones;
    private final TeamRepository teams;

    public PortalController(
            ProjectRepository projects,
            StudentRepository students,
            MentorRepository mentors,
            JoinRequestRepository requests,
            MilestoneRepository milestones,
            TeamRepository teams
    ) {
        this.projects = projects;
        this.students = students;
        this.mentors = mentors;
        this.requests = requests;
        this.milestones = milestones;
        this.teams = teams;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        long pendingCount = projects.findAll().stream()
                .filter(this::isPendingApproval)
                .count();

        return Map.of(
                "projectCount", projects.count(),
                "studentCount", students.count(),
                "teamCount", teams.count(),
                "requestCount", requests.count(),
                "milestoneCount", milestones.count(),
                "pendingCount", pendingCount,
                "recentProjects", projects.findAllByOrderByCreatedAtDesc()
                        .stream()
                        .limit(4)
                        .toList()
        );
    }

    @GetMapping("/students")
    public List<Student> students() {
        return students.findAll();
    }

    // Students can see only approved/open projects.
    @GetMapping("/projects")
    public List<Project> listProjects(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status
    ) {
        String selectedStatus = (status == null || status.isBlank())
                ? "Open"
                : status.trim();

        return projects.findAllByOrderByCreatedAtDesc().stream()
                .filter(project -> project.getStatus() != null
                        && project.getStatus().equalsIgnoreCase(selectedStatus))
                .filter(project -> q == null || q.isBlank()
                        || (project.getTitle() + " "
                        + project.getDescription() + " "
                        + project.getSkills() + " "
                        + project.getCategory())
                        .toLowerCase()
                        .contains(q.toLowerCase()))
                .toList();
    }

    // This MUST be before /projects/{id}.
    // It prevents Spring from trying to read "pending" as a numeric project ID.
    @GetMapping("/projects/pending")
    public List<Project> pendingProjects() {
        return projects.findAllByOrderByCreatedAtDesc().stream()
                .filter(this::isPendingApproval)
                .toList();
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> createProject(
            @Valid @RequestBody CreateProjectRequest body
    ) {
        Student owner = students.findById(body.ownerId)
                .orElseThrow(() -> new NoSuchElementException("Project owner was not found"));

        Project project = new Project(
                body.title.trim(),
                body.description.trim(),
                body.skills.trim(),
                body.category.trim(),
                body.maxMembers,
                owner
        );

        // Every new project first goes to the mentor/admin Approval Desk.
        project.setStatus("Pending");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projects.save(project));
    }

    @GetMapping("/projects/{id}")
    public Map<String, Object> project(@PathVariable Long id) {
        Project project = getProject(id);

        return Map.of(
                "project", project,
                "milestones", milestones.findByProjectIdOrderByDueDateAsc(id)
        );
    }

    // Frontend should call PATCH /api/projects/{id}/approval
    // with: { "status": "Open" } or { "status": "Rejected" }
    @PatchMapping("/projects/{id}/approval")
    public Project updateProjectApproval(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest body
    ) {
        String requestedStatus = body.status == null
                ? ""
                : body.status.trim().toLowerCase();

        String newStatus;

        if ("open".equals(requestedStatus)) {
            newStatus = "Open";
        } else if ("rejected".equals(requestedStatus)) {
            newStatus = "Rejected";
        } else {
            throw new IllegalArgumentException(
                    "Approval status must be Open or Rejected"
            );
        }

        Project project = getProject(id);

        if (!isPendingApproval(project)) {
            throw new IllegalArgumentException(
                    "Only pending projects can be approved or rejected"
            );
        }

        project.setStatus(newStatus);
        return projects.save(project);
    }

    @PostMapping("/join-requests")
    public ResponseEntity<TeamJoinRequest> join(
            @Valid @RequestBody CreateJoinRequest body
    ) {
        Project project = getProject(body.projectId);

        if (!"Open".equalsIgnoreCase(project.getStatus())) {
            throw new IllegalArgumentException(
                    "This project is not currently accepting new members"
            );
        }

        TeamJoinRequest request = new TeamJoinRequest(
                project,
                body.studentName.trim(),
                body.studentEmail.trim(),
                body.message.trim()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(requests.save(request));
    }

    @GetMapping("/mentors/recommendations")
    public List<Mentor> recommendedMentors(
            @RequestParam(defaultValue = "") String skills
    ) {
        Set<String> wantedSkills = Arrays.stream(skills.toLowerCase().split(","))
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .collect(Collectors.toSet());

        return mentors.findAll().stream()
                .filter(mentor -> wantedSkills.isEmpty()
                        || wantedSkills.stream().anyMatch(skill ->
                        mentor.getExpertise().toLowerCase().contains(skill)))
                .toList();
    }

    @PostMapping("/milestones")
    public ResponseEntity<Milestone> addMilestone(
            @Valid @RequestBody CreateMilestoneRequest body
    ) {
        Milestone milestone = new Milestone(
                getProject(body.projectId),
                body.title.trim(),
                body.dueDate
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(milestones.save(milestone));
    }

    @PatchMapping("/milestones/{id}/status")
    public Milestone updateMilestone(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest body
    ) {
        Set<String> validStatuses = Set.of(
                "Not Started",
                "In Progress",
                "Completed"
        );

        if (!validStatuses.contains(body.status)) {
            throw new IllegalArgumentException(
                    "Choose a valid milestone status"
            );
        }

        Milestone milestone = milestones.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Milestone was not found"
                ));

        milestone.setStatus(body.status);
        return milestones.save(milestone);
    }

    private boolean isPendingApproval(Project project) {
        return project.getStatus() != null
                && (project.getStatus().equalsIgnoreCase("Pending")
                || project.getStatus().equalsIgnoreCase("Pending Approval"));
    }

    private Project getProject(Long id) {
        return projects.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Project was not found"
                ));
    }
}