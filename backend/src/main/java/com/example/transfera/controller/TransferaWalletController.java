package com.example.transfera.controller;

import com.example.transfera.dto.TransferaWalletDTO.TransferaWalletDTO;
import com.example.transfera.service.transferaWallet.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping( "api/v1/transfera-wallet" )
public class TransferaWalletController {

    private final GetTransferaWalletService getTransferaWalletService;


    public TransferaWalletController( GetTransferaWalletService getTransferaWalletService ) {
        this.getTransferaWalletService = getTransferaWalletService;
    }

    @GetMapping
    public ResponseEntity<TransferaWalletDTO> getBankAccountById() {
        return getTransferaWalletService.execute( null );
    }

    @PostMapping( "/add-money" )
    public ResponseEntity<Void> addMoney(){
        return null;
    }

    @PostMapping( "/subtract-money" )
    public ResponseEntity<Void> subtractMoney(){
        return null;
    }
}
