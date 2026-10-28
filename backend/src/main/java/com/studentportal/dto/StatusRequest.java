package com.studentportal.dto;

import jakarta.validation.constraints.NotBlank;
public class StatusRequest { @NotBlank(message = "Status is required") public String status; }
