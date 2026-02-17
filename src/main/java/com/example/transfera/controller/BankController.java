package com.example.transfera.controller;

import com.example.transfera.dto.BankAccountDTO;
import com.example.transfera.dto.TransferDTO;
import com.example.transfera.service.transfer.TransferService;
import com.example.transfera.service.accounts.GetAccountsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BankController {

    private final TransferService transferService;
    private final GetAccountsService getAccountsService;

    public BankController(TransferService transferService, GetAccountsService getAccountsService) {
        this.transferService = transferService;
        this.getAccountsService = getAccountsService;
    }

    // RETURNS ALL THE ACCOUNTS
    @GetMapping( "/accounts" )
    public ResponseEntity<List<BankAccountDTO>> getAccounts() {
        return getAccountsService.execute( null );
    }

    @PostMapping( "/transfer" )
    public ResponseEntity<String> transfer( @RequestBody TransferDTO transferDTO ) {
        return transferService.execute( transferDTO );
    }
}
