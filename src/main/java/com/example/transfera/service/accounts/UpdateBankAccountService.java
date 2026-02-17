// might be an overkill

package com.example.transfera.service.accounts;

import com.example.transfera.Command;
import com.example.transfera.domain.account.BankAccount;
import com.example.transfera.domain.account.BankAccountRepository;
import com.example.transfera.dto.BankAccountDTO;
import com.example.transfera.dto.UpdateBankAccountCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateBankAccountService implements Command< UpdateBankAccountCommand, BankAccountDTO> {
    private final BankAccountRepository bankAccountRepository;

    public UpdateBankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public ResponseEntity<BankAccountDTO> execute( UpdateBankAccountCommand command ) {
        Optional<BankAccount> bankAccountOptional = bankAccountRepository.findById( command.getId() );
        if ( bankAccountOptional.isPresent() ) {
            BankAccount bankAccount = command.getBankAccount();
            bankAccount.setId( command.getId() );
            bankAccountRepository.save( bankAccount );
            return ResponseEntity.ok( new BankAccountDTO( bankAccount ) );

        }
        return null;
    }
}
