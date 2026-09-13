package com.crimsonlogic.creditcardmanagementsystem.service;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeClassificationRequestDto;
import com.crimsonlogic.creditcardmanagementsystem.dto.DisputeClassificationResponseDto;
import com.crimsonlogic.creditcardmanagementsystem.enums.DisputeType;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AiDisputeClassificationServiceImpl implements IAiDisputeClassificationService {

    private final GeminiApiClient geminiApiClient;

    public AiDisputeClassificationServiceImpl(GeminiApiClient geminiApiClient) {
        this.geminiApiClient = geminiApiClient;
    }

    @Override
    public DisputeClassificationResponseDto classifyDispute(DisputeClassificationRequestDto requestDto) {
        String description = requestDto.getDescription() != null ? requestDto.getDescription().trim() : "";

        // 1. Try Gemini AI classification first
        Optional<GeminiApiClient.DisputeClassificationResult> aiResultOpt =
                geminiApiClient.classifyDispute(description);

        if (aiResultOpt.isPresent()) {
            GeminiApiClient.DisputeClassificationResult aiResult = aiResultOpt.get();
            return new DisputeClassificationResponseDto(
                    aiResult.getSuggestedType(),
                    "HIGH",
                    aiResult.getExplanation()
            );
        }

        // 2. Deterministic keyword fallback (used when Gemini is unavailable or returns an unparsable value)
        return classifyDeterministically(description);
    }

    private DisputeClassificationResponseDto classifyDeterministically(String description) {
        String lower = description.toLowerCase();

        // Keyword rules per PDF specification
        if (lower.contains("twice") || lower.contains("duplicate") || lower.contains("double charged")) {
            return new DisputeClassificationResponseDto(
                    DisputeType.DUPLICATE_TRANSACTION,
                    "LOW",
                    "Dispute identified as a duplicate transaction based on duplicate charge indicators."
            );
        }

        if (lower.contains("didn't authorize") || lower.contains("did not authorize")
                || lower.contains("not me") || lower.contains("unauthorized")
                || lower.contains("fraud") || lower.contains("stolen card")) {
            return new DisputeClassificationResponseDto(
                    DisputeType.UNAUTHORIZED_TRANSACTION,
                    "LOW",
                    "Dispute identified as an unauthorized transaction based on authorization dispute indicators."
            );
        }

        if ((lower.contains("refund") || lower.contains("credit"))
                && (lower.contains("didn't receive") || lower.contains("did not receive")
                || lower.contains("never got") || lower.contains("not received") || lower.contains("pending"))) {
            return new DisputeClassificationResponseDto(
                    DisputeType.REFUND_NOT_RECEIVED,
                    "LOW",
                    "Dispute identified as an uncredited refund based on refund dispute indicators."
            );
        }

        if (lower.contains("wrong amount") || lower.contains("incorrect amount")
                || lower.contains("overcharged") || lower.contains("billed more")) {
            return new DisputeClassificationResponseDto(
                    DisputeType.WRONG_AMOUNT,
                    "LOW",
                    "Dispute identified as an incorrect amount based on billing discrepancy indicators."
            );
        }

        if (lower.contains("merchant") || lower.contains("seller") || lower.contains("vendor")
                || lower.contains("damaged") || lower.contains("defective") || lower.contains("cancelled service")) {
            return new DisputeClassificationResponseDto(
                    DisputeType.MERCHANT_DISPUTE,
                    "LOW",
                    "Dispute identified as a merchant dispute based on vendor or service discrepancy indicators."
            );
        }

        return new DisputeClassificationResponseDto(
                DisputeType.OTHER,
                "LOW",
                "Unable to determine category with high confidence; classified as Other."
        );
    }
}
