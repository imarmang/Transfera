package com.example.transfera.controller;

import com.example.transfera.dto.TransactionDTO.TransactionDTO;
import com.example.transfera.service.transaction.GetTransactionsHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping( "/api/v1/transaction" )
public class TransactionController {

    private final GetTransactionsHistoryService getTransactionsHistoryService;

    public TransactionController( GetTransactionsHistoryService getTransactionsHistoryService ) {
        this.getTransactionsHistoryService = getTransactionsHistoryService;
    }

    @GetMapping( "/history" )
    public ResponseEntity<List<TransactionDTO>> getTransactionsHistory() {
        return getTransactionsHistoryService.execute( null );
    }

}
