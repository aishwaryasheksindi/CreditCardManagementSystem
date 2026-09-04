package com.crimsonlogic.creditcardmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class FraudAlertRequestDto {

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    private String riskScoreId;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "OPEN|INVESTIGATING|CONFIRMED|FALSE_POSITIVE|CLOSED",
            message = "Status must be OPEN, INVESTIGATING, CONFIRMED, FALSE_POSITIVE, or CLOSED")
    private String status;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String investigatorStaffId;

    public FraudAlertRequestDto() {
    }

    public FraudAlertRequestDto(String transactionId,
                                String riskScoreId,
                                String status,
                                String reason,
                                String investigatorStaffId) {
        this.transactionId = transactionId;
        this.riskScoreId = riskScoreId;
        this.status = status;
        this.reason = reason;
        this.investigatorStaffId = investigatorStaffId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getRiskScoreId() {
        return riskScoreId;
    }

    public void setRiskScoreId(String riskScoreId) {
        this.riskScoreId = riskScoreId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getInvestigatorStaffId() {
        return investigatorStaffId;
    }

    public void setInvestigatorStaffId(String investigatorStaffId) {
        this.investigatorStaffId = investigatorStaffId;
    }
}
