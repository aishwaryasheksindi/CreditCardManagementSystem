package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardStatusHistoryDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.CardStatusHistory;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardStatusHistoryRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

@Service
public class CardStatusHistoryServiceImpl implements ICardStatusHistoryService {

    private final CardStatusHistoryRepository cardStatusHistoryRepository;
    private final CardRepository cardRepository;
    private final IdGenerationUtil idGenerationUtil;

    public CardStatusHistoryServiceImpl(
            CardStatusHistoryRepository cardStatusHistoryRepository,
            CardRepository cardRepository,
            IdGenerationUtil idGenerationUtil) {

        this.cardStatusHistoryRepository = cardStatusHistoryRepository;
        this.cardRepository = cardRepository;
        this.idGenerationUtil = idGenerationUtil;
    }

    @Override
    public CardStatusHistoryDto addCardStatusHistory(
            CardStatusHistoryDto cardStatusHistoryDto) {

        String cardStatusHistoryId;

        do {
            cardStatusHistoryId =
                    idGenerationUtil.generateCardStatusHistoryId();
        } while (cardStatusHistoryRepository.existsById(cardStatusHistoryId));

        Card card = cardRepository
                .findById(cardStatusHistoryDto.getCardId())
                .orElseThrow(() ->
                        new RuntimeException(
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

        return convertToDto(savedHistory);
    }

    @Override
    public CardStatusHistoryDto getCardStatusHistoryById(
            String cardStatusHistoryId) {

        CardStatusHistory cardStatusHistory =
                cardStatusHistoryRepository
                        .findById(cardStatusHistoryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Card status history not found with ID: "
                                                + cardStatusHistoryId
                                )
                        );

        return convertToDto(cardStatusHistory);
    }

    private CardStatusHistoryDto convertToDto(
            CardStatusHistory cardStatusHistory) {

        CardStatusHistoryDto cardStatusHistoryDto =
                new CardStatusHistoryDto();

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