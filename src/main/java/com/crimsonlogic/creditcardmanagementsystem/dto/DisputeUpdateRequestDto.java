package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.DisputeStatus;
import jakarta.validation.constraints.Size;

public class DisputeUpdateRequestDto {

    private DisputeStatus status;

    @Size(max = 100, message = "Investigator staff ID must not exceed 100 characters")
    private String investigatorStaffId;

    @Size(max = 1000, message = "Resolution must not exceed 1000 characters")
    private String resolution;

    public DisputeUpdateRequestDto() {
    }

    public DisputeUpdateRequestDto(DisputeStatus status, String investigatorStaffId, String resolution) {
        this.status = status;
        this.investigatorStaffId = investigatorStaffId;
        this.resolution = resolution;
    }

    public DisputeStatus getStatus() {
        return status;
    }

    public void setStatus(DisputeStatus status) {
        this.status = status;
    }

    public String getInvestigatorStaffId() {
        return investigatorStaffId;
    }

    public void setInvestigatorStaffId(String investigatorStaffId) {
        this.investigatorStaffId = investigatorStaffId;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
