package com.example.transfera.dto.MoneyRequestDTO;

import com.example.transfera.domain.money_request.MoneyRequest;
import com.example.transfera.domain.money_request.MoneyRequestStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MoneyRequestDTO {
    private UUID moneyRequestId;
    private LocalDateTime createdAt;
    private BigDecimal amount;
    private String note;
    private MoneyRequestStatus status;
    private String peerName;
    private boolean isPayer;

    public MoneyRequestDTO( MoneyRequest moneyRequest, UUID loggedInWalletId ) {
        this.moneyRequestId = moneyRequest.getMoneyRequestId();
        this.createdAt = moneyRequest.getCreatedAt();
        this.amount = moneyRequest.getAmount();
        this.note = moneyRequest.getNote();
        this.status = moneyRequest.getStatus();
        this.peerName = moneyRequest.getPeerName();
        this.isPayer = moneyRequest.getPayerWallet().getId().equals( loggedInWalletId );
    }
}