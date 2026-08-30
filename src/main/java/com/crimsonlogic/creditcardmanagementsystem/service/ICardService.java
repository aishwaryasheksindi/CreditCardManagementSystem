package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardDto;

public interface ICardService {

    CardDto addCard(CardDto cardDto);

    CardDto getCardById(String cardId);
}