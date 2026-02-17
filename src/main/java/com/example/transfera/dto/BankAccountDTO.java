package com.example.transfera.dto;

import com.example.transfera.domain.account.BankAccount;
import lombok.Data;

@Data
public class BankAccountDTO {
    private String name;
    private double balance;

    public BankAccountDTO( BankAccount bankAccount ) {
        this.name = bankAccount.getName();
        this.balance = bankAccount.getBalance();
    }
}
