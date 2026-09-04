package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardResponseDto;

public interface ICardService {

    CardResponseDto addCard(CardRequestDto cardDto);

    CardResponseDto getCardById(String cardId);

    CardResponseDto updateCard(String cardId, CardRequestDto cardDto);

    void setPin(String cardId, String pin);

    boolean verifyPin(String cardId, String pin);
}