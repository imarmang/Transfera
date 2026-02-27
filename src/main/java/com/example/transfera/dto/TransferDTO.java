package com.example.transfera.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDTO {

    private String fromUser;
    private String toUser;
    private BigDecimal amount;


}
