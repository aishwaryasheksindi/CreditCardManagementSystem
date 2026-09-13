package com.crimsonlogic.creditcardmanagementsystem.client;

import com.crimsonlogic.creditcardmanagementsystem.dto.EmiTenureOptionDto;
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
}
