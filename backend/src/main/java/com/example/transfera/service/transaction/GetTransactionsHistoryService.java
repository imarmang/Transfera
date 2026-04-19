package com.example.transfera.service.transaction;

import com.example.transfera.Query;
import com.example.transfera.domain.transaction.Transaction;
import com.example.transfera.domain.transaction.TransactionRepository;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.dto.TransactionDTO.TransactionDTO;
import com.example.transfera.exceptions.customExceptions.TransferaWalletNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

// Without @Transactional, the JPA session closes as soon as the query returns the Transaction list.
// Then when the DTO constructor tries to call transaction.getLinkedBankAccount().getBankName(),
// the session is already closed and you get a LazyInitializationException.
// @Transactional keeps the session alive long enough to resolve the lazy relationship.
@Service
public class GetTransactionsHistoryService implements Query<Void, List<TransactionDTO>> {

    private final TransactionRepository transactionRepository;
    private final TransferaWalletRepository transferaWalletRepository;


    public GetTransactionsHistoryService( TransactionRepository transactionRepository,
                                         TransferaWalletRepository transferaWalletRepository ) {
        this.transactionRepository = transactionRepository;
        this.transferaWalletRepository = transferaWalletRepository;
    }

    @Transactional
    @Override
    public ResponseEntity<List<TransactionDTO>> execute( Void input ) {
        String email = ( String ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TransferaWallet transferaWallet = transferaWalletRepository
                .findByUserCredentialsEmail( email )
                .orElseThrow( TransferaWalletNotFoundException::new );

        List<Transaction> transactions = transactionRepository.findAllByTransferaWallet_IdOrderByCreatedAtDesc( transferaWallet.getId() );

        List<TransactionDTO> result = transactions.stream()
                .map( TransactionDTO::new ).toList();


        return ResponseEntity.ok( result );
    }
}
