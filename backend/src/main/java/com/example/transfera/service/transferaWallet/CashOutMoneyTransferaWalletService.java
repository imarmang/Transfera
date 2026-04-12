package com.example.transfera.service.transferaWallet;

import com.example.transfera.Command;
import com.example.transfera.domain.linked_bank_account.LinkedBankAccountRepository;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.dto.TransferaWalletDTO.CashOutRequestDTO;
import com.example.transfera.dto.TransferaWalletDTO.TransferaWalletDTO;
import com.example.transfera.exceptions.customExceptions.InsufficientBalanceTransferaWalletException;
import com.example.transfera.exceptions.customExceptions.LinkedBankAccountNotFoundException;
import com.example.transfera.exceptions.customExceptions.TransferaWalletNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CashOutMoneyTransferaWalletService implements Command<CashOutRequestDTO, TransferaWalletDTO> {

    private final TransferaWalletRepository transferaWalletRepository;
    private final LinkedBankAccountRepository linkedBankAccountRepository;

    public CashOutMoneyTransferaWalletService( TransferaWalletRepository transferaWalletRepository,
                                                LinkedBankAccountRepository linkedBankAccountRepository ) {
        this.transferaWalletRepository = transferaWalletRepository;
        this.linkedBankAccountRepository = linkedBankAccountRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<TransferaWalletDTO> execute( CashOutRequestDTO input ) {
        String email = ( String ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        linkedBankAccountRepository
                .findByIdAndUserCredentialsEmail( input.getLinkedBankAccountId(), email )
                .orElseThrow( LinkedBankAccountNotFoundException::new );

        TransferaWallet wallet = transferaWalletRepository
                .findByUserCredentialsEmail( email )
                .orElseThrow( TransferaWalletNotFoundException::new );

        if ( input.getAmount().compareTo( BigDecimal.ZERO ) <= 0 ) {
            throw new IllegalArgumentException( "Amount must be greater than zero." );
        }

        if ( wallet.getBalance().compareTo( input.getAmount() ) < 0 ) {
            throw new InsufficientBalanceTransferaWalletException();
        }

        wallet.setBalance( wallet.getBalance().subtract( input.getAmount() ) );
        TransferaWallet updated = transferaWalletRepository.save( wallet );

        return ResponseEntity.ok( new TransferaWalletDTO( updated ) );
    }
}