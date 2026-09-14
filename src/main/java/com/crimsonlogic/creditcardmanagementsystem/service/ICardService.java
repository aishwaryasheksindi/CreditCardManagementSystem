package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardActivationOtpResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardActivationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardBlockRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardResponseDto;

import java.util.List;

public interface ICardService {

    CardResponseDto addCard(CardRequestDto cardDto);

    List<CardResponseDto> getAllCards();

    CardResponseDto getCardById(String cardId);

    CardResponseDto updateCard(String cardId, CardRequestDto cardDto);

    void setPin(String cardId, String pin);

    boolean verifyPin(String cardId, String pin);

    CardResponseDto blockCard(String cardId, CardBlockRequestDto requestDto);

    CardResponseDto unblockCard(String cardId, String reason);

    CardResponseDto replaceCard(String cardId, String reason);

    List<CardResponseDto> getCardsByCustomerId(String customerId);

    CardActivationOtpResponseDto requestActivationOtp(String cardId);

    CardResponseDto activateCard(String cardId, CardActivationRequestDto requestDto);
}