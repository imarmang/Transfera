package com.example.transfera.controller;

import com.example.transfera.dto.TransferaWalletDTO.AddMoneyRequestDTO;
import com.example.transfera.dto.TransferaWalletDTO.TransferaWalletDTO;
import com.example.transfera.service.transferaWallet.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping( "api/v1/transfera-wallet" )
public class TransferaWalletController {

    private final GetTransferaWalletService getTransferaWalletService;
    private final AddMoneyTransferaWalletService addMoneyTransferaWalletService;

    public TransferaWalletController( GetTransferaWalletService getTransferaWalletService,
                                      AddMoneyTransferaWalletService addMoneyTransferaWalletService ) {
        this.getTransferaWalletService = getTransferaWalletService;
        this.addMoneyTransferaWalletService = addMoneyTransferaWalletService;
    }

    @GetMapping
    public ResponseEntity<TransferaWalletDTO> getTransferaWallet() {
        return getTransferaWalletService.execute( null );
    }

    @PostMapping( "/add-money" )
    public ResponseEntity<TransferaWalletDTO> addMoney( @RequestBody AddMoneyRequestDTO transferaWalletDTO ) {
        return addMoneyTransferaWalletService.execute( transferaWalletDTO );
    }

    @PostMapping( "/subtract-money" )
    public ResponseEntity<Void> subtractMoney(){
        return null;
    }
}
