package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardTypeDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.CardType;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardTypeRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

@Service
public class CardTypeServiceImpl implements ICardTypeService {

    private final CardTypeRepository cardTypeRepository;
  //  private final IdGenerationUtil idGenerationUtil;

    public CardTypeServiceImpl(CardTypeRepository cardTypeRepository
                               ) {
        this.cardTypeRepository = cardTypeRepository;
        
    }

    @Override
    public CardTypeDto addCardType(CardTypeDto cardTypeDto) {

        String cardTypeId;

        do {
            cardTypeId = IdGenerationUtil.generateCardTypeId();
        } while (cardTypeRepository.existsById(cardTypeId));

        CardType cardType = new CardType();

        cardType.setCardTypeId(cardTypeId);
        cardType.setTypeName(cardTypeDto.getTypeName());
        cardType.setCreditLimit(cardTypeDto.getCreditLimit());
        cardType.setInterestRate(cardTypeDto.getInterestRate());
        cardType.setRewardRate(cardTypeDto.getRewardRate());
        cardType.setJoiningFee(cardTypeDto.getJoiningFee());
        cardType.setAnnualFee(cardTypeDto.getAnnualFee());
        cardType.setCashWithdrawalAllowed(
                cardTypeDto.getCashWithdrawalAllowed()
        );
        cardType.setEmiEligible(
                cardTypeDto.getEmiEligible()
        );
        cardType.setRewardCategories(
                cardTypeDto.getRewardCategories()
        );

        CardType savedCardType = cardTypeRepository.save(cardType);

        return convertToDto(savedCardType);
    }

    @Override
    public CardTypeDto getCardTypeById(String cardTypeId) {

        CardType cardType = cardTypeRepository.findById(cardTypeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Card Type not found with ID: " + cardTypeId)
                );

        return convertToDto(cardType);
    }

    private CardTypeDto convertToDto(CardType cardType) {

        CardTypeDto cardTypeDto = new CardTypeDto();

        cardTypeDto.setCardTypeId(cardType.getCardTypeId());
        cardTypeDto.setTypeName(cardType.getTypeName());
        cardTypeDto.setCreditLimit(cardType.getCreditLimit());
        cardTypeDto.setInterestRate(cardType.getInterestRate());
        cardTypeDto.setRewardRate(cardType.getRewardRate());
        cardTypeDto.setJoiningFee(cardType.getJoiningFee());
        cardTypeDto.setAnnualFee(cardType.getAnnualFee());
        cardTypeDto.setCashWithdrawalAllowed(
                cardType.getCashWithdrawalAllowed()
        );
        cardTypeDto.setEmiEligible(
                cardType.getEmiEligible()
        );
        cardTypeDto.setRewardCategories(
                cardType.getRewardCategories()
        );

        return cardTypeDto;
    }
}