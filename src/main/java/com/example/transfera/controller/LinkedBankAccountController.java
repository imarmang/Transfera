package com.example.transfera.controller;


import com.example.transfera.dto.LinkedBankAccountDTO.CreateLinkedBankAccountRequestDTO;
import com.example.transfera.dto.LinkedBankAccountDTO.LinkedBankAccountDTO;
import com.example.transfera.service.linkedBankAccount.CreateLinkedBankAccountService;
import com.example.transfera.service.linkedBankAccount.GetLinkedBankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/linked-bank-account")
public class LinkedBankAccountController {

    private final CreateLinkedBankAccountService createLinkedBankAccountService;
    private final GetLinkedBankAccountService getLinkedBankAccountService;


    public LinkedBankAccountController( CreateLinkedBankAccountService createLinkedBankAccountService,
                                        GetLinkedBankAccountService getLinkedBankAccountService
    ) {
        this.createLinkedBankAccountService = createLinkedBankAccountService;
        this.getLinkedBankAccountService = getLinkedBankAccountService;
    }

    // POST the user's new linked bank account
    @PostMapping
    public ResponseEntity<LinkedBankAccountDTO> createLinkedBankAccount(
            @RequestBody CreateLinkedBankAccountRequestDTO request ) {
        return createLinkedBankAccountService.execute( request );
    }

    // GET the user's all linked bank accounts
    @GetMapping
    public ResponseEntity<List<LinkedBankAccountDTO>> getLinkedBankAccount() {
        return getLinkedBankAccountService.execute( null );
    }

    // DELETE the user's linked bank account
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLinkedBankAccount() {
        return null;
    }
}
