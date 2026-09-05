package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.Size;

public class KycRejectRequestDto {

    @Size(max = 1000, message = "Rejection reason must not exceed 1000 characters")
    private String rejectionReason;

    public KycRejectRequestDto() {
    }

    public KycRejectRequestDto(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
