package com.studentportal.config;

import com.studentportal.model.*;
import com.studentportal.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;

@Configuration
public class SeedData {
    @Bean CommandLineRunner loadDemoData(StudentRepository students, MentorRepository mentors, ProjectRepository projects, MilestoneRepository milestones, TeamRepository teams) {
        return args -> { if (students.count() > 0) return;
            Student a = students.save(new Student("Aarav Sharma", "aarav@example.com", "React, Java, MySQL"));
            Student b = students.save(new Student("Maya Patel", "maya@example.com", "Python, UI/UX, Figma"));
            students.save(new Student("Rohan Gupta", "rohan@example.com", "Android, Kotlin, Firebase"));
            mentors.save(new Mentor("Dr. Neha Rao", "React, JavaScript, UI/UX", "neha.rao@college.edu"));
            mentors.save(new Mentor("Prof. Vikram Sen", "Java, Spring Boot, MySQL, REST APIs", "vikram.sen@college.edu"));
            mentors.save(new Mentor("Anita Bose", "Python, Machine Learning, Data Science", "anita.bose@college.edu"));
            Project p1 = projects.save(new Project("Campus Carpool", "A web platform that helps students find safe carpool partners travelling along similar routes.", "React, Spring Boot, MySQL", "Web Development", 4, a));
            Project p2 = projects.save(new Project("Study Buddy", "An intelligent study-planning app with shared focus sessions and revision reminders.", "Python, UI/UX, Firebase", "Education", 3, b));
            teams.save(new Team("Route Rovers", p1)); teams.save(new Team("Focus Collective", p2));
            milestones.save(new Milestone(p1, "Create wireframes", java.time.LocalDate.now().plusDays(7)));
            milestones.save(new Milestone(p1, "Build API prototype", java.time.LocalDate.now().plusDays(21)));
            milestones.save(new Milestone(p2, "Research user needs", java.time.LocalDate.now().plusDays(5)));
        }; }
}
