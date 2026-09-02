package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.CardType;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardTypeRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImpl implements ICardService {

    private final CardRepository cardRepository;
    private final CustomerRepository customerRepository;
    private final CardTypeRepository cardTypeRepository;
//    private final IdGenerationUtil idGenerationUtil;

    public CardServiceImpl(CardRepository cardRepository,
                           CustomerRepository customerRepository,
                           CardTypeRepository cardTypeRepository
                           ) {

        this.cardRepository = cardRepository;
        this.customerRepository = customerRepository;
        this.cardTypeRepository = cardTypeRepository;
        
    }

    @Override
    public CardDto addCard(CardDto cardDto) {

        if (cardDto.getAvailableLimit() != null && cardDto.getCreditLimit() != null
                && cardDto.getAvailableLimit().compareTo(cardDto.getCreditLimit()) > 0) {
            throw new IllegalArgumentException("Available limit cannot exceed credit limit");
        }

        String cardId;

        do {
            cardId = IdGenerationUtil.generateCardId();
        } while (cardRepository.existsById(cardId));

        Customer customer = customerRepository.findById(cardDto.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with ID: "
                                        + cardDto.getCustomerId()
                        )
                );

        CardType cardType = cardTypeRepository.findById(cardDto.getCardTypeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card Type not found with ID: "
                                        + cardDto.getCardTypeId()
                        )
                );

        Card card = new Card();

        card.setCardId(cardId);
        card.setCardReference(cardDto.getCardReference());
        card.setCustomer(customer);
        card.setCardType(cardType);
        card.setCardStatus(cardDto.getCardStatus());
        card.setCreditLimit(cardDto.getCreditLimit());
        card.setAvailableLimit(cardDto.getAvailableLimit());
        card.setBillingCycle(cardDto.getBillingCycle());
        card.setInterestRate(cardDto.getInterestRate());
        card.setAnnualFee(cardDto.getAnnualFee());
        card.setExpiryDate(cardDto.getExpiryDate());
        card.setIssuanceDate(cardDto.getIssuanceDate());

        Card savedCard = cardRepository.save(card);

        return convertToDto(savedCard);
    }

    @Override
    public CardDto getCardById(String cardId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card not found with ID: " + cardId
                        )
                );

        return convertToDto(card);
    }

    @Override
    public CardDto updateCard(String cardId, CardDto cardDto) {

        if (cardDto.getAvailableLimit() != null && cardDto.getCreditLimit() != null
                && cardDto.getAvailableLimit().compareTo(cardDto.getCreditLimit()) > 0) {
            throw new IllegalArgumentException("Available limit cannot exceed credit limit");
        }

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card not found with ID: " + cardId
                        )
                );

        if (cardDto.getCardReference() != null) {
            card.setCardReference(cardDto.getCardReference());
        }
        if (cardDto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(cardDto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + cardDto.getCustomerId()));
            card.setCustomer(customer);
        }
        if (cardDto.getCardTypeId() != null) {
            CardType cardType = cardTypeRepository.findById(cardDto.getCardTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Card Type not found with ID: " + cardDto.getCardTypeId()));
            card.setCardType(cardType);
        }
        if (cardDto.getCardStatus() != null) {
            card.setCardStatus(cardDto.getCardStatus());
        }
        if (cardDto.getCreditLimit() != null) {
            card.setCreditLimit(cardDto.getCreditLimit());
        }
        if (cardDto.getAvailableLimit() != null) {
            card.setAvailableLimit(cardDto.getAvailableLimit());
        }
        if (cardDto.getBillingCycle() != null) {
            card.setBillingCycle(cardDto.getBillingCycle());
        }
        if (cardDto.getInterestRate() != null) {
            card.setInterestRate(cardDto.getInterestRate());
        }
        if (cardDto.getAnnualFee() != null) {
            card.setAnnualFee(cardDto.getAnnualFee());
        }
        if (cardDto.getExpiryDate() != null) {
            card.setExpiryDate(cardDto.getExpiryDate());
        }
        if (cardDto.getIssuanceDate() != null) {
            card.setIssuanceDate(cardDto.getIssuanceDate());
        }

        Card savedCard = cardRepository.save(card);
        return convertToDto(savedCard);
    }

    private CardDto convertToDto(Card card) {

        CardDto cardDto = new CardDto();

        cardDto.setCardId(card.getCardId());
        cardDto.setCardReference(card.getCardReference());

        if (card.getCustomer() != null) {
            cardDto.setCustomerId(card.getCustomer().getCustomerId());
        }

        if (card.getCardType() != null) {
            cardDto.setCardTypeId(card.getCardType().getCardTypeId());
        }

        cardDto.setCardStatus(card.getCardStatus());
        cardDto.setCreditLimit(card.getCreditLimit());
        cardDto.setAvailableLimit(card.getAvailableLimit());
        cardDto.setBillingCycle(card.getBillingCycle());
        cardDto.setInterestRate(card.getInterestRate());
        cardDto.setAnnualFee(card.getAnnualFee());
        cardDto.setExpiryDate(card.getExpiryDate());
        cardDto.setIssuanceDate(card.getIssuanceDate());

        return cardDto;
    }
}