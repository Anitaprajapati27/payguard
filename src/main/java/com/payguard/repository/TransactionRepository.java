package com.payguard.repository;

import com.payguard.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Check if idempotency key already exists
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    // Get all transactions for a wallet (paginated)
    Page<Transaction> findBySenderWalletIdOrReceiverWalletId(
        Long senderWalletId,
        Long receiverWalletId,
        Pageable pageable
    );
}