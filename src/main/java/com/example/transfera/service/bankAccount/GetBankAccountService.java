package com.example.transfera.service.bankAccount;

import com.example.transfera.Query;
import com.example.transfera.domain.bank_account.BankAccount;
import com.example.transfera.domain.bank_account.BankAccountRepository;
import com.example.transfera.dto.BankAccountDTO.BankAccountDTO;
import com.example.transfera.exceptions.BankAccountNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


// TODO: change the Query to do Void, BankAccountDTO instead of UUID use UUID for the admin side
@Service
public class GetBankAccountService implements Query<UUID, BankAccountDTO> {

    private final BankAccountRepository bankAccountRepository;

    public GetBankAccountService( BankAccountRepository bankAccountRepository ) {
        this.bankAccountRepository = bankAccountRepository;
    }


    @Override
    public ResponseEntity<BankAccountDTO> execute( UUID input ) {
        Optional<BankAccount> bankAccountOptional = bankAccountRepository.findById( input );

        if ( bankAccountOptional.isPresent() ) {
            return ResponseEntity.ok( new BankAccountDTO( bankAccountOptional.get() ) );
        }

        throw new BankAccountNotFoundException();
    }
}
