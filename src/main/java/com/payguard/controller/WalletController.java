package com.payguard.controller;

import com.payguard.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(walletService.getBalance(userDetails.getUsername()));
    }

    @PostMapping("/add-money")
    public ResponseEntity<Map<String, Object>> addMoney(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Double amount) {
        return ResponseEntity.ok(walletService.addMoney(userDetails.getUsername(), amount));
    }
}