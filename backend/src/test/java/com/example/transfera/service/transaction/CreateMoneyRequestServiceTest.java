package com.example.transfera.service.transaction;

import com.example.transfera.domain.money_request.MoneyRequest;
import com.example.transfera.domain.money_request.MoneyRequestRepository;
import com.example.transfera.domain.profile.Profile;
import com.example.transfera.domain.profile.ProfileRepository;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.dto.MoneyRequestDTO.CreateMoneyRequestDTO;
import com.example.transfera.dto.MoneyRequestDTO.MoneyRequestDTO;
import com.example.transfera.exceptions.customExceptions.RequestMoneyFromYourself;
import com.example.transfera.exceptions.customExceptions.TransferaWalletNotFoundException;
import com.example.transfera.exceptions.customExceptions.UserNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith( MockitoExtension.class )
class CreateMoneyRequestServiceTest {

    @Mock
    private MoneyRequestRepository moneyRequestRepository;

    @Mock
    private TransferaWalletRepository transferaWalletRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private CreateMoneyRequestService createMoneyRequestService;

    private static final String REQUESTER_EMAIL = "requester@example.com";
    private static final String PAYER_EMAIL = "payer@example.com";
    private static final String REQUESTER_USERNAME = "requesteruser";
    private static final String PAYER_USERNAME = "payeruser";
    private static final UUID REQUESTER_WALLET_ID = UUID.randomUUID();
    private static final UUID PAYER_WALLET_ID = UUID.randomUUID();

    // ─── Setup ────────────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken( REQUESTER_EMAIL, null, null )
        );
    }

    // ─── Helper builders ──────────────────────────────────────────────────────

    private UserCredentials buildUser( String email ) {
        return new UserCredentials( email, "password" );
    }

    private TransferaWallet buildWallet( UUID id, UserCredentials user ) {
        TransferaWallet wallet = new TransferaWallet();
        wallet.setId( id );
        wallet.setWalletNumber( "1234567890" );
        wallet.setBalance( new BigDecimal( "100.00" ) );
        wallet.setUserCredentials( user );
        return wallet;
    }

    private Profile buildProfile( String userName, UserCredentials user ) {
        return new Profile( userName, "First", "Last", "5551234567", user );
    }

    private CreateMoneyRequestDTO buildRequest( String recipientUsername, BigDecimal amount, String note ) {
        CreateMoneyRequestDTO dto = new CreateMoneyRequestDTO();
        try {
            var usernameField = CreateMoneyRequestDTO.class.getDeclaredField( "recipientUsername" );
            usernameField.setAccessible( true );
            usernameField.set( dto, recipientUsername );

            var amountField = CreateMoneyRequestDTO.class.getDeclaredField( "amount" );
            amountField.setAccessible( true );
            amountField.set( dto, amount );

            var noteField = CreateMoneyRequestDTO.class.getDeclaredField( "note" );
            noteField.setAccessible( true );
            noteField.set( dto, note );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        return dto;
    }

    // ─── Unit Tests ───────────────────────────────────────────────────────────

    @Test
    void execute_happyPath_savesMoneyRequestAndReturns200() {
        // Verifies that a valid request is saved and returns 200 with MoneyRequestDTO.
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );
        UserCredentials payerUser = buildUser( PAYER_EMAIL );

        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser );
        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser );

        Profile payerProfile = buildProfile( PAYER_USERNAME, payerUser );
        Profile requesterProfile = buildProfile( REQUESTER_USERNAME, requesterUser );

        when( transferaWalletRepository.findByUserCredentialsEmail( REQUESTER_EMAIL ) )
                .thenReturn( Optional.of( requesterWallet ) );
        when( profileRepository.findByUserName( PAYER_USERNAME ) )
                .thenReturn( Optional.of( payerProfile ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( PAYER_EMAIL ) )
                .thenReturn( Optional.of( payerWallet ) );
        when( profileRepository.findByUserCredentialsEmail( REQUESTER_EMAIL ) )
                .thenReturn( Optional.of( requesterProfile ) );
        when( moneyRequestRepository.save( any( MoneyRequest.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        ResponseEntity<MoneyRequestDTO> response = createMoneyRequestService.execute(
                buildRequest( PAYER_USERNAME, new BigDecimal( "50.00" ), "Dinner split" )
        );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).isNotNull();
        assertThat( response.getBody().getAmount() ).isEqualByComparingTo( new BigDecimal( "50.00" ) );
        verify( moneyRequestRepository, times( 1 ) ).save( any( MoneyRequest.class ) );
    }

    @Test
    void execute_happyPath_requesterAndRequesteeAreSetCorrectly() {
        // Verifies that requester is set to the requester's username and requestee to the payer's username.
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );
        UserCredentials payerUser = buildUser( PAYER_EMAIL );

        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser );
        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser );

        Profile payerProfile = buildProfile( PAYER_USERNAME, payerUser );
        Profile requesterProfile = buildProfile( REQUESTER_USERNAME, requesterUser );

        when( transferaWalletRepository.findByUserCredentialsEmail( REQUESTER_EMAIL ) )
                .thenReturn( Optional.of( requesterWallet ) );
        when( profileRepository.findByUserName( PAYER_USERNAME ) )
                .thenReturn( Optional.of( payerProfile ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( PAYER_EMAIL ) )
                .thenReturn( Optional.of( payerWallet ) );
        when( profileRepository.findByUserCredentialsEmail( REQUESTER_EMAIL ) )
                .thenReturn( Optional.of( requesterProfile ) );
        when( moneyRequestRepository.save( any( MoneyRequest.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        ResponseEntity<MoneyRequestDTO> response = createMoneyRequestService.execute(
                buildRequest( PAYER_USERNAME, new BigDecimal( "50.00" ), "Dinner split" )
        );

        assertThat( response.getBody().getRequester() ).isEqualTo( REQUESTER_USERNAME );
        assertThat( response.getBody().getRequestee() ).isEqualTo( PAYER_USERNAME );
    }

    @Test
    void execute_requesterWalletNotFound_throwsTransferaWalletNotFoundException() {
        // Verifies that missing requester wallet throws correct exception.
        when( transferaWalletRepository.findByUserCredentialsEmail( REQUESTER_EMAIL ) )
                .thenReturn( Optional.empty() );

        assertThatThrownBy( () -> createMoneyRequestService.execute(
                buildRequest( PAYER_USERNAME, new BigDecimal( "50.00" ), "" )
        ) ).isInstanceOf( TransferaWalletNotFoundException.class );

        verify( moneyRequestRepository, never() ).save( any() );
    }

    @Test
    void execute_payerNotFound_throwsUserNotFound() {
        // Verifies that unknown payer username throws UserNotFound.
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );
        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser );

        when( transferaWalletRepository.findByUserCredentialsEmail( REQUESTER_EMAIL ) )
                .thenReturn( Optional.of( requesterWallet ) );
        when( profileRepository.findByUserName( "unknownuser" ) )
                .thenReturn( Optional.empty() );

        assertThatThrownBy( () -> createMoneyRequestService.execute(
                buildRequest( "unknownuser", new BigDecimal( "50.00" ), "" )
        ) ).isInstanceOf( UserNotFound.class );

        verify( moneyRequestRepository, never() ).save( any() );
    }

    @Test
    void execute_requestToYourself_throwsIllegalArgumentException() {
        // Verifies that requesting money from yourself throws IllegalArgumentException.
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );
        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser );
        Profile requesterProfile = buildProfile( REQUESTER_USERNAME, requesterUser );

        when( transferaWalletRepository.findByUserCredentialsEmail( REQUESTER_EMAIL ) )
                .thenReturn( Optional.of( requesterWallet ) );
        when( profileRepository.findByUserName( REQUESTER_USERNAME ) )
                .thenReturn( Optional.of( requesterProfile ) );

        assertThatThrownBy( () -> createMoneyRequestService.execute(
                buildRequest( REQUESTER_USERNAME, new BigDecimal( "50.00" ), "" )
        ) ).isInstanceOf( RequestMoneyFromYourself.class )
                .hasMessage( "You cannot request money from yourself." );

        verify( moneyRequestRepository, never() ).save( any() );
    }

    @Test
    void execute_zeroAmount_savesRequest() {
        // Verifies that a zero amount request is still saved (validation is frontend responsibility).
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );
        UserCredentials payerUser = buildUser( PAYER_EMAIL );

        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser );
        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser );

        Profile payerProfile = buildProfile( PAYER_USERNAME, payerUser );
        Profile requesterProfile = buildProfile( REQUESTER_USERNAME, requesterUser );

        when( transferaWalletRepository.findByUserCredentialsEmail( REQUESTER_EMAIL ) )
                .thenReturn( Optional.of( requesterWallet ) );
        when( profileRepository.findByUserName( PAYER_USERNAME ) )
                .thenReturn( Optional.of( payerProfile ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( PAYER_EMAIL ) )
                .thenReturn( Optional.of( payerWallet ) );
        when( profileRepository.findByUserCredentialsEmail( REQUESTER_EMAIL ) )
                .thenReturn( Optional.of( requesterProfile ) );
        when( moneyRequestRepository.save( any( MoneyRequest.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        ResponseEntity<MoneyRequestDTO> response = createMoneyRequestService.execute(
                buildRequest( PAYER_USERNAME, BigDecimal.ZERO, "" )
        );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        verify( moneyRequestRepository, times( 1 ) ).save( any( MoneyRequest.class ) );
    }

    // ─── Concurrency Tests ────────────────────────────────────────────────────

    @Test
    void execute_concurrentRequestsFromDifferentUsers_allSucceed() throws InterruptedException {
        // Verifies that 5 concurrent money requests from different users all succeed.
        int userCount = 5;

        for ( int i = 0; i < userCount; i++ ) {
            String requesterEmail = "requester" + i + "@example.com";
            String payerEmail = "payer" + i + "@example.com";
            String payerUsername = "payer" + i;
            String requesterUsername = "requester" + i;

            UserCredentials requesterUser = buildUser( requesterEmail );
            UserCredentials payerUser = buildUser( payerEmail );

            TransferaWallet requesterWallet = buildWallet( UUID.randomUUID(), requesterUser );
            TransferaWallet payerWallet = buildWallet( UUID.randomUUID(), payerUser );

            Profile payerProfile = buildProfile( payerUsername, payerUser );
            Profile requesterProfile = buildProfile( requesterUsername, requesterUser );

            when( transferaWalletRepository.findByUserCredentialsEmail( requesterEmail ) )
                    .thenReturn( Optional.of( requesterWallet ) );
            when( profileRepository.findByUserName( payerUsername ) )
                    .thenReturn( Optional.of( payerProfile ) );
            when( transferaWalletRepository.findByUserCredentialsEmail( payerEmail ) )
                    .thenReturn( Optional.of( payerWallet ) );
            when( profileRepository.findByUserCredentialsEmail( requesterEmail ) )
                    .thenReturn( Optional.of( requesterProfile ) );
        }

        when( moneyRequestRepository.save( any( MoneyRequest.class ) ) )
                .thenAnswer( inv -> inv.getArgument( 0 ) );

        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool( userCount );
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch( userCount );
        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger( 0 );
        java.util.concurrent.atomic.AtomicInteger failureCount = new java.util.concurrent.atomic.AtomicInteger( 0 );

        for ( int i = 0; i < userCount; i++ ) {
            final String requesterEmail = "requester" + i + "@example.com";
            final String payerUsername = "payer" + i;

            executor.submit( () -> {
                try {
                    var context = new org.springframework.security.core.context.SecurityContextImpl();
                    context.setAuthentication(
                            new UsernamePasswordAuthenticationToken( requesterEmail, null, null )
                    );
                    SecurityContextHolder.setContext( context );

                    ResponseEntity<MoneyRequestDTO> response = createMoneyRequestService.execute(
                            buildRequest( payerUsername, new BigDecimal( "50.00" ), "Split" )
                    );

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

        latch.await( 10, java.util.concurrent.TimeUnit.SECONDS );
        executor.shutdown();

        assertThat( successCount.get() ).isEqualTo( userCount );
        assertThat( failureCount.get() ).isZero();
        verify( moneyRequestRepository, times( userCount ) ).save( any( MoneyRequest.class ) );
    }
}