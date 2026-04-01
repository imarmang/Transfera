package com.example.transfera.service.transferaWallet;

import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.domain.user.UserCredentials;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

// Creates a wallet number and sets the balance to zero and saves the information inside the DB
@Service
public class CreateTransferaWalletService {

    private final TransferaWalletRepository transferaWalletRepository;

    public CreateTransferaWalletService( TransferaWalletRepository transferaWalletRepository ) {
        this.transferaWalletRepository = transferaWalletRepository;
    }

    public TransferaWallet execute( UserCredentials userCredentials ) {
        TransferaWallet wallet = new TransferaWallet();
        wallet.setBalance( BigDecimal.ZERO.setScale( 2 ) );
        wallet.setWalletNumber( generateWalletNumber() );
        wallet.setUserCredentials( userCredentials );
        return transferaWalletRepository.save( wallet );
    }

    private String generateWalletNumber() {
        Random random = new Random();
        long number = ( long ) ( random.nextDouble() * 9_000_000_000L ) + 1_000_000_000L;
        return String.valueOf( number );
    }
}