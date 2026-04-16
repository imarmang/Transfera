package com.example.transfera.service.transaction;

import com.example.transfera.domain.transaction.Transaction;
import com.example.transfera.domain.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Internal service class, we use it to create a transaction when we do Add Money and so on
@Service
@RequiredArgsConstructor
public class CreateTransactionService {

    private final TransactionRepository transactionRepository;

    public void execute( Transaction transaction ) {
        transactionRepository.save( transaction );
    }
}
