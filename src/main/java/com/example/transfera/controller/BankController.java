package com.example.transfera.controller;

import com.example.transfera.domain.account.BankAccount;
import com.example.transfera.dto.BankAccountDTO;
import com.example.transfera.dto.UpdateBankAccountCommand;
import com.example.transfera.service.accounts.DeleteBankAccountService;
import com.example.transfera.service.accounts.GetBankAccountService;
import com.example.transfera.service.accounts.GetBankAccountsService;
import com.example.transfera.service.accounts.UpdateBankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping( "api/v1/bankaccounts" )
public class BankController {

    private final GetBankAccountsService getBankAccountsService;
    private final GetBankAccountService getBankAccountService;
    private final UpdateBankAccountService updateBankAccountService;
    private final DeleteBankAccountService deleteBankAccountService;


    public BankController(GetBankAccountsService getBankAccountsService, GetBankAccountService getBankAccountService, UpdateBankAccountService updateBankAccountService, DeleteBankAccountService deleteBankAccountService) {
        this.getBankAccountsService = getBankAccountsService;
        this.getBankAccountService = getBankAccountService;
        this.updateBankAccountService = updateBankAccountService;
        this.deleteBankAccountService = deleteBankAccountService;
    }

    // RETURNS ALL THE ACCOUNTS
    @GetMapping( "/accounts" )
    public ResponseEntity<List<BankAccountDTO>> getAccounts() {
        return getBankAccountsService.execute( null );
    }

    @GetMapping( "/account/{id}" )
    public ResponseEntity<BankAccountDTO> getBankAccountById(@PathVariable UUID id ) {
        return getBankAccountService.execute( id );
    }
//    FUTURE IMPLEMENTATION IF I ADD CATEGORIES
//    @GetMapping( "account/search" )
//    public ResponseEntity<List<BankAccountDTO>> searchAccountByName( @RequestParam String name ) {}

    @PutMapping( "/account/{id}" )
    public ResponseEntity<BankAccountDTO> updateBankAccount( @PathVariable UUID id, @RequestBody BankAccount bankAccount ) {
        return updateBankAccountService.execute( new UpdateBankAccountCommand( id, bankAccount ) );
    }

    @DeleteMapping( "/account/{id}" )
    public ResponseEntity<Void> deleteBankAccountById( @PathVariable UUID id ) {
        return deleteBankAccountService.execute( id );
    }

}
