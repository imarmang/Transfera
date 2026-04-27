package com.example.transfera.dto.MoneyRequestDTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateMoneyRequestDTO {
    private String recipientUsername;
    private BigDecimal amount;
    private String note;
}