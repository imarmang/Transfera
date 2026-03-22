package com.example.transfera.dto.LinkedBankAccountDTO;


public record CreateLinkedBankAccountRequestDTO(
        String bankName,
        String accountHolderName,
        String accountNumber,
        String routingNumber,
        String accountType
        )
{}

