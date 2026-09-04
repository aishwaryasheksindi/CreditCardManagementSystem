package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.TransactionCategory;
import com.crimsonlogic.creditcardmanagementsystem.entity.Merchant;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
import com.crimsonlogic.creditcardmanagementsystem.enums.CardStatus;
import com.crimsonlogic.creditcardmanagementsystem.enums.TransactionType;
import com.crimsonlogic.creditcardmanagementsystem.exception.ResourceNotFoundException;
import com.crimsonlogic.creditcardmanagementsystem.repository.CardRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionCategoryRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.MerchantRepository;
import com.crimsonlogic.creditcardmanagementsystem.repository.TransactionRepository;
import com.crimsonlogic.creditcardmanagementsystem.utility.IdGenerationUtil;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final MerchantRepository merchantRepository;
    private final TransactionCategoryRepository categoryRepository;
    private final ICardService cardService;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            CardRepository cardRepository,
            MerchantRepository merchantRepository,
            TransactionCategoryRepository categoryRepository,
            ICardService cardService
            ) {

        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.merchantRepository = merchantRepository;
        this.categoryRepository = categoryRepository;
        this.cardService = cardService;
    }

    @Override
    public TransactionResponseDto addTransaction(TransactionRequestDto transactionDto) {

        // Generate unique transaction ID
        String transactionId;

        do {
            transactionId = IdGenerationUtil.generateTransactionId();
        } while (transactionRepository.existsById(transactionId));


        // Get Card
        Card card = cardRepository
                .findById(transactionDto.getCardId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Card not found with ID: "
                                        + transactionDto.getCardId()
                        )
                );

        if (card.getCardStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Card is " + card.getCardStatus() + " — transactions are not allowed on this card");
        }

        if (transactionDto.getTransactionType() == TransactionType.CASH_WITHDRAWAL) {
            if (transactionDto.getPin() == null || transactionDto.getPin().isBlank()) {
                throw new IllegalArgumentException("PIN is required for this transaction type");
            }
            cardService.verifyPin(card.getCardId(), transactionDto.getPin());
        }

        // Get Merchant
        Merchant merchant = merchantRepository
                .findById(transactionDto.getMerchantId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Merchant not found with ID: "
                                        + transactionDto.getMerchantId()
                        )
                );


        // Get Category
        TransactionCategory category = categoryRepository
                .findById(transactionDto.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with ID: "
                                        + transactionDto.getCategoryId()
                        )
                );


        // Convert DTO to Entity
        Transaction transaction = new Transaction();

        transaction.setTransactionId(transactionId);
        transaction.setCard(card);
        transaction.setMerchant(merchant);
        transaction.setCategory(category);
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCurrency(transactionDto.getCurrency());
        transaction.setTransactionDate(
                transactionDto.getTransactionDate()
        );
        transaction.setTransactionLocation(
                transactionDto.getTransactionLocation()
        );
        transaction.setTransactionStatus(
                transactionDto.getTransactionStatus()
        );
        transaction.setTransactionType(
                transactionDto.getTransactionType()
        );


        // Save Transaction
        Transaction savedTransaction =
                transactionRepository.save(transaction);


        // Convert Entity to DTO
        return convertToResponseDto(savedTransaction);
    }


    @Override
    public TransactionResponseDto getTransactionById(String transactionId) {

        Transaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found with ID: "
                                                + transactionId
                                )
                        );

        return convertToResponseDto(transaction);
    }


    // Entity → Response DTO
    private TransactionResponseDto convertToResponseDto(Transaction transaction) {

        TransactionResponseDto transactionDto = new TransactionResponseDto();

        transactionDto.setTransactionId(
                transaction.getTransactionId()
        );

        if (transaction.getCard() != null) {
            transactionDto.setCardId(
                    transaction.getCard().getCardId()
            );
        }

        if (transaction.getMerchant() != null) {
            transactionDto.setMerchantId(
                    transaction.getMerchant().getMerchantId()
            );
        }

        if (transaction.getCategory() != null) {
            transactionDto.setCategoryId(
                    transaction.getCategory().getCategoryId()
            );
        }

        transactionDto.setAmount(
                transaction.getAmount()
        );

        transactionDto.setCurrency(
                transaction.getCurrency()
        );

        transactionDto.setTransactionDate(
                transaction.getTransactionDate()
        );

        transactionDto.setTransactionLocation(
                transaction.getTransactionLocation()
        );

        transactionDto.setTransactionStatus(
                transaction.getTransactionStatus()
        );

        transactionDto.setTransactionType(
                transaction.getTransactionType()
        );

        return transactionDto;
    }
}