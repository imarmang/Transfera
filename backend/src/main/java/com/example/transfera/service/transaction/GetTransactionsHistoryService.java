package com.example.transfera.service.transaction;

import com.example.transfera.Query;
import com.example.transfera.domain.money_request.MoneyRequest;
import com.example.transfera.domain.money_request.MoneyRequestRepository;
import com.example.transfera.domain.money_request.MoneyRequestStatus;
import com.example.transfera.domain.transaction.TransactionRepository;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.dto.MoneyRequestDTO.MoneyRequestDTO;
import com.example.transfera.dto.TransactionDTO.ActivityFeedDTO;
import com.example.transfera.dto.TransactionDTO.TransactionDTO;
import com.example.transfera.exceptions.customExceptions.TransferaWalletNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetTransactionsHistoryService implements Query<Void, ActivityFeedDTO> {

    private final TransactionRepository transactionRepository;
    private final TransferaWalletRepository transferaWalletRepository;
    private final MoneyRequestRepository moneyRequestRepository;

    public GetTransactionsHistoryService( TransactionRepository transactionRepository,
                                          TransferaWalletRepository transferaWalletRepository,
                                          MoneyRequestRepository moneyRequestRepository ) {
        this.transactionRepository = transactionRepository;
        this.transferaWalletRepository = transferaWalletRepository;
        this.moneyRequestRepository = moneyRequestRepository;
    }

    @Transactional
    @Override
    public ResponseEntity<ActivityFeedDTO> execute( Void input ) {
        String email = ( String ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TransferaWallet transferaWallet = transferaWalletRepository
                .findByUserCredentialsEmail( email )
                .orElseThrow( TransferaWalletNotFoundException::new );

        // Fetch transactions sorted by createdAt desc
        List<TransactionDTO> transactions = transactionRepository
                .findAllByTransferaWallet_IdOrderByCreatedAtDesc( transferaWallet.getId() )
                .stream()
                .map( TransactionDTO::new )
                .toList();

        // Fetch only PENDING money requests for this wallet
        List<MoneyRequestDTO> pendingRequests = moneyRequestRepository
                .findAllByRequesterWallet_IdOrPayerWallet_Id( transferaWallet.getId(), transferaWallet.getId() )
                .stream()
                .filter( r -> r.getStatus() == MoneyRequestStatus.PENDING )
                .map( MoneyRequestDTO::new )
                .toList();

        return ResponseEntity.ok( new ActivityFeedDTO( pendingRequests, transactions ) );
    }
}