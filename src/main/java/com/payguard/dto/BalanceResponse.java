package com.payguard.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class BalanceResponse implements Serializable {
    private String email;
    private Long balanceInPaise;
    private Double balanceInRupees;
}