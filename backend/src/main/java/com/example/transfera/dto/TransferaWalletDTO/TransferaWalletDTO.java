package com.example.transfera.dto.TransferaWalletDTO;

import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

// Return always the DTO, return object
@Data
public class TransferaWalletDTO {
    private UUID accountId;
    private String walletNumber;
    private BigDecimal balance;

    public TransferaWalletDTO( TransferaWallet transferaWallet ) {
        this.accountId = transferaWallet.getId();
        this.walletNumber = transferaWallet.getWalletNumber();
        this.balance = transferaWallet.getBalance();
    }
}
