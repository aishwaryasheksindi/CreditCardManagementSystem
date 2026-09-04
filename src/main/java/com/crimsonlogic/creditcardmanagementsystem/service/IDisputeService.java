package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeUpdateRequestDto;

import java.util.List;

public interface IDisputeService {

    DisputeResponseDto raiseDispute(DisputeRequestDto requestDto);

    DisputeResponseDto getDisputeById(String disputeId);

    List<DisputeResponseDto> getAllDisputes();

    List<DisputeResponseDto> getDisputesByCustomerId(String customerId);

    List<DisputeResponseDto> getDisputesByTransactionId(String transactionId);

    DisputeResponseDto updateDispute(String disputeId, DisputeUpdateRequestDto updateDto);

    void deleteDispute(String disputeId);
}
