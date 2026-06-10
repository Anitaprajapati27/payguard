package com.payguard.fraud;

import com.payguard.entity.Wallet;
import com.payguard.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RapidFireRule implements FraudRule {

    private final TransactionRepository transactionRepository;

    // Flag if more than 5 transactions in last 5 minutes
    private static final int MAX_TRANSACTIONS = 5;

    @Override
    public boolean isFraudulent(Long senderWalletId,
                                 Long amountInPaise,
                                 Wallet senderWallet) {

        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        int recentCount = transactionRepository
            .countBySenderWalletIdAndCreatedAtAfter(senderWalletId, fiveMinutesAgo);

        return recentCount >= MAX_TRANSACTIONS;
    }

    @Override
    public String getRuleName() {
        return "RAPID_FIRE";
    }
}