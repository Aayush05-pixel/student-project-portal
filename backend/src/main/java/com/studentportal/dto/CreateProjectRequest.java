package com.studentportal.dto;

import jakarta.validation.constraints.*;

public class CreateProjectRequest {
    @NotBlank(message = "Project title is required") @Size(max = 100) public String title;
    @NotBlank(message = "Description is required") @Size(min = 20, max = 2000, message = "Description must be 20–2000 characters") public String description;
    @NotBlank(message = "Add at least one skill") @Size(max = 250) public String skills;
    @NotBlank(message = "Category is required") public String category;
    @Min(value = 2, message = "A team needs at least 2 members") @Max(value = 10, message = "Maximum team size is 10") public int maxMembers;
    @NotNull(message = "Choose a project owner") public Long ownerId;
}
