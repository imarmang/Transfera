package com.example.transfera.service.transaction;

import com.example.transfera.domain.transaction.Transaction;
import com.example.transfera.domain.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Internal service class, we use it to create a transaction when we do Add Money and so on
// Doesn't need the @Transactional annot because these methods are called in Add Money and Cash Out which
// already have the Transactional annotation on them
@Service
@RequiredArgsConstructor
public class CreateTransactionService {

    private final TransactionRepository transactionRepository;

    public void execute( Transaction transaction ) {
        transactionRepository.save( transaction );
    }
}
