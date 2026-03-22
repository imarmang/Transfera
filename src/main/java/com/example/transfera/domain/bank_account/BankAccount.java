package com.example.transfera.domain.bank_account;

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
@Table( name="bank_account" )
public class BankAccount {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name="id" )
    private UUID id;

    @Column( name="account_number" )
    private String accountNumber;

    @Column( name="balance" )
    private BigDecimal balance;

    @OneToOne
    @JoinColumn( name="user_id" )

    private UserCredentials userCredentials;
}
