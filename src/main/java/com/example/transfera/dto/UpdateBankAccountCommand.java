package com.example.transfera.dto;

import com.example.transfera.domain.account.BankAccount;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBankAccountCommand {

    private UUID id;
    private BankAccount bankAccount;

    public UpdateBankAccountCommand( UUID id, BankAccount bankAccount ) {
        this.id = id;
        this.bankAccount = bankAccount;
    }
}
