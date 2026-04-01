package com.example.transfera.domain.transfera_wallet;

import com.example.transfera.domain.user.UserCredentials;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

// TODO: change bank account to Transfera Wallet

// bank account entity, which holds the account number, balance,
// there is a one-to-one relationship between the user and the account
@Entity
@Data
@Table( name="transfera_wallet" )
public class TransferaWallet {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name="id" )
    private UUID id;

    @Column( name="wallet_number", unique = true, nullable = false )
    private String walletNumber;

    @Column( name="balance" )
    private BigDecimal balance;

    @OneToOne
    @JoinColumn( name="user_id" )
    private UserCredentials userCredentials;
}
