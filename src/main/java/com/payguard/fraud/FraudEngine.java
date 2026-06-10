package com.payguard.fraud;

import com.payguard.entity.Wallet;
import com.payguard.service.FraudAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudEngine {

    private final List<FraudRule> rules;
    private final FraudAlertService fraudAlertService;

    public String checkFraud(Long senderWalletId,
                              Long amountInPaise,
                              Wallet senderWallet) {

        for (FraudRule rule : rules) {
            if (rule.isFraudulent(senderWalletId, amountInPaise, senderWallet)) {
                fraudAlertService.saveFraudAlert(
                    senderWalletId, amountInPaise, rule.getRuleName());
                return rule.getRuleName();
            }
        }
        return null;
    }
}