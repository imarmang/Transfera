package com.example.transfera.service.accounts;

import com.example.transfera.Query;
import com.example.transfera.domain.account.BankAccount;
import com.example.transfera.domain.account.BankAccountRepository;
import com.example.transfera.dto.BankAccountDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetAccountService implements Query<UUID, BankAccountDTO> {

    private final BankAccountRepository bankAccountRepository;

    public GetAccountService( BankAccountRepository bankAccountRepository ) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public ResponseEntity<BankAccountDTO> execute(UUID input) {
        return null;
    }

//    @Override
//    public ResponseEntity<BankAccountDTO> execute( UUID input ) {
//        Optional<BankAccount> bankAccountOptional = bankAccountRepository.findById( input );
//
//        if ( bankAccountOptional.isPresent() ) {
//            return ResponseEntity.ok( new BankAccountDTO( bankAccountOptional.get() ) );
//        }
//
//        throw new AccountNotFoundException();
//    }
}
