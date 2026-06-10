package com.payguard.fraud;

import com.payguard.entity.Wallet;
import org.springframework.stereotype.Component;
import java.time.LocalTime;

@Component
public class OddHoursRule implements FraudRule {

    // Flag large transfers between 1AM - 4AM
    private static final Long THRESHOLD = 1_000_000L; // ₹10,000 in paise

    @Override
    public boolean isFraudulent(Long senderWalletId,
                                 Long amountInPaise,
                                 Wallet senderWallet) {

        LocalTime now = LocalTime.now();
        boolean isOddHours = now.isAfter(LocalTime.of(1, 0))
                          && now.isBefore(LocalTime.of(4, 0));

        return isOddHours && amountInPaise > THRESHOLD;
    }

    @Override
    public String getRuleName() {
        return "ODD_HOURS";
    }
}