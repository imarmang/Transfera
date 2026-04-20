package com.example.transfera.dto.TransferaWalletDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SendMoneyRequestDTO {
    private String recipientUsername;
    private BigDecimal amount;
    private String note;

}
