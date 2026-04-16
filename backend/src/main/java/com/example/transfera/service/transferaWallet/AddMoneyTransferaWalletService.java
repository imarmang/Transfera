package com.example.transfera.service.transferaWallet;

import com.example.transfera.Command;
import com.example.transfera.domain.linked_bank_account.LinkedBankAccount;
import com.example.transfera.domain.linked_bank_account.LinkedBankAccountRepository;
import com.example.transfera.domain.transaction.TransactionFactory;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.dto.TransferaWalletDTO.AddMoneyRequestDTO;
import com.example.transfera.dto.TransferaWalletDTO.TransferaWalletDTO;
import com.example.transfera.exceptions.customExceptions.LinkedBankAccountNotFoundException;
import com.example.transfera.exceptions.customExceptions.TransferaWalletNotFoundException;
import com.example.transfera.service.transaction.CreateTransactionService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AddMoneyTransferaWalletService implements Command<AddMoneyRequestDTO, TransferaWalletDTO> {


    private final TransferaWalletRepository transferaWalletRepository;
    private final LinkedBankAccountRepository linkedBankAccountRepository;
    private final CreateTransactionService createTransactionService;

    public AddMoneyTransferaWalletService(TransferaWalletRepository transferaWalletRepository,
                                          LinkedBankAccountRepository linkedBankAccountRepository, CreateTransactionService createTransactionService) {
        this.transferaWalletRepository = transferaWalletRepository;
        this.linkedBankAccountRepository = linkedBankAccountRepository;
        this.createTransactionService = createTransactionService;
    }

    @Override
    @Transactional
    public ResponseEntity<TransferaWalletDTO> execute( AddMoneyRequestDTO input ) {
        String email = ( String ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        System.out.println( "AddMoney request — email: " + email );
        System.out.println( "linkedBankAccountId: " + input.getLinkedBankAccountId() );
        System.out.println( "amount: " + input.getAmount() );

        LinkedBankAccount linkedBankAccount = linkedBankAccountRepository
                .findByIdAndUserCredentialsEmail( input.getLinkedBankAccountId(), email )
                .orElseThrow( LinkedBankAccountNotFoundException::new );

        System.out.println( "LinkedBankAccount found: " + linkedBankAccount.getId() );

        TransferaWallet transferaWallet = transferaWalletRepository
                .findByUserCredentialsEmail( email )
                .orElseThrow( TransferaWalletNotFoundException::new );

        System.out.println( "Wallet found, current balance: " + transferaWallet.getBalance() );

        transferaWallet.setBalance( transferaWallet.getBalance().add( input.getAmount() ) );
        TransferaWallet updated = transferaWalletRepository.save( transferaWallet );

        System.out.println( "Wallet updated — new balance: " + updated.getBalance() );

        createTransactionService
            .execute(
                    TransactionFactory.addMoney( updated, input.getAmount(), linkedBankAccount )
        );

        return ResponseEntity.ok( new TransferaWalletDTO( updated ) );
    }
}
