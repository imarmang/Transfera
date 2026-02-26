package com.example.transfera.dto.BankAccountDTO;

import com.example.transfera.domain.bank_account.BankAccount;
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
