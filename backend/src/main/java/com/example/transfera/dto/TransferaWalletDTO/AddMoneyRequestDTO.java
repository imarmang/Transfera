package com.example.transfera.dto.TransferaWalletDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class AddMoneyRequestDTO {

    @JsonProperty( "linkedBankAccountId" )
    private UUID linkedBankAccountId;

    @JsonProperty( "amount" )
    private BigDecimal amount;
}