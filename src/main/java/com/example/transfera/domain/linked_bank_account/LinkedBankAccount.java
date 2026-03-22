package com.example.transfera.domain.linked_bank_account;

import com.example.transfera.domain.user.UserCredentials;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Table( name = "linked_bank_account" )
@NoArgsConstructor
public class LinkedBankAccount {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name = "id", updatable = false, nullable = false )
    private UUID id;

    @Column( name = "bank_name", nullable = false )
    private String bankName;

    @Column( name = "account_holder_name", nullable = false )
    private String accountHolderName;

    @Column( name = "account_number", nullable = false )
    private String accountNumber;

    @Column( name = "routing_number", nullable = false )
    private String routingNumber;

    @Column ( name = "account_type", nullable = false ) // CHECKING OR SAVINGS
    private String accountType;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "user_id", nullable = false )
    private UserCredentials userCredentials;

    public LinkedBankAccount( UserCredentials userCredentials,
                              String bankName,
                              String accountHolderName,
                              String accountNumber,
                              String routingNumber,
                              String accountType
                               ) {
        this.userCredentials = userCredentials;
        this.bankName = bankName;
        this.accountHolderName = accountHolderName;
        this.accountNumber = accountNumber;
        this.routingNumber = routingNumber;
        this.accountType = accountType;
    }
}
