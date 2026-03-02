package com.example.transfera.service.bankAccount;

import com.example.transfera.Command;
import com.example.transfera.domain.bank_account.BankAccount;
import com.example.transfera.domain.bank_account.BankAccountRepository;
import com.example.transfera.dto.BankAccountDTO.BankAccountDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


// creates the bank account
@Service
public class CreateBankAccountService implements Command<BankAccount, BankAccountDTO> {
    private final BankAccountRepository bankAccountRepository;

    public CreateBankAccountService( BankAccountRepository bankAccountRepository ) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public ResponseEntity<BankAccountDTO> execute( BankAccount bankAccount ) {
        BankAccount savedAccount = bankAccountRepository.save( bankAccount );

        return ResponseEntity.status( HttpStatus.CREATED ).body( new BankAccountDTO( savedAccount ) );
    }
}
