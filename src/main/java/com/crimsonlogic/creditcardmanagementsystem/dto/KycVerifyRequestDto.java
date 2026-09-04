package com.crimsonlogic.creditcardmanagementsystem.dto;

public class KycVerifyRequestDto {

    private String verifiedByStaffId;

    public KycVerifyRequestDto() {
    }

    public KycVerifyRequestDto(String verifiedByStaffId) {
        this.verifiedByStaffId = verifiedByStaffId;
    }

    public String getVerifiedByStaffId() {
        return verifiedByStaffId;
    }

    public void setVerifiedByStaffId(String verifiedByStaffId) {
        this.verifiedByStaffId = verifiedByStaffId;
    }
}
