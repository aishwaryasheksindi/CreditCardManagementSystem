package com.crimsonlogic.creditcardmanagementsystem.dto;

import com.crimsonlogic.creditcardmanagementsystem.enums.DisputeType;

public class DisputeClassificationResponseDto {

    private DisputeType suggestedType;
    private String confidence;
    private String explanation;

    public DisputeClassificationResponseDto() {
    }

    public DisputeClassificationResponseDto(DisputeType suggestedType, String confidence, String explanation) {
        this.suggestedType = suggestedType;
        this.confidence = confidence;
        this.explanation = explanation;
    }

    public DisputeType getSuggestedType() {
        return suggestedType;
    }

    public void setSuggestedType(DisputeType suggestedType) {
        this.suggestedType = suggestedType;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
