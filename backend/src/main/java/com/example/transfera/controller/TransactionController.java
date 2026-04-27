package com.example.transfera.controller;

import com.example.transfera.dto.MoneyRequestDTO.CreateMoneyRequestDTO;
import com.example.transfera.dto.MoneyRequestDTO.MoneyRequestDTO;
import com.example.transfera.dto.MoneyRequestDTO.RespondToMoneyRequestDTO;
import com.example.transfera.dto.TransactionDTO.ActivityFeedDTO;
import com.example.transfera.service.transaction.CreateMoneyRequestService;
import com.example.transfera.service.transaction.GetTransactionsHistoryService;
import com.example.transfera.service.transaction.RespondToMoneyRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api/v1/transaction" )
public class TransactionController {

    private final GetTransactionsHistoryService getTransactionsHistoryService;
    private final CreateMoneyRequestService createMoneyRequestService;
    private final RespondToMoneyRequestService respondToMoneyRequestService;


    public TransactionController( GetTransactionsHistoryService getTransactionsHistoryService,
                                  CreateMoneyRequestService createMoneyRequestService,
                                  RespondToMoneyRequestService respondToMoneyRequestService ) {
        this.getTransactionsHistoryService = getTransactionsHistoryService;
        this.createMoneyRequestService = createMoneyRequestService;
        this.respondToMoneyRequestService = respondToMoneyRequestService;
    }

    @GetMapping( "/history" )
    public ResponseEntity<ActivityFeedDTO> getTransactionsHistory() {
        return getTransactionsHistoryService.execute( null );
    }

    @PostMapping( "/request" )
    public ResponseEntity<MoneyRequestDTO> createMoneyRequest( @RequestBody CreateMoneyRequestDTO request ) {
        return createMoneyRequestService.execute( request );
    }

    @PostMapping( "/request/respond" )
    public ResponseEntity<Void> respondToMoneyRequest( @RequestBody RespondToMoneyRequestDTO request ) {
        return respondToMoneyRequestService.execute( request );
    }
}
