package com.example.transfera.controller;

import com.example.transfera.dto.TransferaWalletDTO.AddMoneyRequestDTO;
import com.example.transfera.dto.TransferaWalletDTO.CashOutRequestDTO;
import com.example.transfera.dto.TransferaWalletDTO.SendMoneyRequestDTO;
import com.example.transfera.dto.TransferaWalletDTO.TransferaWalletDTO;
import com.example.transfera.service.transferaWallet.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping( "api/v1/transfera-wallet" )
public class TransferaWalletController {

    private final GetTransferaWalletService getTransferaWalletService;
    private final AddMoneyTransferaWalletService addMoneyTransferaWalletService;
    private final CashOutMoneyTransferaWalletService subtractMoneyTransferaWalletService;
    private final SendMoneyTransferaWalletService sendMoneyTransferaWalletService;

    public TransferaWalletController(GetTransferaWalletService getTransferaWalletService,
                                     AddMoneyTransferaWalletService addMoneyTransferaWalletService,
                                     CashOutMoneyTransferaWalletService subtractMoneyTransferaWalletService, SendMoneyTransferaWalletService sendMoneyTransferaWalletService) {
        this.getTransferaWalletService = getTransferaWalletService;
        this.addMoneyTransferaWalletService = addMoneyTransferaWalletService;
        this.subtractMoneyTransferaWalletService = subtractMoneyTransferaWalletService;
        this.sendMoneyTransferaWalletService = sendMoneyTransferaWalletService;
    }

    @GetMapping
    public ResponseEntity<TransferaWalletDTO> getTransferaWallet() {
        return getTransferaWalletService.execute( null );
    }

    @PostMapping( "/add-money" )
    public ResponseEntity<TransferaWalletDTO> addMoney( @RequestBody AddMoneyRequestDTO transferaWalletDTO ) {
        return addMoneyTransferaWalletService.execute( transferaWalletDTO );
    }

    @PostMapping( "/cash-out" )
    public ResponseEntity<TransferaWalletDTO> cashOut( @RequestBody CashOutRequestDTO request ) {
        return subtractMoneyTransferaWalletService.execute( request );
    }

    @PostMapping( "/send-money" )
    public ResponseEntity<TransferaWalletDTO> sendMoney( @RequestBody SendMoneyRequestDTO request ) {
        return sendMoneyTransferaWalletService.execute( request );
    }
}
