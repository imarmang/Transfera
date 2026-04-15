package com.example.transfera.domain.transaction;

import com.example.transfera.domain.linked_bank_account.LinkedBankAccount;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table( name = "transaction" )
public class Transaction {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( table = "id", nullable = false, unique = true, updatable = false )
    private UUID transactionId;

    @Column( table = "created_at", nullable = false )
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column( table = "amount", nullable = false )
    private BigDecimal amount;

    @Enumerated( EnumType.STRING )
    @Column( table = "type", nullable = false )
    private TransactionType type;

    @Enumerated( EnumType.STRING )
    @Column( table = "status", nullable = false )
    private TransactionStatus status;

    // The user's wallet
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "wallet_id", nullable = false )
    private TransferaWallet transferaWallet;

    // Used when the user ADDS or CASHES OUT money
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "linked_bank_account_id", nullable = true )
    private LinkedBankAccount linkedBankAccount;

    // Used when the user sends or receives money, peer wallet means the other user's wallet number
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "peer_wallet", nullable = true )
    private TransferaWallet peerWallet;
}