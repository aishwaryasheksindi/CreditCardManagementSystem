package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardStatusHistoryDto;

public interface ICardStatusHistoryService {

    CardStatusHistoryDto addCardStatusHistory(
            CardStatusHistoryDto cardStatusHistoryDto
    );

    CardStatusHistoryDto getCardStatusHistoryById(
            String cardStatusHistoryId
    );
}