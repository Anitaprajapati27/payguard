package com.payguard.service;

import com.payguard.entity.FraudAlert;
import com.payguard.repository.FraudAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FraudAlertService {

    private final FraudAlertRepository fraudAlertRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFraudAlert(Long walletId, Long amount, String ruleName) {
        FraudAlert alert = FraudAlert.builder()
                .walletId(walletId)
                .amountInPaise(amount)
                .ruleTriggered(ruleName)
                .build();
        fraudAlertRepository.save(alert);
        System.out.println("✅ Fraud alert saved: " + ruleName);
    }
}
