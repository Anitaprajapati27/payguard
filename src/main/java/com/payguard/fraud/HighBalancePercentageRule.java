package com.payguard.fraud;

import com.payguard.entity.Wallet;
import org.springframework.stereotype.Component;

@Component
public class HighBalancePercentageRule implements FraudRule {

    // Flag if transfer is more than 90% of wallet balance
    private static final double THRESHOLD_PERCENTAGE = 0.90;

    @Override
    public boolean isFraudulent(Long senderWalletId,
                                 Long amountInPaise,
                                 Wallet senderWallet) {

        if (senderWallet.getBalance() == 0) return false;

        double percentage = (double) amountInPaise / senderWallet.getBalance();
        return percentage > THRESHOLD_PERCENTAGE;
    }

    @Override
    public String getRuleName() {
        return "HIGH_BALANCE_PERCENTAGE";
    }
}