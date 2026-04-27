package com.example.transfera.domain.money_request;

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
@Table( name = "money_request" )
public class MoneyRequest {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name = "money_request_id", nullable = false, unique = true, updatable = false )
    private UUID moneyRequestId;

    @Column( name = "created_at", nullable = false )
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column( name = "amount", nullable = false )
    private BigDecimal amount;

    @Column( name = "note" )
    private String note;

    @Column(name = "requester", nullable = false)
    private String requester;   // username of whoever sent the request

    @Column(name = "requestee", nullable = false)
    private String requestee;   // username of whoever was asked to pay

    @Enumerated( EnumType.STRING )
    @Column( name = "status", nullable = false )
    @Builder.Default
    private MoneyRequestStatus status = MoneyRequestStatus.PENDING;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "requester_wallet_id", nullable = false )
    private TransferaWallet requesterWallet;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "payer_wallet_id", nullable = false )
    private TransferaWallet payerWallet;
}