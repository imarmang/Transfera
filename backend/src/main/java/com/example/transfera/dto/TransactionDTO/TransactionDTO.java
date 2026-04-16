package com.example.transfera.dto.TransactionDTO;

import com.example.transfera.domain.transaction.Transaction;
import com.example.transfera.domain.transaction.TransactionStatus;
import com.example.transfera.domain.transaction.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionDTO {
    private UUID transactionId;
    private LocalDateTime createdAt;
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;

    // Populated for ADD_MONEY, CASH_OUT
    private String bankName;
    private String lastFourDigits;

    // Populated for SEND, RECEIVED
    private String peerName;

    public TransactionDTO( Transaction transaction ) {
        this.transactionId = transaction.getTransactionId();
        this.createdAt = transaction.getCreatedAt();
        this.amount = transaction.getAmount();
        this.transactionType = transaction.getType();
        this.transactionStatus = transaction.getStatus();
        this.peerName = transaction.getPeerName();

        if ( transaction.getLinkedBankAccount() != null ) {
            this.bankName = transaction.getLinkedBankAccount().getBankName();
            this.lastFourDigits = transaction.getLinkedBankAccount().getAccountNumber()
                    .substring( transaction.getLinkedBankAccount().getAccountNumber().length() - 4 );
        }
    }
}
