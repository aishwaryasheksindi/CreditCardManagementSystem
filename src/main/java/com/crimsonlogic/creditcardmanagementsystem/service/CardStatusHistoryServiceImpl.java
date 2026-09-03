package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardStatusHistoryRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardStatusHistoryResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.CardStatusHistory;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardStatusHistoryRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

@Service
public class CardStatusHistoryServiceImpl implements ICardStatusHistoryService {

    private final CardStatusHistoryRepository cardStatusHistoryRepository;
    private final CardRepository cardRepository;

    public CardStatusHistoryServiceImpl(
            CardStatusHistoryRepository cardStatusHistoryRepository,
            CardRepository cardRepository
       ) {

        this.cardStatusHistoryRepository = cardStatusHistoryRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public CardStatusHistoryResponseDto addCardStatusHistory(
            CardStatusHistoryRequestDto cardStatusHistoryDto) {

        String cardStatusHistoryId;

        do {
            cardStatusHistoryId =
                    IdGenerationUtil.generateCardStatusHistoryId();
        } while (cardStatusHistoryRepository.existsById(cardStatusHistoryId));

        Card card = cardRepository
                .findById(cardStatusHistoryDto.getCardId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card not found with ID: "
                                        + cardStatusHistoryDto.getCardId()
                        )
                );

        CardStatusHistory cardStatusHistory =
                new CardStatusHistory();

        cardStatusHistory.setCardStatusHistoryId(
                cardStatusHistoryId
        );

        cardStatusHistory.setCard(card);

        cardStatusHistory.setStatus(
                cardStatusHistoryDto.getStatus()
        );

        cardStatusHistory.setChangedAt(
                cardStatusHistoryDto.getChangedAt()
        );

        cardStatusHistory.setChangedBy(
                cardStatusHistoryDto.getChangedBy()
        );

        CardStatusHistory savedHistory =
                cardStatusHistoryRepository.save(cardStatusHistory);

        return convertToResponseDto(savedHistory);
    }

    @Override
    public CardStatusHistoryResponseDto getCardStatusHistoryById(
            String cardStatusHistoryId) {

        CardStatusHistory cardStatusHistory =
                cardStatusHistoryRepository
                        .findById(cardStatusHistoryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Card status history not found with ID: "
                                                + cardStatusHistoryId
                                )
                        );

        return convertToResponseDto(cardStatusHistory);
    }

    private CardStatusHistoryResponseDto convertToResponseDto(
            CardStatusHistory cardStatusHistory) {

        CardStatusHistoryResponseDto cardStatusHistoryDto =
                new CardStatusHistoryResponseDto();

        cardStatusHistoryDto.setCardStatusHistoryId(
                cardStatusHistory.getCardStatusHistoryId()
        );

        if (cardStatusHistory.getCard() != null) {
            cardStatusHistoryDto.setCardId(
                    cardStatusHistory.getCard().getCardId()
            );
        }

        cardStatusHistoryDto.setStatus(
                cardStatusHistory.getStatus()
        );

        cardStatusHistoryDto.setChangedAt(
                cardStatusHistory.getChangedAt()
        );

        cardStatusHistoryDto.setChangedBy(
                cardStatusHistory.getChangedBy()
        );

        return cardStatusHistoryDto;
    }
}