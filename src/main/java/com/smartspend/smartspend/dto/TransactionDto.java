package com.smartspend.smartspend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDto {
    private LocalDate date;
    private String description;
    private String category;
    private String accountName;
    private BigDecimal amount;
    private String type; // "INCOME" or "EXPENSE"

    public TransactionDto(LocalDate date, String description, String category, String accountName, BigDecimal amount, String type) {
        this.date = date;
        this.description = description;
        this.category = category;
        this.accountName = accountName;
        this.amount = amount;
        this.type = type;
    }

    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getAccountName() { return accountName; }
    public BigDecimal getAmount() { return amount; }
    public String getType() { return type; }
}
