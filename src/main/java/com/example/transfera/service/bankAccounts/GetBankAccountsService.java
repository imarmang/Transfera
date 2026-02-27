package com.example.transfera.service.bankAccounts;

import com.example.transfera.Query;
import com.example.transfera.domain.bank_account.BankAccount;
import com.example.transfera.domain.bank_account.BankAccountRepository;
import com.example.transfera.dto.BankAccountDTO.BankAccountDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetBankAccountsService implements Query<Void, List<BankAccountDTO>> {
    private final BankAccountRepository bankAccountRepository;

    public GetBankAccountsService( BankAccountRepository bankAccountRepository ) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public ResponseEntity<List<BankAccountDTO>> execute( Void input ) {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map( BankAccountDTO:: new ).toList();

        return ResponseEntity.status( HttpStatus.OK ).body( bankAccountDTOS );
    }
}
