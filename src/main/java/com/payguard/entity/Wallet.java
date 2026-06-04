package com.payguard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Wallet {

    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id", nullable = false)
private Long id;

    // One user has one wallet
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // IMPORTANT: Store in PAISE (not rupees) to avoid floating point errors
    // ₹100 = 10000 paise
    @Column(nullable = false)
    private Long balance;   // in paise

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.balance = 0L;  // start with zero balance
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}