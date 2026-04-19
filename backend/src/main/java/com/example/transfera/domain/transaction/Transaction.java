package com.example.transfera.domain.transaction;

import com.example.transfera.domain.linked_bank_account.LinkedBankAccount;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table( name = "transaction" )
public class Transaction {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name = "transaction_id", nullable = false, unique = true, updatable = false )
    private UUID transactionId;

    @Column( name = "created_at", nullable = false )
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column( name = "amount", nullable = false )
    private BigDecimal amount;

    @Enumerated( EnumType.STRING )
    @Column( name = "type", nullable = false )
    private TransactionType type;

    @Enumerated( EnumType.STRING )
    @Column( name = "status", nullable = false )
    private TransactionStatus status;

    // The user's wallet
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "wallet_id", nullable = false )
    private TransferaWallet transferaWallet;

    // Used when the user ADDS or CASHES OUT money
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "linked_bank_account_id", nullable = true )
    private LinkedBankAccount linkedBankAccount;

    @Column( name = "peer_name" )
    private String peerName;
}