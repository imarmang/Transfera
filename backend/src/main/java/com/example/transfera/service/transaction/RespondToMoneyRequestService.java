package com.example.transfera.service.transaction;

import com.example.transfera.Command;
import com.example.transfera.domain.money_request.MoneyRequest;
import com.example.transfera.domain.money_request.MoneyRequestRepository;
import com.example.transfera.domain.money_request.MoneyRequestStatus;
import com.example.transfera.domain.profile.Profile;
import com.example.transfera.domain.profile.ProfileRepository;
import com.example.transfera.domain.transaction.TransactionFactory;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.dto.MoneyRequestDTO.RespondToMoneyRequestDTO;
import com.example.transfera.exceptions.customExceptions.InsufficientBalanceTransferaWalletException;
import com.example.transfera.exceptions.customExceptions.TransferaWalletNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RespondToMoneyRequestService implements Command<RespondToMoneyRequestDTO, Void> {

    private final MoneyRequestRepository moneyRequestRepository;
    private final TransferaWalletRepository transferaWalletRepository;
    private final CreateTransactionService createTransactionService;
    private final ProfileRepository profileRepository;

    public RespondToMoneyRequestService( MoneyRequestRepository moneyRequestRepository,
                                         TransferaWalletRepository transferaWalletRepository,
                                         CreateTransactionService createTransactionService,
                                         ProfileRepository profileRepository ) {
        this.moneyRequestRepository = moneyRequestRepository;
        this.transferaWalletRepository = transferaWalletRepository;
        this.createTransactionService = createTransactionService;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<Void> execute( RespondToMoneyRequestDTO input ) {

        String payerEmail = ( String ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        MoneyRequest moneyRequest = moneyRequestRepository
                .findById( input.getMoneyRequestId() )
                .orElseThrow( () -> new IllegalArgumentException( "Money request not found." ) );

        if ( moneyRequest.getStatus() != MoneyRequestStatus.PENDING ) {
            throw new IllegalArgumentException( "This request has already been responded to." );
        }

        TransferaWallet payerWallet = transferaWalletRepository
                .findByUserCredentialsEmail( payerEmail )
                .orElseThrow( TransferaWalletNotFoundException::new );

        if ( !moneyRequest.getPayerWallet().getId().equals( payerWallet.getId() ) ) {
            throw new IllegalArgumentException( "You are not authorized to respond to this request." );
        }

        TransferaWallet requesterWallet = moneyRequest.getRequesterWallet();
        UUID moneyRequestId = moneyRequest.getMoneyRequestId();
        String payerUsername = profileRepository
                .findByUserCredentialsEmail( payerEmail )
                .map( Profile::getUserName )
                .orElse( payerEmail );
        String requesterUsername = moneyRequest.getPeerName();

        if ( input.getResponse() == MoneyRequestStatus.APPROVED ) {

            if ( payerWallet.getBalance().compareTo( moneyRequest.getAmount() ) < 0 ) {
                throw new InsufficientBalanceTransferaWalletException();
            }

            payerWallet.setBalance( payerWallet.getBalance().subtract( moneyRequest.getAmount() ) );
            requesterWallet.setBalance( requesterWallet.getBalance().add( moneyRequest.getAmount() ) );

            transferaWalletRepository.save( payerWallet );
            transferaWalletRepository.save( requesterWallet );

            createTransactionService.execute(
                    TransactionFactory.requestApprovedSend( payerWallet, moneyRequest.getAmount(), requesterUsername, moneyRequestId )
            );
            createTransactionService.execute(
                    TransactionFactory.requestApprovedReceived( requesterWallet, moneyRequest.getAmount(), payerUsername, moneyRequestId )
            );

        } else if ( input.getResponse() == MoneyRequestStatus.DECLINED ) {

            createTransactionService.execute(
                    TransactionFactory.requestDeclined( payerWallet, moneyRequest.getAmount(), requesterUsername, moneyRequestId )
            );
        }

        moneyRequest.setStatus( input.getResponse() );
        moneyRequestRepository.save( moneyRequest );

        return ResponseEntity.ok().build();
    }
}