package com.crimsonlogic.creditcardmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_alerts")
public class FraudAlert {

    @Id
    private String fraudAlertId;

    @Column(nullable = false)
    private String transactionId;

    private String riskScoreId;

    @Column(nullable = false)
    private String status;

    @Column(length = 1000, nullable = false)
    private String reason;

    private String investigatorStaffId;

    @Column(nullable = false)
    private LocalDateTime raisedAt;

    private LocalDateTime closedAt;

    public FraudAlert() {
    }

    public FraudAlert(String fraudAlertId,
                      String transactionId,
                      String riskScoreId,
                      String status,
                      String reason,
                      String investigatorStaffId,
                      LocalDateTime raisedAt,
                      LocalDateTime closedAt) {
        this.fraudAlertId = fraudAlertId;
        this.transactionId = transactionId;
        this.riskScoreId = riskScoreId;
        this.status = status;
        this.reason = reason;
        this.investigatorStaffId = investigatorStaffId;
        this.raisedAt = raisedAt;
        this.closedAt = closedAt;
    }

    public String getFraudAlertId() {
        return fraudAlertId;
    }

    public void setFraudAlertId(String fraudAlertId) {
        this.fraudAlertId = fraudAlertId;
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

    public LocalDateTime getRaisedAt() {
        return raisedAt;
    }

    public void setRaisedAt(LocalDateTime raisedAt) {
        this.raisedAt = raisedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}
