package com.studentportal.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class CreateMilestoneRequest {
    @NotNull(message = "Project is required") public Long projectId;
    @NotBlank(message = "Milestone title is required") @Size(max = 120) public String title;
    @NotNull(message = "Due date is required") @FutureOrPresent(message = "Due date cannot be in the past") public LocalDate dueDate;
}
