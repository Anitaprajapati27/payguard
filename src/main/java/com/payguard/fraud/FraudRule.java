package com.payguard.fraud;

import com.payguard.entity.Wallet;

public interface FraudRule {
    boolean isFraudulent(Long senderWalletId, Long amountInPaise, Wallet senderWallet);
    String getRuleName();
}