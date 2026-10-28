package com.studentportal.dto;

import jakarta.validation.constraints.*;

public class CreateJoinRequest {
    @NotNull(message = "Project is required") public Long projectId;
    @NotBlank(message = "Your name is required") @Size(max = 80) public String studentName;
    @NotBlank(message = "Email is required") @Email(message = "Enter a valid email") public String studentEmail;
    @NotBlank(message = "Please add a short message") @Size(min = 10, max = 1000, message = "Message must be 10–1000 characters") public String message;
}
