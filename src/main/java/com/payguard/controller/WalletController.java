package com.payguard.controller;

import com.payguard.dto.BalanceResponse;
import com.payguard.entity.User;
import com.payguard.entity.Wallet;
import com.payguard.repository.FraudAlertRepository;
import com.payguard.repository.UserRepository;
import com.payguard.repository.WalletRepository;
import com.payguard.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Tag(name = "Wallet", description = "Wallet and transaction APIs")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final FraudAlertRepository fraudAlertRepository;

    @Operation(summary = "Get wallet balance")
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(walletService.getBalance(userDetails.getUsername()));
    }

    @Operation(summary = "Add money to wallet")
    @PostMapping("/add-money")
    public ResponseEntity<Map<String, Object>> addMoney(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Double amount) {
        return ResponseEntity.ok(walletService.addMoney(userDetails.getUsername(), amount));
    }

    @Operation(summary = "Transfer money to another user")
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String receiverEmail,
            @RequestParam Double amount,
            @RequestParam(required = false) String idempotencyKey) {
        return ResponseEntity.ok(walletService.transfer(
            userDetails.getUsername(), receiverEmail, amount, idempotencyKey));
    }

    @Operation(summary = "Get transaction history")
    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Object>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(
            walletService.getTransactionHistory(userDetails.getUsername(), page));
    }

    @Operation(summary = "Get fraud alerts for wallet")
    @GetMapping("/fraud-alerts")
    public ResponseEntity<?> getFraudAlerts(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return ResponseEntity.ok(
            fraudAlertRepository.findByWalletId(wallet.getId()));
    }
}