package com.example.transfera.dto.MoneyRequestDTO;

import com.example.transfera.domain.money_request.MoneyRequestStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class RespondToMoneyRequestDTO {
    private UUID moneyRequestId;
    private MoneyRequestStatus response; // APPROVED or DECLINED
}