package com.example.transfera.service.transferaWallet;

import com.example.transfera.Command;
import com.example.transfera.domain.profile.Profile;
import com.example.transfera.domain.profile.ProfileRepository;
import com.example.transfera.domain.transaction.TransactionFactory;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.dto.TransferaWalletDTO.SendMoneyRequestDTO;
import com.example.transfera.dto.TransferaWalletDTO.TransferaWalletDTO;
import com.example.transfera.exceptions.customExceptions.InsufficientBalanceTransferaWalletException;
import com.example.transfera.exceptions.customExceptions.TransferaWalletNotFoundException;
import com.example.transfera.exceptions.customExceptions.UserNotFound;
import com.example.transfera.service.transaction.CreateTransactionService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SendMoneyTransferaWalletService implements Command<SendMoneyRequestDTO, TransferaWalletDTO> {

    private final TransferaWalletRepository transferaWalletRepository;
    private final ProfileRepository profileRepository;
    private final CreateTransactionService createTransactionService;
    public SendMoneyTransferaWalletService( TransferaWalletRepository transferaWalletRepository,
                                            ProfileRepository profileRepository,
                                            CreateTransactionService createTransactionService ) {
        this.transferaWalletRepository = transferaWalletRepository;
        this.profileRepository = profileRepository;
        this.createTransactionService = createTransactionService;
    }

    @Override
    @Transactional
    public ResponseEntity<TransferaWalletDTO> execute( SendMoneyRequestDTO input ) {

        String senderEmail = ( String ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println( "SendMoney — senderEmail: " + senderEmail );
        System.out.println( "SendMoney — recipientUsername: " + input.getRecipientUsername() );
        System.out.println( "SendMoney — amount: " + input.getAmount() );

        TransferaWallet senderWallet = transferaWalletRepository
                .findByUserCredentialsEmail( senderEmail )
                .orElseThrow( TransferaWalletNotFoundException::new );
        System.out.println( "SendMoney — senderWallet found: " + senderWallet.getId() );

        Profile recipientProfile = profileRepository
                .findByUserName( input.getRecipientUsername() )
                .orElseThrow( UserNotFound::new );
        System.out.println( "SendMoney — recipientProfile found: " + recipientProfile.getUserName() );

        String recipientEmail = recipientProfile.getUserCredentials().getEmail();
        System.out.println( "SendMoney — recipientEmail: " + recipientEmail );

        TransferaWallet recipientWallet = transferaWalletRepository
                .findByUserCredentialsEmail( recipientEmail )
                .orElseThrow( TransferaWalletNotFoundException::new );
        System.out.println( "SendMoney — recipientWallet found: " + recipientWallet.getId() );

        if ( senderWallet.getId().equals( recipientWallet.getId() ) ) {
            System.out.println( "SendMoney — ERROR: sender and recipient are the same" );
            throw new IllegalArgumentException( "You cannot send money to yourself." );
        }

        if ( input.getAmount().compareTo( BigDecimal.ZERO ) <= 0 ) {
            System.out.println( "SendMoney — ERROR: amount is zero or negative" );
            throw new IllegalArgumentException( "Amount must be greater than zero." );
        }

        if ( senderWallet.getBalance().compareTo( input.getAmount() ) < 0 ) {
            System.out.println( "SendMoney — ERROR: insufficient balance. Balance: " + senderWallet.getBalance() + " Amount: " + input.getAmount() );
            throw new InsufficientBalanceTransferaWalletException();
        }

        senderWallet.setBalance( senderWallet.getBalance().subtract( input.getAmount() ) );
        recipientWallet.setBalance( recipientWallet.getBalance().add( input.getAmount() ) );

        transferaWalletRepository.save( senderWallet );
        System.out.println( "SendMoney — senderWallet saved, new balance: " + senderWallet.getBalance() );

        transferaWalletRepository.save( recipientWallet );
        System.out.println( "SendMoney — recipientWallet saved, new balance: " + recipientWallet.getBalance() );

        String recipientUserName = recipientProfile.getUserName();
        String senderUserName = profileRepository
                .findByUserCredentialsEmail( senderEmail )
                .map( Profile::getUserName )
                .orElse( senderEmail );

        System.out.println( "SendMoney — creating SEND transaction for: " + senderUserName + " → " + recipientUserName );
        createTransactionService.execute(
                TransactionFactory.send( senderWallet, input.getAmount(), recipientUserName )
        );

        System.out.println( "SendMoney — creating RECEIVED transaction for: " + recipientUserName );
        createTransactionService.execute(
                TransactionFactory.received( recipientWallet, input.getAmount(), senderUserName )
        );

        System.out.println( "SendMoney — completed successfully" );
        return ResponseEntity.ok( new TransferaWalletDTO( senderWallet ) );
    }
}
