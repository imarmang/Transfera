package com.example.transfera.service.linkedBankAccount;

import com.example.transfera.Command;
import com.example.transfera.domain.linked_bank_account.LinkedBankAccount;
import com.example.transfera.domain.linked_bank_account.LinkedBankAccountRepository;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.dto.LinkedBankAccountDTO.CreateLinkedBankAccountRequestDTO;
import com.example.transfera.dto.LinkedBankAccountDTO.LinkedBankAccountDTO;
import com.example.transfera.exceptions.customExceptions.LinkedBankAccountExists;
import com.example.transfera.exceptions.customExceptions.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CreateLinkedBankAccountService implements Command<CreateLinkedBankAccountRequestDTO, LinkedBankAccountDTO> {

    private final LinkedBankAccountRepository linkedBankAccountRepository;
    private final UserCredentialsRepository userCredentialsRepository;


    public CreateLinkedBankAccountService(LinkedBankAccountRepository linkedBankAccountRepository, UserCredentialsRepository userCredentialsRepository ) {
        this.linkedBankAccountRepository = linkedBankAccountRepository;
        this.userCredentialsRepository = userCredentialsRepository;
    }

    @Override
    public ResponseEntity<LinkedBankAccountDTO> execute( CreateLinkedBankAccountRequestDTO request ) {
        String email = ( String ) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        System.out.println( ">>> [CreateLinkedBankAccountService] Executing for email: " + email );

        UserCredentials userCredentials = userCredentialsRepository.findByEmail( email ).orElseThrow( () -> {
            System.out.println( ">>> [CreateProfileService] User not found for email: " + email );
            return new UserNotFound();
            });

        // Check if the user has already connected this bank account to their profile
        if ( linkedBankAccountRepository.existsByAccountNumberAndUserCredentialsEmail(
                request.accountNumber(), email)) {
            throw new LinkedBankAccountExists();
        }

        LinkedBankAccount newAccount = new LinkedBankAccount(
                userCredentials,
                request.bankName(),
                request.accountNumber(),
                request.accountHolderName(),
                request.routingNumber(),
                request.accountType()
        );

        LinkedBankAccount saved =  linkedBankAccountRepository.save( newAccount );

        System.out.println( ">>> [CreateLinkedBankAccountService] Linked account created for: " + email );

        return ResponseEntity.status( HttpStatus.CREATED ).body( new LinkedBankAccountDTO( saved ) );

    }
}
