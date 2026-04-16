package com.example.transfera.domain.transaction;

import com.example.transfera.domain.linked_bank_account.LinkedBankAccount;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;

import java.math.BigDecimal;

// USE THE FACTORY PATTERN TO CREATE THE DIFFERENT TYPES OF TRANSACTIONS
public class TransactionFactory {

    // When the user adds money to their account from their bank account
    public static Transaction addMoney( TransferaWallet wallet,
                                       BigDecimal amount,
                                       LinkedBankAccount linkedBankAccount ) {

        return Transaction.builder()
                .transferaWallet( wallet )
                .amount( amount )
                .type( TransactionType.ADD_MONEY )
                .status( TransactionStatus.PENDING )
                .linkedBankAccount( linkedBankAccount )
                .build();

    }

    // When the user sends money to their bank account
    public static Transaction cashOut( TransferaWallet wallet,
                                       BigDecimal amount,
                                       LinkedBankAccount linkedBankAccount ) {

        return Transaction.builder()
                .transferaWallet( wallet )
                .amount( amount )
                .type( TransactionType.CASH_OUT )
                .status( TransactionStatus.PENDING )
                .linkedBankAccount( linkedBankAccount )
                .build();

    }

    // When the user sends money to another user
    public static Transaction send( TransferaWallet wallet,
                                    BigDecimal amount,
                                    String peerName ) {

        return Transaction.builder()
                .transferaWallet( wallet )
                .amount( amount )
                .type( TransactionType.SEND )
                .status( TransactionStatus.PENDING )
                .peerName( peerName )
                .build();

    }

    // When the user receives money from another user
    public static Transaction received( TransferaWallet wallet,
                                        BigDecimal amount,
                                        String peerName ) {
        return Transaction.builder()
                .transferaWallet( wallet )
                .amount( amount )
                .type( TransactionType.RECEIVED )
                .status( TransactionStatus.PENDING )
                .peerName( peerName )
                .build();

    }
}
