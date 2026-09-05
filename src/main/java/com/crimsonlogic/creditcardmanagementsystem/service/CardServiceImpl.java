package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.CardBlockRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.CardStatusHistoryRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.CardType;
import com.crimsonlogic.creditcardmanagementsystem.entity.Customer;
import com.crimsonlogic.creditcardmanagementsystem.enums.AuditAction;
import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.KycStatus;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.exception.VerificationLockedException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardTypeRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.CustomerRepository;
import com.crimsonlogic.creditcardmanagementsystem.security.CurrentUserContext;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardServiceImpl implements ICardService {

    private final CardRepository cardRepository;
    private final CustomerRepository customerRepository;
    private final CardTypeRepository cardTypeRepository;
    private final IAuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;
    private final ICardStatusHistoryService cardStatusHistoryService;
    private final CurrentUserContext currentUserContext;

    public CardServiceImpl(CardRepository cardRepository,
                           CustomerRepository customerRepository,
                           CardTypeRepository cardTypeRepository,
                           IAuditLogService auditLogService,
                           PasswordEncoder passwordEncoder,
                           ICardStatusHistoryService cardStatusHistoryService,
                           CurrentUserContext currentUserContext) {
        this.cardRepository = cardRepository;
        this.customerRepository = customerRepository;
        this.cardTypeRepository = cardTypeRepository;
        this.auditLogService = auditLogService;
        this.passwordEncoder = passwordEncoder;
        this.cardStatusHistoryService = cardStatusHistoryService;
        this.currentUserContext = currentUserContext;
    }

    @Override
    public CardResponseDto addCard(CardRequestDto cardDto) {

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

        if (customer.getKycStatus() != KycStatus.VERIFIED) {
            throw new VerificationLockedException(
                    "Customer KYC status is " + customer.getKycStatus() + ". Cards can only be issued to VERIFIED customers."
            );
        }

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

        auditLogService.logAction(cardDto.getCustomerId(), AuditAction.CREATE, "Card", savedCard.getCardId(), "New card issued for customer " + cardDto.getCustomerId());

        return convertToResponseDto(savedCard);
    }

    @Override
    public CardResponseDto getCardById(String cardId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card not found with ID: " + cardId
                        )
                );

        if (card.getCustomer() != null) {
            currentUserContext.assertCustomerOwnership(card.getCustomer().getCustomerId());
        }

        return convertToResponseDto(card);
    }

    @Override
    public CardResponseDto updateCard(String cardId, CardRequestDto cardDto) {

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

        CardStatus oldStatus = card.getCardStatus();
        BigDecimal oldCreditLimit = card.getCreditLimit();

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

        if (oldStatus != null && savedCard.getCardStatus() != null && oldStatus != savedCard.getCardStatus()) {
            auditLogService.logAction(
                    card.getCustomer() != null ? card.getCustomer().getCustomerId() : null,
                    AuditAction.STATUS_CHANGE,
                    "Card",
                    cardId,
                    "Card status changed from " + oldStatus + " to " + savedCard.getCardStatus()
            );
        }

        if (oldCreditLimit != null && savedCard.getCreditLimit() != null
                && oldCreditLimit.compareTo(savedCard.getCreditLimit()) != 0) {
            auditLogService.logAction(
                    card.getCustomer() != null ? card.getCustomer().getCustomerId() : null,
                    AuditAction.UPDATE,
                    "Card",
                    cardId,
                    "Credit limit changed from " + oldCreditLimit + " to " + savedCard.getCreditLimit()
            );
        }

        return convertToResponseDto(savedCard);
    }

    @Override
    public void setPin(String cardId, String pin) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Card not found with ID: " + cardId));

        if (card.getCustomer() != null) {
            currentUserContext.assertCustomerOwnership(card.getCustomer().getCustomerId());
        }

        card.setPinHash(passwordEncoder.encode(pin));
        card.setPinSetAt(LocalDateTime.now());
        card.setFailedPinAttempts(0);
        cardRepository.save(card);

        auditLogService.logAction(
                card.getCustomer() != null ? card.getCustomer().getCustomerId() : null,
                AuditAction.UPDATE,
                "Card",
                cardId,
                "PIN set/reset for card: " + cardId
        );
    }

    @Override
    public boolean verifyPin(String cardId, String pin) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Card not found with ID: " + cardId));

        if (card.getPinHash() == null) {
            throw new IllegalArgumentException("PIN not set for this card");
        }

        if (passwordEncoder.matches(pin, card.getPinHash())) {
            card.setFailedPinAttempts(0);
            cardRepository.save(card);
            return true;
        } else {
            int attempts = card.getFailedPinAttempts() + 1;
            card.setFailedPinAttempts(attempts);
            if (attempts >= 3) {
                card.setCardStatus(CardStatus.BLOCKED);
                cardRepository.save(card);
                auditLogService.logAction(
                        card.getCustomer() != null ? card.getCustomer().getCustomerId() : null,
                        AuditAction.STATUS_CHANGE,
                        "Card",
                        cardId,
                        "Card auto-blocked after 3 failed PIN attempts"
                );
                throw new IllegalArgumentException("Card blocked due to repeated incorrect PIN attempts");
            } else {
                cardRepository.save(card);
                throw new IllegalArgumentException("Incorrect PIN");
            }
        }
    }

    @Override
    @Transactional
    public CardResponseDto blockCard(String cardId, CardBlockRequestDto requestDto) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with ID: " + cardId));

        if (card.getCustomer() != null) {
            currentUserContext.assertCustomerOwnership(card.getCustomer().getCustomerId());
        }

        if (card.getCardStatus() == CardStatus.CLOSED) {
            throw new IllegalArgumentException("Cannot block a CLOSED card");
        }

        CardStatus targetStatus = requestDto.getTargetStatus();
        if (targetStatus != CardStatus.BLOCKED && targetStatus != CardStatus.LOST && targetStatus != CardStatus.STOLEN) {
            throw new IllegalArgumentException("Target status must be BLOCKED, LOST, or STOLEN");
        }

        card.setCardStatus(targetStatus);
        Card savedCard = cardRepository.save(card);

        String actingUserId = currentUserContext.getCurrentUserId();
        if (actingUserId == null) {
            actingUserId = card.getCustomer() != null ? card.getCustomer().getCustomerId() : "SYSTEM";
        }
        cardStatusHistoryService.addCardStatusHistory(
                new CardStatusHistoryRequestDto(cardId, targetStatus, LocalDateTime.now(), actingUserId)
        );

        auditLogService.logAction(
                actingUserId,
                AuditAction.STATUS_CHANGE,
                "Card",
                cardId,
                "Card status changed to " + targetStatus + ": " + requestDto.getReason()
        );

        return convertToResponseDto(savedCard);
    }

    @Override
    @Transactional
    public CardResponseDto unblockCard(String cardId, String reason) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with ID: " + cardId));

        if (card.getCustomer() != null) {
            currentUserContext.assertCustomerOwnership(card.getCustomer().getCustomerId());
        }

        if (card.getCardStatus() != CardStatus.BLOCKED) {
            throw new IllegalArgumentException("Only a card with status BLOCKED can be unblocked");
        }

        card.setCardStatus(CardStatus.ACTIVE);
        card.setFailedPinAttempts(0);
        Card savedCard = cardRepository.save(card);

        String actingUserId = currentUserContext.getCurrentUserId();
        if (actingUserId == null) {
            actingUserId = card.getCustomer() != null ? card.getCustomer().getCustomerId() : "SYSTEM";
        }
        cardStatusHistoryService.addCardStatusHistory(
                new CardStatusHistoryRequestDto(cardId, CardStatus.ACTIVE, LocalDateTime.now(), actingUserId)
        );

        auditLogService.logAction(
                actingUserId,
                AuditAction.STATUS_CHANGE,
                "Card",
                cardId,
                "Card unblocked and status reset to ACTIVE: " + reason
        );

        return convertToResponseDto(savedCard);
    }

    @Override
    @Transactional
    public CardResponseDto replaceCard(String cardId, String reason) {
        Card oldCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with ID: " + cardId));

        if (oldCard.getCustomer() != null) {
            currentUserContext.assertCustomerOwnership(oldCard.getCustomer().getCustomerId());
        }

        if (oldCard.getCardStatus() != CardStatus.LOST && oldCard.getCardStatus() != CardStatus.STOLEN) {
            throw new IllegalArgumentException("Only cards with status LOST or STOLEN can be replaced");
        }

        // Close old card
        oldCard.setCardStatus(CardStatus.CLOSED);
        cardRepository.save(oldCard);

        String actingUserId = currentUserContext.getCurrentUserId();
        if (actingUserId == null) {
            actingUserId = oldCard.getCustomer() != null ? oldCard.getCustomer().getCustomerId() : "SYSTEM";
        }
        cardStatusHistoryService.addCardStatusHistory(
                new CardStatusHistoryRequestDto(cardId, CardStatus.CLOSED, LocalDateTime.now(), actingUserId)
        );
        auditLogService.logAction(
                actingUserId,
                AuditAction.STATUS_CHANGE,
                "Card",
                cardId,
                "Old card closed due to replacement: " + reason
        );

        // Issue new replacement card
        String newCardId;
        do {
            newCardId = IdGenerationUtil.generateCardId();
        } while (cardRepository.existsById(newCardId));

        Card newCard = new Card();
        newCard.setCardId(newCardId);
        newCard.setCardReference("REP-" + newCardId);
        newCard.setCustomer(oldCard.getCustomer());
        newCard.setCardType(oldCard.getCardType());
        newCard.setCardStatus(CardStatus.ACTIVE);
        newCard.setCreditLimit(oldCard.getCreditLimit());
        newCard.setAvailableLimit(oldCard.getCreditLimit());
        newCard.setBillingCycle(oldCard.getBillingCycle());
        newCard.setInterestRate(oldCard.getInterestRate());
        newCard.setAnnualFee(oldCard.getAnnualFee());
        newCard.setIssuanceDate(LocalDate.now());
        newCard.setExpiryDate(LocalDate.now().plusYears(5));
        newCard.setPinHash(null);
        newCard.setFailedPinAttempts(0);

        Card savedCard = cardRepository.save(newCard);

        cardStatusHistoryService.addCardStatusHistory(
                new CardStatusHistoryRequestDto(newCardId, CardStatus.ACTIVE, LocalDateTime.now(), actingUserId)
        );
        auditLogService.logAction(
                actingUserId,
                AuditAction.CREATE,
                "Card",
                newCardId,
                "Replacement card issued for old card " + cardId + ": " + reason
        );

        return convertToResponseDto(savedCard);
    }

    private CardResponseDto convertToResponseDto(Card card) {

        CardResponseDto cardDto = new CardResponseDto();

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