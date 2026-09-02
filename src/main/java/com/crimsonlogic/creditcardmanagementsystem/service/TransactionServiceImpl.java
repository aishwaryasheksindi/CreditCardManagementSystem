package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.dto.TransactionDto;
import com.crimsonlogic.creditcardmanagementsystem.entity.Card;
import com.crimsonlogic.creditcardmanagementsystem.entity.TransactionCategory;
import com.crimsonlogic.creditcardmanagementsystem.entity.Merchant;
import com.crimsonlogic.creditcardmanagementsystem.entity.Transaction;
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
  //  private final IdGenerationUtil idGenerationUtil;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            CardRepository cardRepository,
            MerchantRepository merchantRepository,
            TransactionCategoryRepository categoryRepository
            ) {

        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.merchantRepository = merchantRepository;
        this.categoryRepository = categoryRepository;
       
    }

    @Override
    public TransactionDto addTransaction(TransactionDto transactionDto) {

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


        // Save Transaction
        Transaction savedTransaction =
                transactionRepository.save(transaction);


        // Convert Entity to DTO
        return convertToDto(savedTransaction);
    }


    @Override
    public TransactionDto getTransactionById(String transactionId) {

        Transaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found with ID: "
                                                + transactionId
                                )
                        );

        return convertToDto(transaction);
    }


    // Entity → DTO
    private TransactionDto convertToDto(Transaction transaction) {

        TransactionDto transactionDto = new TransactionDto();

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

        return transactionDto;
    }
}