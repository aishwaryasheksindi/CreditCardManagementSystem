package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.Size;

public class KycRejectRequestDto {

    private String verifiedByStaffId;

    @Size(max = 1000, message = "Rejection reason must not exceed 1000 characters")
    private String rejectionReason;

    public KycRejectRequestDto() {
    }

    public KycRejectRequestDto(String verifiedByStaffId, String rejectionReason) {
        this.verifiedByStaffId = verifiedByStaffId;
        this.rejectionReason = rejectionReason;
    }

    public String getVerifiedByStaffId() {
        return verifiedByStaffId;
    }

    public void setVerifiedByStaffId(String verifiedByStaffId) {
        this.verifiedByStaffId = verifiedByStaffId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
