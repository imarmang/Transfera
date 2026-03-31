package com.example.transfera.dto.LinkedBankAccountDTO;

import com.example.transfera.domain.linked_bank_account.LinkedBankAccount;
import lombok.Data;

import java.util.UUID;

@Data
public class LinkedBankAccountDTO {

    private UUID id;
    private String bankName;
    private String accountHolderName;

    private String lastFourDigitsAccountNumber;
    private String routingNumber;
    private String accountType;

    public LinkedBankAccountDTO(LinkedBankAccount account ) {
        this.id = account.getId();
        this.bankName = account.getBankName();
        this.accountHolderName = account.getAccountHolderName();
        this.lastFourDigitsAccountNumber = account.getAccountNumber()
                .substring( account.getAccountNumber().length()- 4 );
        this.routingNumber = account.getRoutingNumber();
        this.accountType = account.getAccountType();
    }


}
