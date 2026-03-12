package com.github.nathilang.task_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTaskDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    @NotNull(message = "Completed flag is required")
    private boolean completed;

    // getters and setters
}
