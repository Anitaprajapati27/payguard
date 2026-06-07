package com.payguard.service;

import com.payguard.entity.Transaction;
import com.payguard.entity.Transaction.TransactionStatus;
import com.payguard.entity.User;
import com.payguard.entity.Wallet;
import com.payguard.exception.InsufficientFundsException;
import com.payguard.repository.TransactionRepository;
import com.payguard.repository.UserRepository;
import com.payguard.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    // Update the class — add transactionRepository
    private final TransactionRepository transactionRepository;

    @Transactional
public Map<String, Object> transfer(String senderEmail,
                                     String receiverEmail,
                                     Double amountInRupees,
                                     String idempotencyKey) {

    // 1. Check idempotency — same request sent twice?
    if (idempotencyKey != null) {
        Optional<Transaction> existing =
            transactionRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return Map.of(
                "message", "Duplicate request — transaction already processed",
                "transactionId", existing.get().getId()
            );
        }
    }

    // 2. Generate idempotency key if not provided
    String finalKey = idempotencyKey != null ?
        idempotencyKey : UUID.randomUUID().toString();

    // 3. Validate amount
    if (amountInRupees <= 0)
        throw new RuntimeException("Amount must be positive");

    if (senderEmail.equals(receiverEmail))
        throw new RuntimeException("Cannot transfer to yourself");

    // 4. Load sender and receiver
    User sender = userRepository.findByEmail(senderEmail)
            .orElseThrow(() -> new RuntimeException("Sender not found"));

    User receiver = userRepository.findByEmail(receiverEmail)
            .orElseThrow(() -> new RuntimeException("Receiver not found"));

    // 5. Lock BOTH wallets (pessimistic lock — nobody else can touch them)
    Wallet senderWallet = walletRepository.findByUserId(sender.getId())
            .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

    Wallet receiverWallet = walletRepository.findByUserId(receiver.getId())
            .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

    // 6. Convert to paise
    Long amountInPaise = Math.round(amountInRupees * 100);

    // 7. Check balance
    if (senderWallet.getBalance() < amountInPaise) {
        throw new InsufficientFundsException(
            "Insufficient balance. Available: ₹" +
            senderWallet.getBalance() / 100.0
        );
    }

    // 8. Debit sender, Credit receiver
    senderWallet.setBalance(senderWallet.getBalance() - amountInPaise);
    receiverWallet.setBalance(receiverWallet.getBalance() + amountInPaise);

    walletRepository.save(senderWallet);
    walletRepository.save(receiverWallet);

    // 9. Record transaction
    Transaction transaction = Transaction.builder()
            .senderWalletId(senderWallet.getId())
            .receiverWalletId(receiverWallet.getId())
            .amount(amountInPaise)
            .status(TransactionStatus.SUCCESS)
            .idempotencyKey(finalKey)
            .build();
    transactionRepository.save(transaction);

    return Map.of(
        "message", "Transfer successful",
        "amountTransferred", amountInRupees,
        "senderNewBalance", senderWallet.getBalance() / 100.0,
        "transactionId", transaction.getId()
    );
}

// Get transaction history
public Map<String, Object> getTransactionHistory(String email, int page) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Wallet wallet = walletRepository.findByUserId(user.getId())
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

    Page<Transaction> transactions = transactionRepository
        .findBySenderWalletIdOrReceiverWalletId(
            wallet.getId(),
            wallet.getId(),
            PageRequest.of(page, 10, Sort.by("createdAt").descending())
        );

    return Map.of(
        "transactions", transactions.getContent(),
        "totalPages", transactions.getTotalPages(),
        "currentPage", page
    );
}
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