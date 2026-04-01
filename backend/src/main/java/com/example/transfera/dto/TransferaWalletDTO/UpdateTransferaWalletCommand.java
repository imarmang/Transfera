package com.example.transfera.dto.TransferaWalletDTO;

import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateTransferaWalletCommand {

    private UUID id;
    private TransferaWallet bankAccount;

    public UpdateTransferaWalletCommand( UUID id, TransferaWallet bankAccount ) {
        this.id = id;
        this.bankAccount = bankAccount;
    }
}
