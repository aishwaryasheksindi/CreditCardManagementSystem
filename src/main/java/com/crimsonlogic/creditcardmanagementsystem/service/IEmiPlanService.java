package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.EmiPlanRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.EmiPlanResponseDto;

import java.util.List;

public interface IEmiPlanService {

    EmiPlanResponseDto createEmiPlan(EmiPlanRequestDto requestDto);

    EmiPlanResponseDto getEmiPlanById(String emiPlanId);

    List<EmiPlanResponseDto> getAllEmiPlans();

    List<EmiPlanResponseDto> getEmiPlansByTransactionId(String transactionId);

    EmiPlanResponseDto updateEmiPlan(String emiPlanId, EmiPlanRequestDto requestDto);

    EmiPlanResponseDto recordLatePayment(String emiPlanId);

    void deleteEmiPlan(String emiPlanId);
}
