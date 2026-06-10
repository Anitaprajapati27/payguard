package com.payguard.fraud;

import com.payguard.entity.Wallet;
import org.springframework.stereotype.Component;

@Component
public class LargeAmountRule implements FraudRule {

    // Flag if transfer > ₹50,000
    private static final Long THRESHOLD = 5_000_000L; // in paise

    @Override
    public boolean isFraudulent(Long senderWalletId,
                                 Long amountInPaise,
                                 Wallet senderWallet) {
        return amountInPaise > THRESHOLD;
    }

    @Override
    public String getRuleName() {
        return "LARGE_AMOUNT";
    }
}
