package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeClassificationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeClassificationResponseDto;

public interface IAiDisputeClassificationService {

    DisputeClassificationResponseDto classifyDispute(DisputeClassificationRequestDto requestDto);
}
