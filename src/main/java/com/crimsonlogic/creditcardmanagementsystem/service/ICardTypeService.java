package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardTypeRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardTypeResponseDto;

public interface ICardTypeService {

    CardTypeResponseDto addCardType(CardTypeRequestDto cardTypeDto);

    CardTypeResponseDto getCardTypeById(String cardTypeId);
}