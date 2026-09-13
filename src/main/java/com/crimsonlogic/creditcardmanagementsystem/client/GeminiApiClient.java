package com.crimsonlogic.creditcardmanagementsystem.client;

import com.crimsonlogic.creditcardmanagementsystem.dto.EmiTenureOptionDto;
import com.crimsonlogic.creditcardmanagementsystem.enums.DisputeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GeminiApiClient {

    private static final Logger logger = LoggerFactory.getLogger(GeminiApiClient.class);

    private static final Pattern TENURE_PATTERN = Pattern.compile("\"recommendedTenureMonths\"\\s*:\\s*(\\d+)");
    private static final Pattern REASON_PATTERN = Pattern.compile("\"reason\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
    private static final Pattern EXPLANATION_PATTERN = Pattern.compile("\"explanation\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
    private static final Pattern SUMMARY_PATTERN = Pattern.compile("\"summary\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
    private static final Pattern DISPUTE_TYPE_PATTERN = Pattern.compile("\"suggestedType\"\\s*:\\s*\"([A-Z_]+)\"");

    private final String apiKey;
    private final String apiUrl;
    private final RestTemplate restTemplate;

    public GeminiApiClient(@Value("${gemini.api.key:}") String apiKey,
                           @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}") String apiUrl) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.apiUrl = apiUrl != null ? apiUrl.trim() : "";
        this.restTemplate = createRestTemplate();
    }

    // Constructor for testing / custom RestTemplate injection
    public GeminiApiClient(String apiKey, String apiUrl, RestTemplate restTemplate) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.apiUrl = apiUrl != null ? apiUrl.trim() : "";
        this.restTemplate = restTemplate;
    }

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }

    public static class AiRecommendationResult {
        private final Integer recommendedTenureMonths;
        private final String explanation;

        public AiRecommendationResult(Integer recommendedTenureMonths, String explanation) {
            this.recommendedTenureMonths = recommendedTenureMonths;
            this.explanation = explanation;
        }

        public Integer getRecommendedTenureMonths() {
            return recommendedTenureMonths;
        }

        public String getExplanation() {
            return explanation;
        }
    }

    public static class DisputeClassificationResult {
        private final DisputeType suggestedType;
        private final String explanation;

        public DisputeClassificationResult(DisputeType suggestedType, String explanation) {
            this.suggestedType = suggestedType;
            this.explanation = explanation;
        }

        public DisputeType getSuggestedType() {
            return suggestedType;
        }

        public String getExplanation() {
            return explanation;
        }
    }

    /**
     * Sends non-sensitive transaction and tenure data to Gemini to get tenure recommendation and explanation.
     * Returns Optional.empty() if API key is not configured, or if any error occurs.
     */
    public Optional<AiRecommendationResult> getEmiRecommendation(BigDecimal amount,
                                                                 BigDecimal availableLimit,
                                                                 List<EmiTenureOptionDto> options) {
        if (apiKey.isEmpty()) {
            logger.info("Gemini API key is not configured. Falling back to deterministic recommendation.");
            return Optional.empty();
        }

        try {
            String prompt = buildPrompt(amount, availableLimit, options);
            String url = buildRequestUrl();

            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> partsMap = Map.of("parts", List.of(textPart));
            Map<String, Object> requestBody = Map.of("contents", List.of(partsMap));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseGeminiResponse(response.getBody());
            } else {
                logger.warn("Gemini API returned non-2xx status: {}", response.getStatusCode());
            }
        } catch (Exception ex) {
            logger.warn("Failed to obtain recommendation from Gemini API: {}. Falling back to deterministic engine.", ex.getMessage());
        }

        return Optional.empty();
    }

    private String buildPrompt(BigDecimal amount, BigDecimal availableLimit, List<EmiTenureOptionDto> options) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an intelligent financial advisor for CardNest Bank.\n");
        sb.append("A customer has made a credit card transaction of ₹").append(amount);
        if (availableLimit != null) {
            sb.append(" with an available card limit of ₹").append(availableLimit);
        }
        sb.append(".\nAvailable calculated EMI options:\n");
        for (EmiTenureOptionDto opt : options) {
            sb.append(String.format("- Tenure: %d months | Monthly EMI: ₹%s | Total Interest: ₹%s | Total Payable: ₹%s\n",
                    opt.getTenureMonths(), opt.getMonthlyEmi(), opt.getTotalInterest(), opt.getTotalPayable()));
        }
        sb.append("\nAnalyze the financial impact and recommend the single best tenure that offers a manageable monthly installment while keeping total interest reasonable.\n");
        sb.append("Respond ONLY with a valid JSON object matching this structure (no markdown formatting, no code blocks):\n");
        sb.append("{\"recommendedTenureMonths\": <integer>, \"reason\": \"<brief 1-2 sentence customer explanation>\"}\n");
        return sb.toString();
    }

    private String buildRequestUrl() {
        if (apiUrl.contains("key=")) {
            return apiUrl;
        }
        String separator = apiUrl.contains("?") ? "&" : "?";
        return apiUrl + separator + "key=" + apiKey;
    }

    private Optional<AiRecommendationResult> parseGeminiResponse(String responseJson) {
        if (responseJson == null || responseJson.isBlank()) {
            return Optional.empty();
        }

        try {
            Matcher tenureMatcher = TENURE_PATTERN.matcher(responseJson);
            if (!tenureMatcher.find()) {
                return Optional.empty();
            }
            int tenure = Integer.parseInt(tenureMatcher.group(1));

            Matcher reasonMatcher = REASON_PATTERN.matcher(responseJson);
            String reason = "";
            if (reasonMatcher.find()) {
                reason = reasonMatcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
            } else {
                reason = String.format("A %d-month tenure is recommended for a manageable monthly installment.", tenure);
            }

            if (tenure > 0) {
                return Optional.of(new AiRecommendationResult(tenure, reason));
            }
        } catch (Exception e) {
            logger.warn("Could not parse AI response JSON: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Sends fraud risk scoring details to Gemini to get a concise plain-English explanation.
     * Returns Optional.empty() if API key is not configured, or if any error occurs.
     */
    public Optional<String> getFraudExplanation(int score, String riskFactors) {
        if (apiKey.isEmpty()) {
            logger.info("Gemini API key is not configured. Falling back to deterministic fraud explanation.");
            return Optional.empty();
        }

        try {
            String prompt = buildFraudPrompt(score, riskFactors);
            String url = buildRequestUrl();

            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> partsMap = Map.of("parts", List.of(textPart));
            Map<String, Object> requestBody = Map.of("contents", List.of(partsMap));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseFraudExplanationResponse(response.getBody());
            } else {
                logger.warn("Gemini API returned non-2xx status: {}", response.getStatusCode());
            }
        } catch (Exception ex) {
            logger.warn("Failed to obtain fraud explanation from Gemini API: {}. Falling back to deterministic engine.", ex.getMessage());
        }

        return Optional.empty();
    }

    private String buildFraudPrompt(int score, String riskFactors) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an intelligent fraud risk analyst for CardNest Bank.\n");
        sb.append("A transaction has been assessed with a fraud risk score of ").append(score).append(" out of 100.\n");
        sb.append("Identified risk factor signals: ").append(riskFactors).append(".\n");
        sb.append("Provide a single, professional plain-English sentence summarizing the risk assessment for the customer or analyst.\n");
        sb.append("Respond ONLY with a valid JSON object matching this structure (no markdown formatting, no code blocks):\n");
        sb.append("{\"explanation\": \"<single sentence summary>\"}\n");
        return sb.toString();
    }

    private Optional<String> parseFraudExplanationResponse(String responseJson) {
        if (responseJson == null || responseJson.isBlank()) {
            return Optional.empty();
        }

        try {
            Matcher matcher = EXPLANATION_PATTERN.matcher(responseJson);
            if (matcher.find()) {
                String explanation = matcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\").trim();
                if (!explanation.isEmpty()) {
                    return Optional.of(explanation);
                }
            }
        } catch (Exception e) {
            logger.warn("Could not parse fraud explanation AI response JSON: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Sends calculated monthly spending metrics to Gemini to get a natural-language spending summary paragraph.
     * Returns Optional.empty() if API key is not configured, or if any error occurs.
     */
    public Optional<String> getSpendingSummary(BigDecimal totalSpending,
                                               Map<String, Double> categoryBreakdown,
                                               List<String> topMerchants,
                                               BigDecimal avgTxnValue,
                                               Double momChangePercent) {
        if (apiKey.isEmpty()) {
            logger.info("Gemini API key is not configured. Falling back to deterministic spending summary.");
            return Optional.empty();
        }

        try {
            String prompt = buildSpendingPrompt(totalSpending, categoryBreakdown, topMerchants, avgTxnValue, momChangePercent);
            String url = buildRequestUrl();

            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> partsMap = Map.of("parts", List.of(textPart));
            Map<String, Object> requestBody = Map.of("contents", List.of(partsMap));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseSpendingSummaryResponse(response.getBody());
            } else {
                logger.warn("Gemini API returned non-2xx status: {}", response.getStatusCode());
            }
        } catch (Exception ex) {
            logger.warn("Failed to obtain spending summary from Gemini API: {}. Falling back to deterministic engine.", ex.getMessage());
        }

        return Optional.empty();
    }

    private String buildSpendingPrompt(BigDecimal totalSpending,
                                       Map<String, Double> categoryBreakdown,
                                       List<String> topMerchants,
                                       BigDecimal avgTxnValue,
                                       Double momChangePercent) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a financial advisor for CardNest Bank.\n");
        sb.append("Here is the customer's credit card spending analysis for the current month:\n");
        sb.append("- Total spending: ₹").append(totalSpending).append("\n");
        sb.append("- Category breakdown (% of total): ").append(categoryBreakdown).append("\n");
        sb.append("- Top merchants: ").append(topMerchants).append("\n");
        sb.append("- Average transaction value: ₹").append(avgTxnValue).append("\n");
        sb.append("- Month-over-month spending change: ").append(String.format("%.1f%%", momChangePercent != null ? momChangePercent : 0.0)).append("\n");
        sb.append("Provide a short, concise 2-3 sentence paragraph summarizing their spending patterns and trend.\n");
        sb.append("Respond ONLY with a valid JSON object matching this structure (no markdown formatting, no code blocks):\n");
        sb.append("{\"summary\": \"<short paragraph summarizing the spending>\"}\n");
        return sb.toString();
    }

    private Optional<String> parseSpendingSummaryResponse(String responseJson) {
        if (responseJson == null || responseJson.isBlank()) {
            return Optional.empty();
        }

        try {
            Matcher matcher = SUMMARY_PATTERN.matcher(responseJson);
            if (matcher.find()) {
                String summary = matcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\").trim();
                if (!summary.isEmpty()) {
                    return Optional.of(summary);
                }
            }
        } catch (Exception e) {
            logger.warn("Could not parse spending summary AI response JSON: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Sends payment risk signals to Gemini to get a concise explanation of the customer's risk profile.
     * Returns Optional.empty() if API key is not configured, or if any error occurs.
     */
    public Optional<String> getPaymentRiskExplanation(String riskLevel,
                                                      double utilization,
                                                      int minOnlyCount,
                                                      int lateCount) {
        if (apiKey.isEmpty()) {
            logger.info("Gemini API key is not configured. Falling back to deterministic payment risk explanation.");
            return Optional.empty();
        }

        try {
            String prompt = buildPaymentRiskPrompt(riskLevel, utilization, minOnlyCount, lateCount);
            String url = buildRequestUrl();

            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> partsMap = Map.of("parts", List.of(textPart));
            Map<String, Object> requestBody = Map.of("contents", List.of(partsMap));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseFraudExplanationResponse(response.getBody());
            } else {
                logger.warn("Gemini API returned non-2xx status: {}", response.getStatusCode());
            }
        } catch (Exception ex) {
            logger.warn("Failed to obtain payment risk explanation from Gemini API: {}. Falling back to deterministic engine.", ex.getMessage());
        }

        return Optional.empty();
    }

    private String buildPaymentRiskPrompt(String riskLevel,
                                          double utilization,
                                          int minOnlyCount,
                                          int lateCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a credit risk analyst for CardNest Bank.\n");
        sb.append("A customer has been evaluated with a payment-risk prediction level of ").append(riskLevel).append(".\n");
        sb.append("Financial risk signals:\n");
        sb.append("- Credit card utilization: ").append(String.format("%.1f%%", utilization)).append("\n");
        sb.append("- Minimum-only payment cycles in last 3 cycles: ").append(minOnlyCount).append("\n");
        sb.append("- Late payment cycles: ").append(lateCount).append("\n");
        sb.append("Provide a single, professional plain-English sentence summarizing the payment risk assessment and explaining the behavioral indicators.\n");
        sb.append("Respond ONLY with a valid JSON object matching this structure (no markdown formatting, no code blocks):\n");
        sb.append("{\"explanation\": \"<single sentence explanation>\"}\n");
        return sb.toString();
    }

    /**
     * Sends dispute description to Gemini to classify into one of the 6 DisputeType enum values.
     * Returns Optional.empty() if API key is not configured, if any error occurs, or if
     * the AI output fails to parse into one of the strict DisputeType enum constants.
     */
    public Optional<DisputeClassificationResult> classifyDispute(String description) {
        if (apiKey.isEmpty()) {
            logger.info("Gemini API key is not configured. Falling back to deterministic dispute classification.");
            return Optional.empty();
        }

        try {
            String prompt = buildDisputePrompt(description);
            String url = buildRequestUrl();

            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> partsMap = Map.of("parts", List.of(textPart));
            Map<String, Object> requestBody = Map.of("contents", List.of(partsMap));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseDisputeClassificationResponse(response.getBody());
            } else {
                logger.warn("Gemini API returned non-2xx status: {}", response.getStatusCode());
            }
        } catch (Exception ex) {
            logger.warn("Failed to obtain dispute classification from Gemini API: {}. Falling back to deterministic engine.", ex.getMessage());
        }

        return Optional.empty();
    }

    private String buildDisputePrompt(String description) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an intelligent dispute resolution analyst for CardNest Bank.\n");
        sb.append("A customer has submitted the following credit card dispute description:\n");
        sb.append("\"").append(description).append("\"\n\n");
        sb.append("Classify this dispute into EXACTLY ONE of the following valid DisputeType categories:\n");
        sb.append("- UNAUTHORIZED_TRANSACTION\n");
        sb.append("- DUPLICATE_TRANSACTION\n");
        sb.append("- WRONG_AMOUNT\n");
        sb.append("- REFUND_NOT_RECEIVED\n");
        sb.append("- MERCHANT_DISPUTE\n");
        sb.append("- OTHER\n\n");
        sb.append("Provide a single concise sentence explaining why this classification fits.\n");
        sb.append("Respond ONLY with a valid JSON object matching this structure (no markdown formatting, no code blocks):\n");
        sb.append("{\"suggestedType\": \"<EXACT_ENUM_NAME>\", \"explanation\": \"<single sentence explanation>\"}\n");
        return sb.toString();
    }

    private Optional<DisputeClassificationResult> parseDisputeClassificationResponse(String responseJson) {
        if (responseJson == null || responseJson.isBlank()) {
            return Optional.empty();
        }

        try {
            Matcher typeMatcher = DISPUTE_TYPE_PATTERN.matcher(responseJson);
            if (!typeMatcher.find()) {
                logger.warn("Gemini dispute response did not contain a suggestedType key");
                return Optional.empty();
            }

            String typeStr = typeMatcher.group(1).trim();
            DisputeType disputeType;
            try {
                disputeType = DisputeType.valueOf(typeStr);
            } catch (IllegalArgumentException ex) {
                logger.warn("Gemini suggested invalid DisputeType: {}. Falling back to deterministic path.", typeStr);
                return Optional.empty();
            }

            Matcher explanationMatcher = EXPLANATION_PATTERN.matcher(responseJson);
            String explanation = "";
            if (explanationMatcher.find()) {
                explanation = explanationMatcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\").trim();
            }
            if (explanation.isEmpty()) {
                explanation = "AI-recommended classification based on dispute description.";
            }

            return Optional.of(new DisputeClassificationResult(disputeType, explanation));
        } catch (Exception e) {
            logger.warn("Could not parse dispute classification AI response JSON: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Sends matched spending category and card type to Gemini to generate a personalized reward recommendation reason.
     * Returns Optional.empty() if API key is not configured, or if any error occurs.
     */
    public Optional<String> getRewardRecommendationReason(String category, String cardTypeName) {
        if (apiKey.isEmpty()) {
            logger.info("Gemini API key is not configured. Falling back to deterministic reward recommendation reason.");
            return Optional.empty();
        }

        try {
            String prompt = buildRewardPrompt(category, cardTypeName);
            String url = buildRequestUrl();

            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> partsMap = Map.of("parts", List.of(textPart));
            Map<String, Object> requestBody = Map.of("contents", List.of(partsMap));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseRewardReasonResponse(response.getBody());
            } else {
                logger.warn("Gemini API returned non-2xx status: {}", response.getStatusCode());
            }
        } catch (Exception ex) {
            logger.warn("Failed to obtain reward reason from Gemini API: {}. Falling back to deterministic engine.", ex.getMessage());
        }

        return Optional.empty();
    }

    private String buildRewardPrompt(String category, String cardTypeName) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a customer rewards advisor for CardNest Bank.\n");
        sb.append("A customer has significant spending in the '").append(category).append("' category.\n");
        sb.append("Their '").append(cardTypeName).append("' card offers dedicated rewards for this category.\n");
        sb.append("Generate a single, appealing customer-facing sentence explaining why they should leverage this card for rewards.\n");
        sb.append("Respond ONLY with a valid JSON object matching this structure (no markdown formatting, no code blocks):\n");
        sb.append("{\"reason\": \"<single sentence reason>\"}\n");
        return sb.toString();
    }

    private Optional<String> parseRewardReasonResponse(String responseJson) {
        if (responseJson == null || responseJson.isBlank()) {
            return Optional.empty();
        }

        try {
            Matcher matcher = REASON_PATTERN.matcher(responseJson);
            if (matcher.find()) {
                String reason = matcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\").trim();
                if (!reason.isEmpty()) {
                    return Optional.of(reason);
                }
            }
        } catch (Exception e) {
            logger.warn("Could not parse reward reason AI response JSON: {}", e.getMessage());
        }
        return Optional.empty();
    }
}
