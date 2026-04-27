package com.example.transfera.service.transaction;

import com.example.transfera.Command;
import com.example.transfera.domain.money_request.MoneyRequest;
import com.example.transfera.domain.money_request.MoneyRequestRepository;
import com.example.transfera.domain.profile.Profile;
import com.example.transfera.domain.profile.ProfileRepository;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.dto.MoneyRequestDTO.CreateMoneyRequestDTO;
import com.example.transfera.dto.MoneyRequestDTO.MoneyRequestDTO;
import com.example.transfera.exceptions.customExceptions.RequestMoneyFromYourself;
import com.example.transfera.exceptions.customExceptions.TransferaWalletNotFoundException;
import com.example.transfera.exceptions.customExceptions.UserNotFound;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CreateMoneyRequestService implements Command<CreateMoneyRequestDTO, MoneyRequestDTO> {

    private final MoneyRequestRepository moneyRequestRepository;
    private final TransferaWalletRepository transferaWalletRepository;
    private final ProfileRepository profileRepository;

    public CreateMoneyRequestService( MoneyRequestRepository moneyRequestRepository,
                                      TransferaWalletRepository transferaWalletRepository,
                                      ProfileRepository profileRepository ) {
        this.moneyRequestRepository = moneyRequestRepository;
        this.transferaWalletRepository = transferaWalletRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<MoneyRequestDTO> execute( CreateMoneyRequestDTO input ) {

        String requesterEmail = ( String ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TransferaWallet requesterWallet = transferaWalletRepository
                .findByUserCredentialsEmail( requesterEmail )
                .orElseThrow( TransferaWalletNotFoundException::new );

        Profile payerProfile = profileRepository
                .findByUserName( input.getRecipientUsername() )
                .orElseThrow( UserNotFound::new );

        String payerEmail = payerProfile.getUserCredentials().getEmail();

        TransferaWallet payerWallet = transferaWalletRepository
                .findByUserCredentialsEmail( payerEmail )
                .orElseThrow( TransferaWalletNotFoundException::new );

        if ( requesterWallet.getId().equals( payerWallet.getId() ) ) {
            throw new RequestMoneyFromYourself();
        }

        String requesterUsername = profileRepository
                .findByUserCredentialsEmail(requesterEmail)
                .map(Profile::getUserName)
                .orElse(requesterEmail);

        MoneyRequest moneyRequest = MoneyRequest.builder()
                .amount(input.getAmount())
                .note(input.getNote())
                .requester(requesterUsername)
                .requestee(payerProfile.getUserName())
                .requesterWallet(requesterWallet)
                .payerWallet(payerWallet)
                .build();

        moneyRequestRepository.save(moneyRequest);

        return ResponseEntity.ok( new MoneyRequestDTO( moneyRequest ) );
    }
}