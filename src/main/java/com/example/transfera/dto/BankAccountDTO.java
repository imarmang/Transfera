package com.example.transfera.dto;

import com.example.transfera.domain.account.BankAccount;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

// Return always the DTO, return object
@Data
public class BankAccountDTO {
    private UUID accountId;
    private String accountNumber;
    private BigDecimal balance;

    public BankAccountDTO( BankAccount bankAccount ) {
        this.accountId = bankAccount.getId();
        this.accountNumber = bankAccount.getAccountNumber();
        this.balance = bankAccount.getBalance();
    }
}
