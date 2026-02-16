package com.example.transfera.transaction;

import com.example.transfera.Command;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TransferService implements Command<TransferDTO, String> {

    private final BankAccountRepository bankAccountRepository;

    public TransferService( BankAccountRepository bankAccountRepository ) {
        this.bankAccountRepository = bankAccountRepository;
    }


    @Override
    public ResponseEntity<String> execute( TransferDTO transfer ) {

        Optional<BankAccount> fromAccount = bankAccountRepository.findById( transfer.getFromUser() );
        Optional<BankAccount> toAccount = bankAccountRepository.findById( transfer.getToUser() );

        if ( fromAccount.isEmpty() || toAccount.isEmpty() ) {
            throw new RuntimeException( "User is not empty" );
        }
        BankAccount from = fromAccount.get();
        BankAccount to = toAccount.get();

        // add & subtract
        add( to, transfer.getAmount() );
        // at this point -> have added new money but not checked if enough to transfer
        System.out.println( "After adding, before deducting" );
        System.out.println( bankAccountRepository.findById( to.getName() ) );  // this would be better as a logging statement

        deduct( from, transfer.getAmount() );

        return ResponseEntity.ok().build();

    }

    private void deduct( BankAccount bankAccount, double amount ) {
        if ( bankAccount.getBalance() < amount ) {
            throw new RuntimeException( "Not Enough Money" );
        }

        bankAccount.setBalance( bankAccount.getBalance() - amount );
    }

    private void add( BankAccount bankAccount, double amount ) {
        bankAccount.setBalance( bankAccount.getBalance() + amount );
    }
}
