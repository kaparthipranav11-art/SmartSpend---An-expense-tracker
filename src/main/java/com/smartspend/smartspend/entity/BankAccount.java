package com.smartspend.smartspend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_accounts")
@Data
@NoArgsConstructor
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    private AccountType accountType; // SAVINGS, CURRENT, WALLET

    private BigDecimal balance;
    private String currency = "INR";
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
}