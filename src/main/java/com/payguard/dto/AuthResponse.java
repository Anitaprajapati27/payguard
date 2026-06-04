package com.payguard.dto;

import lombok.*;

@Getter @Setter @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String message;
}