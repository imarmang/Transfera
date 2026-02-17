package com.example.transfera.service.accounts;

import com.example.transfera.Command;
import com.example.transfera.domain.account.BankAccount;
import com.example.transfera.domain.account.BankAccountRepository;
import com.example.transfera.exceptions.BankAccountNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeleteBankAccountService implements Command<UUID, Void> {
    private final BankAccountRepository bankAccountRepository;

    public DeleteBankAccountService( BankAccountRepository bankAccountRepository ) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public ResponseEntity<Void> execute( UUID id ) {

        Optional<BankAccount> bankAccount = bankAccountRepository.findById( id );

        if ( bankAccount.isPresent() ) {
            bankAccountRepository.deleteById( id );

            return ResponseEntity.status( HttpStatus.NO_CONTENT ).build();
        }

        throw new BankAccountNotFoundException();
    }
}
