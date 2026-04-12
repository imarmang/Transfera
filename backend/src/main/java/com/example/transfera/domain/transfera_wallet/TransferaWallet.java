package com.example.transfera.domain.transfera_wallet;

import com.example.transfera.domain.user.UserCredentials;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@Table( name="transfera_wallet" )
public class TransferaWallet {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name="id" )
    private UUID id;

    // Optimistic locking — prevents lost updates when concurrent transactions
    // modify the same wallet simultaneously. Hibernate auto-increments this
    // value on every save and throws OptimisticLockException if two transactions
    // try to update the same version at the same time.
    @Version
    @Column( name = "version" )
    private Long version;

    @Column( name="wallet_number", unique = true, nullable = false )
    private String walletNumber;

    @Column( name="balance" )
    private BigDecimal balance;

    @OneToOne
    @JoinColumn( name="user_id" )
    private UserCredentials userCredentials;
}