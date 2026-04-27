package com.example.transfera.dto.TransactionDTO;

import com.example.transfera.dto.MoneyRequestDTO.MoneyRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ActivityFeedDTO {
    private List<MoneyRequestDTO> pendingRequests;
    private List<TransactionDTO> transactions;
}