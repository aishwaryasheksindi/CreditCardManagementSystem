package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnomalyDetectionResponseDto {

    private String customerId;
    private LocalDateTime generatedAt;

    @JsonProperty("flags")
    @JsonAlias({"anomalies", "anomalyFlags"})
    private List<AnomalyFlag> flags = new ArrayList<>();

    public AnomalyDetectionResponseDto() {
    }

    public AnomalyDetectionResponseDto(String customerId, LocalDateTime generatedAt, List<AnomalyFlag> flags) {
        this.customerId = customerId;
        this.generatedAt = generatedAt;
        this.flags = flags != null ? flags : new ArrayList<>();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<AnomalyFlag> getFlags() {
        return flags;
    }

    public void setFlags(List<AnomalyFlag> flags) {
        this.flags = flags != null ? flags : new ArrayList<>();
    }

    public List<AnomalyFlag> getAnomalies() {
        return flags;
    }

    public void setAnomalies(List<AnomalyFlag> anomalies) {
        this.flags = anomalies != null ? anomalies : new ArrayList<>();
    }
}
