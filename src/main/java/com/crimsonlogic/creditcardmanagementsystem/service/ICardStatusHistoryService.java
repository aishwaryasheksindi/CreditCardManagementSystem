package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardStatusHistoryRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardStatusHistoryResponseDto;

public interface ICardStatusHistoryService {

    CardStatusHistoryResponseDto addCardStatusHistory(
            CardStatusHistoryRequestDto cardStatusHistoryDto
    );

    CardStatusHistoryResponseDto getCardStatusHistoryById(
            String cardStatusHistoryId
    );
}