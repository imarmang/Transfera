package com.example.transfera.service.transferaWallet;

import com.example.transfera.domain.profile.Profile;
import com.example.transfera.domain.profile.ProfileRepository;
import com.example.transfera.domain.transaction.Transaction;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.dto.TransferaWalletDTO.SendMoneyRequestDTO;
import com.example.transfera.dto.TransferaWalletDTO.TransferaWalletDTO;
import com.example.transfera.exceptions.customExceptions.InsufficientBalanceTransferaWalletException;
import com.example.transfera.exceptions.customExceptions.TransferaWalletNotFoundException;
import com.example.transfera.exceptions.customExceptions.UserNotFound;
import com.example.transfera.service.transaction.CreateTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith( MockitoExtension.class )
class SendMoneyTransferaWalletServiceTest {

    @Mock
    private TransferaWalletRepository transferaWalletRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private CreateTransactionService createTransactionService;

    @InjectMocks
    private SendMoneyTransferaWalletService sendMoneyTransferaWalletService;

    private static final String SENDER_EMAIL = "sender@example.com";
    private static final String RECIPIENT_EMAIL = "recipient@example.com";
    private static final String SENDER_USERNAME = "senderuser";
    private static final String RECIPIENT_USERNAME = "recipientuser";
    private static final UUID SENDER_WALLET_ID = UUID.randomUUID();
    private static final UUID RECIPIENT_WALLET_ID = UUID.randomUUID();

    // ─── Setup ────────────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken( SENDER_EMAIL, null, null );
        SecurityContextHolder.getContext().setAuthentication( auth );
    }

    // ─── Helper builders ──────────────────────────────────────────────────────

    private UserCredentials buildUser( String email ) {
        return new UserCredentials( email, "password" );
    }

    private TransferaWallet buildWallet( UUID id, UserCredentials user, BigDecimal balance ) {
        TransferaWallet wallet = new TransferaWallet();
        wallet.setId( id );
        wallet.setWalletNumber( "1234567890" );
        wallet.setBalance( balance );
        wallet.setUserCredentials( user );
        return wallet;
    }

    private Profile buildProfile( String userName, String firstName, String lastName, UserCredentials user ) {
        return new Profile( userName, firstName, lastName, "5551234567", user );
    }

    private SendMoneyRequestDTO buildRequest( String recipientUsername, BigDecimal amount ) {
        SendMoneyRequestDTO dto = new SendMoneyRequestDTO();
        try {
            var usernameField = SendMoneyRequestDTO.class.getDeclaredField( "recipientUsername" );
            usernameField.setAccessible( true );
            usernameField.set( dto, recipientUsername );

            var amountField = SendMoneyRequestDTO.class.getDeclaredField( "amount" );
            amountField.setAccessible( true );
            amountField.set( dto, amount );

            var noteField = SendMoneyRequestDTO.class.getDeclaredField( "note" );
            noteField.setAccessible( true );
            noteField.set( dto, "" );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        return dto;
    }

    // ─── Unit Tests ───────────────────────────────────────────────────────────

    @Test
    void execute_happyPath_transfersMoneyCorrectly() {
        // Verifies that $50 is deducted from sender and added to recipient correctly.
        UserCredentials senderUser = buildUser( SENDER_EMAIL );
        UserCredentials recipientUser = buildUser( RECIPIENT_EMAIL );

        TransferaWallet senderWallet = buildWallet( SENDER_WALLET_ID, senderUser, new BigDecimal( "100.00" ) );
        TransferaWallet recipientWallet = buildWallet( RECIPIENT_WALLET_ID, recipientUser, new BigDecimal( "50.00" ) );

        Profile recipientProfile = buildProfile( RECIPIENT_USERNAME, "John", "Doe", recipientUser );
        Profile senderProfile = buildProfile( SENDER_USERNAME, "Jane", "Smith", senderUser );

        SendMoneyRequestDTO request = buildRequest( RECIPIENT_USERNAME, new BigDecimal( "50.00" ) );

        when( transferaWalletRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderWallet ) );
        when( profileRepository.findByUserName( RECIPIENT_USERNAME ) )
                .thenReturn( Optional.of( recipientProfile ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( RECIPIENT_EMAIL ) )
                .thenReturn( Optional.of( recipientWallet ) );
        when( profileRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderProfile ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        ResponseEntity<TransferaWalletDTO> response = sendMoneyTransferaWalletService.execute( request );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).isNotNull();
        assertThat( senderWallet.getBalance() ).isEqualByComparingTo( new BigDecimal( "50.00" ) );
        assertThat( recipientWallet.getBalance() ).isEqualByComparingTo( new BigDecimal( "100.00" ) );
    }

    @Test
    void execute_happyPath_savesBothWallets() {
        // Verifies that both sender and recipient wallets are saved exactly once.
        UserCredentials senderUser = buildUser( SENDER_EMAIL );
        UserCredentials recipientUser = buildUser( RECIPIENT_EMAIL );

        TransferaWallet senderWallet = buildWallet( SENDER_WALLET_ID, senderUser, new BigDecimal( "100.00" ) );
        TransferaWallet recipientWallet = buildWallet( RECIPIENT_WALLET_ID, recipientUser, new BigDecimal( "50.00" ) );

        Profile recipientProfile = buildProfile( RECIPIENT_USERNAME, "John", "Doe", recipientUser );
        Profile senderProfile = buildProfile( SENDER_USERNAME, "Jane", "Smith", senderUser );

        SendMoneyRequestDTO request = buildRequest( RECIPIENT_USERNAME, new BigDecimal( "50.00" ) );

        when( transferaWalletRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderWallet ) );
        when( profileRepository.findByUserName( RECIPIENT_USERNAME ) )
                .thenReturn( Optional.of( recipientProfile ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( RECIPIENT_EMAIL ) )
                .thenReturn( Optional.of( recipientWallet ) );
        when( profileRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderProfile ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        sendMoneyTransferaWalletService.execute( request );

        verify( transferaWalletRepository, times( 2 ) ).save( any( TransferaWallet.class ) );
    }

    @Test
    void execute_happyPath_createsTwoTransactions() {
        // Verifies that a SEND and a RECEIVED transaction are both created.
        UserCredentials senderUser = buildUser( SENDER_EMAIL );
        UserCredentials recipientUser = buildUser( RECIPIENT_EMAIL );

        TransferaWallet senderWallet = buildWallet( SENDER_WALLET_ID, senderUser, new BigDecimal( "100.00" ) );
        TransferaWallet recipientWallet = buildWallet( RECIPIENT_WALLET_ID, recipientUser, new BigDecimal( "50.00" ) );

        Profile recipientProfile = buildProfile( RECIPIENT_USERNAME, "John", "Doe", recipientUser );
        Profile senderProfile = buildProfile( SENDER_USERNAME, "Jane", "Smith", senderUser );

        SendMoneyRequestDTO request = buildRequest( RECIPIENT_USERNAME, new BigDecimal( "50.00" ) );

        when( transferaWalletRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderWallet ) );
        when( profileRepository.findByUserName( RECIPIENT_USERNAME ) )
                .thenReturn( Optional.of( recipientProfile ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( RECIPIENT_EMAIL ) )
                .thenReturn( Optional.of( recipientWallet ) );
        when( profileRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderProfile ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        sendMoneyTransferaWalletService.execute( request );

        verify( createTransactionService, times( 2 ) ).execute( any( Transaction.class ) );
    }

    @Test
    void execute_senderWalletNotFound_throwsTransferaWalletNotFoundException() {
        // Verifies that missing sender wallet throws correct exception.
        SendMoneyRequestDTO request = buildRequest( RECIPIENT_USERNAME, new BigDecimal( "50.00" ) );

        when( transferaWalletRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.empty() );

        assertThatThrownBy( () -> sendMoneyTransferaWalletService.execute( request ) )
                .isInstanceOf( TransferaWalletNotFoundException.class );

        verify( transferaWalletRepository, never() ).save( any() );
        verify( createTransactionService, never() ).execute( any() );
    }

    @Test
    void execute_recipientNotFound_throwsUserNotFound() {
        // Verifies that unknown recipient username throws UserNotFound.
        UserCredentials senderUser = buildUser( SENDER_EMAIL );
        TransferaWallet senderWallet = buildWallet( SENDER_WALLET_ID, senderUser, new BigDecimal( "100.00" ) );
        SendMoneyRequestDTO request = buildRequest( "unknownuser", new BigDecimal( "50.00" ) );

        when( transferaWalletRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderWallet ) );
        when( profileRepository.findByUserName( "unknownuser" ) )
                .thenReturn( Optional.empty() );

        assertThatThrownBy( () -> sendMoneyTransferaWalletService.execute( request ) )
                .isInstanceOf( UserNotFound.class );

        verify( transferaWalletRepository, never() ).save( any() );
        verify( createTransactionService, never() ).execute( any() );
    }

    @Test
    void execute_insufficientBalance_throwsInsufficientBalanceException() {
        // Verifies that sending more than available balance throws InsufficientBalanceException.
        UserCredentials senderUser = buildUser( SENDER_EMAIL );
        UserCredentials recipientUser = buildUser( RECIPIENT_EMAIL );

        TransferaWallet senderWallet = buildWallet( SENDER_WALLET_ID, senderUser, new BigDecimal( "30.00" ) );
        TransferaWallet recipientWallet = buildWallet( RECIPIENT_WALLET_ID, recipientUser, new BigDecimal( "50.00" ) );

        Profile recipientProfile = buildProfile( RECIPIENT_USERNAME, "John", "Doe", recipientUser );
        SendMoneyRequestDTO request = buildRequest( RECIPIENT_USERNAME, new BigDecimal( "50.00" ) );

        when( transferaWalletRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderWallet ) );
        when( profileRepository.findByUserName( RECIPIENT_USERNAME ) )
                .thenReturn( Optional.of( recipientProfile ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( RECIPIENT_EMAIL ) )
                .thenReturn( Optional.of( recipientWallet ) );

        assertThatThrownBy( () -> sendMoneyTransferaWalletService.execute( request ) )
                .isInstanceOf( InsufficientBalanceTransferaWalletException.class );

        verify( transferaWalletRepository, never() ).save( any() );
        verify( createTransactionService, never() ).execute( any() );
    }

    @Test
    void execute_sendToYourself_throwsIllegalArgumentException() {
        // Verifies that sending money to yourself throws IllegalArgumentException.
        UserCredentials senderUser = buildUser( SENDER_EMAIL );
        TransferaWallet senderWallet = buildWallet( SENDER_WALLET_ID, senderUser, new BigDecimal( "100.00" ) );

        Profile senderProfile = buildProfile( SENDER_USERNAME, "Jane", "Smith", senderUser );
        SendMoneyRequestDTO request = buildRequest( SENDER_USERNAME, new BigDecimal( "50.00" ) );

        when( transferaWalletRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderWallet ) );
        when( profileRepository.findByUserName( SENDER_USERNAME ) )
                .thenReturn( Optional.of( senderProfile ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderWallet ) );

        assertThatThrownBy( () -> sendMoneyTransferaWalletService.execute( request ) )
                .isInstanceOf( IllegalArgumentException.class )
                .hasMessage( "You cannot send money to yourself." );

        verify( transferaWalletRepository, never() ).save( any() );
        verify( createTransactionService, never() ).execute( any() );
    }

    @Test
    void execute_zeroAmount_throwsIllegalArgumentException() {
        // Verifies that sending zero amount throws IllegalArgumentException.
        UserCredentials senderUser = buildUser( SENDER_EMAIL );
        UserCredentials recipientUser = buildUser( RECIPIENT_EMAIL );

        TransferaWallet senderWallet = buildWallet( SENDER_WALLET_ID, senderUser, new BigDecimal( "100.00" ) );
        TransferaWallet recipientWallet = buildWallet( RECIPIENT_WALLET_ID, recipientUser, new BigDecimal( "50.00" ) );

        Profile recipientProfile = buildProfile( RECIPIENT_USERNAME, "John", "Doe", recipientUser );
        SendMoneyRequestDTO request = buildRequest( RECIPIENT_USERNAME, BigDecimal.ZERO );

        when( transferaWalletRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderWallet ) );
        when( profileRepository.findByUserName( RECIPIENT_USERNAME ) )
                .thenReturn( Optional.of( recipientProfile ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( RECIPIENT_EMAIL ) )
                .thenReturn( Optional.of( recipientWallet ) );

        assertThatThrownBy( () -> sendMoneyTransferaWalletService.execute( request ) )
                .isInstanceOf( IllegalArgumentException.class )
                .hasMessage( "Amount must be greater than zero." );

        verify( transferaWalletRepository, never() ).save( any() );
        verify( createTransactionService, never() ).execute( any() );
    }

    @Test
    void execute_exactBalance_transfersSuccessfully() {
        // Verifies that sending the exact full balance results in zero sender balance.
        UserCredentials senderUser = buildUser( SENDER_EMAIL );
        UserCredentials recipientUser = buildUser( RECIPIENT_EMAIL );

        TransferaWallet senderWallet = buildWallet( SENDER_WALLET_ID, senderUser, new BigDecimal( "100.00" ) );
        TransferaWallet recipientWallet = buildWallet( RECIPIENT_WALLET_ID, recipientUser, new BigDecimal( "0.00" ) );

        Profile recipientProfile = buildProfile( RECIPIENT_USERNAME, "John", "Doe", recipientUser );
        Profile senderProfile = buildProfile( SENDER_USERNAME, "Jane", "Smith", senderUser );

        SendMoneyRequestDTO request = buildRequest( RECIPIENT_USERNAME, new BigDecimal( "100.00" ) );

        when( transferaWalletRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderWallet ) );
        when( profileRepository.findByUserName( RECIPIENT_USERNAME ) )
                .thenReturn( Optional.of( recipientProfile ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( RECIPIENT_EMAIL ) )
                .thenReturn( Optional.of( recipientWallet ) );
        when( profileRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderProfile ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        ResponseEntity<TransferaWalletDTO> response = sendMoneyTransferaWalletService.execute( request );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( senderWallet.getBalance() ).isEqualByComparingTo( BigDecimal.ZERO );
        assertThat( recipientWallet.getBalance() ).isEqualByComparingTo( new BigDecimal( "100.00" ) );
    }

    @Test
    void execute_decimalAmount_transfersCorrectly() {
        // Verifies cent-level precision is maintained during transfer.
        UserCredentials senderUser = buildUser( SENDER_EMAIL );
        UserCredentials recipientUser = buildUser( RECIPIENT_EMAIL );

        TransferaWallet senderWallet = buildWallet( SENDER_WALLET_ID, senderUser, new BigDecimal( "10.00" ) );
        TransferaWallet recipientWallet = buildWallet( RECIPIENT_WALLET_ID, recipientUser, new BigDecimal( "0.00" ) );

        Profile recipientProfile = buildProfile( RECIPIENT_USERNAME, "John", "Doe", recipientUser );
        Profile senderProfile = buildProfile( SENDER_USERNAME, "Jane", "Smith", senderUser );

        SendMoneyRequestDTO request = buildRequest( RECIPIENT_USERNAME, new BigDecimal( "0.99" ) );

        when( transferaWalletRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderWallet ) );
        when( profileRepository.findByUserName( RECIPIENT_USERNAME ) )
                .thenReturn( Optional.of( recipientProfile ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( RECIPIENT_EMAIL ) )
                .thenReturn( Optional.of( recipientWallet ) );
        when( profileRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderProfile ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        sendMoneyTransferaWalletService.execute( request );

        assertThat( senderWallet.getBalance() ).isEqualByComparingTo( new BigDecimal( "9.01" ) );
        assertThat( recipientWallet.getBalance() ).isEqualByComparingTo( new BigDecimal( "0.99" ) );
    }

    // ─── Concurrency Tests ────────────────────────────────────────────────────

    @Test
    void execute_concurrentSendsDifferentUsers_allSucceed() throws InterruptedException {
        // Verifies that 5 concurrent sends from different users all succeed correctly.
        int userCount = 5;
        BigDecimal sendAmount = new BigDecimal( "10.00" );

        List<String> senderEmails = new ArrayList<>();
        List<String> recipientUsernames = new ArrayList<>();

        for ( int i = 0; i < userCount; i++ ) {
            String senderEmail = "sender" + i + "@example.com";
            String recipientEmail = "recipient" + i + "@example.com";
            String recipientUsername = "recipient" + i;

            senderEmails.add( senderEmail );
            recipientUsernames.add( recipientUsername );

            UserCredentials senderUser = buildUser( senderEmail );
            UserCredentials recipientUser = buildUser( recipientEmail );

            TransferaWallet senderWallet = buildWallet( UUID.randomUUID(), senderUser, new BigDecimal( "100.00" ) );
            TransferaWallet recipientWallet = buildWallet( UUID.randomUUID(), recipientUser, new BigDecimal( "0.00" ) );

            Profile recipientProfile = buildProfile( recipientUsername, "John", "Doe", recipientUser );
            Profile senderProfile = buildProfile( "sender" + i, "Jane", "Smith", senderUser );

            when( transferaWalletRepository.findByUserCredentialsEmail( senderEmail ) )
                    .thenReturn( Optional.of( senderWallet ) );
            when( profileRepository.findByUserName( recipientUsername ) )
                    .thenReturn( Optional.of( recipientProfile ) );
            when( transferaWalletRepository.findByUserCredentialsEmail( recipientEmail ) )
                    .thenReturn( Optional.of( recipientWallet ) );
            when( profileRepository.findByUserCredentialsEmail( senderEmail ) )
                    .thenReturn( Optional.of( senderProfile ) );
        }

        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( inv -> inv.getArgument( 0 ) );

        ExecutorService executor = Executors.newFixedThreadPool( userCount );
        CountDownLatch latch = new CountDownLatch( userCount );
        AtomicInteger successCount = new AtomicInteger( 0 );
        AtomicInteger failureCount = new AtomicInteger( 0 );

        for ( int i = 0; i < userCount; i++ ) {
            final String senderEmail = senderEmails.get( i );
            final String recipientUsername = recipientUsernames.get( i );

            executor.submit( () -> {
                try {
                    SecurityContext context = new SecurityContextImpl();
                    context.setAuthentication(
                            new UsernamePasswordAuthenticationToken( senderEmail, null, null )
                    );
                    SecurityContextHolder.setContext( context );

                    SendMoneyRequestDTO request = buildRequest( recipientUsername, sendAmount );
                    ResponseEntity<TransferaWalletDTO> response =
                            sendMoneyTransferaWalletService.execute( request );

                    assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
                    successCount.incrementAndGet();
                } catch ( Exception e ) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                    SecurityContextHolder.clearContext();
                }
            } );
        }

        latch.await( 10, TimeUnit.SECONDS );
        executor.shutdown();

        assertThat( successCount.get() ).isEqualTo( userCount );
        assertThat( failureCount.get() ).isZero();
        verify( createTransactionService, times( userCount * 2 ) ).execute( any( Transaction.class ) );
    }

    @Test
    void execute_concurrentSendsSameUser_allSucceed() throws InterruptedException {
        // Verifies that 10 concurrent send requests from the same user all process correctly.
        int threadCount = 10;

        UserCredentials senderUser = buildUser( SENDER_EMAIL );
        UserCredentials recipientUser = buildUser( RECIPIENT_EMAIL );

        Profile recipientProfile = buildProfile( RECIPIENT_USERNAME, "John", "Doe", recipientUser );
        Profile senderProfile = buildProfile( SENDER_USERNAME, "Jane", "Smith", senderUser );

        when( transferaWalletRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenAnswer( inv -> Optional.of( buildWallet( SENDER_WALLET_ID, senderUser, new BigDecimal( "100.00" ) ) ) );
        when( profileRepository.findByUserName( RECIPIENT_USERNAME ) )
                .thenReturn( Optional.of( recipientProfile ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( RECIPIENT_EMAIL ) )
                .thenAnswer( inv -> Optional.of( buildWallet( RECIPIENT_WALLET_ID, recipientUser, new BigDecimal( "0.00" ) ) ) );
        when( profileRepository.findByUserCredentialsEmail( SENDER_EMAIL ) )
                .thenReturn( Optional.of( senderProfile ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( inv -> inv.getArgument( 0 ) );

        ExecutorService executor = Executors.newFixedThreadPool( threadCount );
        CountDownLatch latch = new CountDownLatch( threadCount );
        AtomicInteger successCount = new AtomicInteger( 0 );
        AtomicInteger failureCount = new AtomicInteger( 0 );

        for ( int i = 0; i < threadCount; i++ ) {
            executor.submit( () -> {
                try {
                    SecurityContext context = new SecurityContextImpl();
                    context.setAuthentication(
                            new UsernamePasswordAuthenticationToken( SENDER_EMAIL, null, null )
                    );
                    SecurityContextHolder.setContext( context );

                    SendMoneyRequestDTO request = buildRequest( RECIPIENT_USERNAME, new BigDecimal( "5.00" ) );
                    ResponseEntity<TransferaWalletDTO> response =
                            sendMoneyTransferaWalletService.execute( request );

                    assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
                    successCount.incrementAndGet();
                } catch ( Exception e ) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                    SecurityContextHolder.clearContext();
                }
            } );
        }

        latch.await( 10, TimeUnit.SECONDS );
        executor.shutdown();

        assertThat( successCount.get() ).isEqualTo( threadCount );
        assertThat( failureCount.get() ).isZero();
        verify( transferaWalletRepository, times( threadCount * 2 ) ).save( any( TransferaWallet.class ) );
    }
}