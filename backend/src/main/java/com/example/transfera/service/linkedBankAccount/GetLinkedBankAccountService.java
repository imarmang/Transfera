package com.example.transfera.service.linkedBankAccount;

import com.example.transfera.Query;
import com.example.transfera.domain.linked_bank_account.LinkedBankAccount;
import com.example.transfera.domain.linked_bank_account.LinkedBankAccountRepository;
import com.example.transfera.dto.LinkedBankAccountDTO.LinkedBankAccountDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GetLinkedBankAccountService implements Query<Void, List<LinkedBankAccountDTO>> {

    private final LinkedBankAccountRepository linkedBankAccountRepository;

    public GetLinkedBankAccountService( LinkedBankAccountRepository linkedBankAccountRepository ) {
        this.linkedBankAccountRepository = linkedBankAccountRepository;
    }


    @Override
    public ResponseEntity<List<LinkedBankAccountDTO>> execute(Void input) {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Optional<List<LinkedBankAccount>> accounts = linkedBankAccountRepository
                .findByUserCredentialsEmail(email);

        return accounts
                .map(list -> ResponseEntity.ok(
                        list.stream()
                                .map(LinkedBankAccountDTO::new)
                                .toList()
                ))
                .orElse(ResponseEntity.notFound().build());
    }
}
