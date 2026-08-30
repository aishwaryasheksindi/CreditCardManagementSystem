package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardTypeDto;

public interface ICardTypeService {

    CardTypeDto addCardType(CardTypeDto cardTypeDto);

    CardTypeDto getCardTypeById(String cardTypeId);
}