package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class DisputeClassificationRequestDto {

    @NotBlank(message = "Dispute description is required")
    private String description;

    public DisputeClassificationRequestDto() {
    }

    public DisputeClassificationRequestDto(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
