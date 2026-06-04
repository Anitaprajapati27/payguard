package com.payguard.service;

import com.payguard.entity.*;
import com.payguard.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    // Get balance (converted from paise to rupees for display)
    public Map<String, Object> getBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return Map.of(
            "email", email,
            "balanceInPaise", wallet.getBalance(),
            "balanceInRupees", wallet.getBalance() / 100.0
        );
    }

    // Add money to wallet (amount passed in rupees, stored in paise)
    @Transactional
    public Map<String, Object> addMoney(String email, Double amountInRupees) {

        if (amountInRupees <= 0) throw new RuntimeException("Amount must be positive");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Long amountInPaise = Math.round(amountInRupees * 100);
        wallet.setBalance(wallet.getBalance() + amountInPaise);
        walletRepository.save(wallet);

        return Map.of(
            "message", "Money added successfully",
            "addedAmount", amountInRupees,
            "newBalance", wallet.getBalance() / 100.0
        );
    }
}