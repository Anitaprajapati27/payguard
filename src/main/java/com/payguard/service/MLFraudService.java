package com.payguard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.time.LocalTime;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class MLFraudService {

    private final RestTemplate restTemplate;
    private static final String ML_SERVICE_URL = "http://localhost:5000/predict";
    private static final double FRAUD_THRESHOLD = 0.75;

    public MLFraudResult checkFraud(Long amountInPaise,
                                     Long walletBalance,
                                     int recentTransactionCount) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("amount", amountInPaise / 100.0);
            request.put("hourOfDay", LocalTime.now().getHour());
            request.put("recentTransactionCount", recentTransactionCount);
            request.put("balancePercentage",
                walletBalance > 0 ? (double) amountInPaise / walletBalance : 0.0);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                ML_SERVICE_URL, entity, Map.class);

            Map<String, Object> result = response.getBody();
            double fraudProbability = ((Number) result.get("fraudProbability")).doubleValue();
            boolean isFraud = fraudProbability > FRAUD_THRESHOLD;
            String riskLevel = (String) result.get("riskLevel");

            System.out.println("🤖 ML Score: " + fraudProbability + " | Risk: " + riskLevel);

            return new MLFraudResult(isFraud, fraudProbability, riskLevel);

        } catch (Exception e) {
            System.out.println("⚠️ ML service unavailable: " + e.getMessage());
            return new MLFraudResult(false, 0.0, "UNKNOWN");
        }
    }

    public record MLFraudResult(boolean isFraud,
                                 double fraudProbability,
                                 String riskLevel) {}
}