package com.studentportal.dto;

import jakarta.validation.constraints.NotBlank;

/** The approval desk may publish a project or reject it. */
public class ApprovalRequest {
    @NotBlank(message = "Approval decision is required")
    public String decision;
}
