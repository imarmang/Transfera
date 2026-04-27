package com.example.transfera.service.transaction;

import com.example.transfera.domain.money_request.MoneyRequest;
import com.example.transfera.domain.money_request.MoneyRequestRepository;
import com.example.transfera.domain.money_request.MoneyRequestStatus;
import com.example.transfera.domain.transaction.Transaction;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.dto.MoneyRequestDTO.RespondToMoneyRequestDTO;
import com.example.transfera.exceptions.customExceptions.InsufficientBalanceTransferaWalletException;
import com.example.transfera.exceptions.customExceptions.TransferaWalletNotFoundException;
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
class RespondToMoneyRequestServiceTest {

    @Mock
    private MoneyRequestRepository moneyRequestRepository;

    @Mock
    private TransferaWalletRepository transferaWalletRepository;

    @Mock
    private CreateTransactionService createTransactionService;

    @InjectMocks
    private RespondToMoneyRequestService respondToMoneyRequestService;

    private static final String PAYER_EMAIL = "payer@example.com";
    private static final String REQUESTER_EMAIL = "requester@example.com";
    private static final String PAYER_USERNAME = "payeruser";
    private static final String REQUESTER_USERNAME = "requesteruser";
    private static final UUID PAYER_WALLET_ID = UUID.randomUUID();
    private static final UUID REQUESTER_WALLET_ID = UUID.randomUUID();
    private static final UUID MONEY_REQUEST_ID = UUID.randomUUID();

    // ─── Setup ────────────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken( PAYER_EMAIL, null, null )
        );
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

    private MoneyRequest buildMoneyRequest( TransferaWallet requesterWallet,
                                            TransferaWallet payerWallet,
                                            BigDecimal amount,
                                            MoneyRequestStatus status ) {
        return MoneyRequest.builder()
                .moneyRequestId( MONEY_REQUEST_ID )
                .amount( amount )
                .note( "Dinner split" )
                .requester( REQUESTER_USERNAME )
                .requestee( PAYER_USERNAME )
                .status( status )
                .requesterWallet( requesterWallet )
                .payerWallet( payerWallet )
                .build();
    }

    private RespondToMoneyRequestDTO buildResponse( UUID moneyRequestId, MoneyRequestStatus response ) {
        RespondToMoneyRequestDTO dto = new RespondToMoneyRequestDTO();
        try {
            var idField = RespondToMoneyRequestDTO.class.getDeclaredField( "moneyRequestId" );
            idField.setAccessible( true );
            idField.set( dto, moneyRequestId );

            var responseField = RespondToMoneyRequestDTO.class.getDeclaredField( "response" );
            responseField.setAccessible( true );
            responseField.set( dto, response );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        return dto;
    }

    // ─── Unit Tests ───────────────────────────────────────────────────────────

    @Test
    void execute_approve_transfersMoneyAndUpdatesStatus() {
        // Verifies that approving a request moves money and sets status to APPROVED.
        UserCredentials payerUser = buildUser( PAYER_EMAIL );
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );

        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser, new BigDecimal( "100.00" ) );
        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser, new BigDecimal( "0.00" ) );

        MoneyRequest moneyRequest = buildMoneyRequest( requesterWallet, payerWallet, new BigDecimal( "50.00" ), MoneyRequestStatus.PENDING );

        when( moneyRequestRepository.findById( MONEY_REQUEST_ID ) )
                .thenReturn( Optional.of( moneyRequest ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( PAYER_EMAIL ) )
                .thenReturn( Optional.of( payerWallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( inv -> inv.getArgument( 0 ) );

        ResponseEntity<Void> response = respondToMoneyRequestService.execute(
                buildResponse( MONEY_REQUEST_ID, MoneyRequestStatus.APPROVED )
        );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( payerWallet.getBalance() ).isEqualByComparingTo( new BigDecimal( "50.00" ) );
        assertThat( requesterWallet.getBalance() ).isEqualByComparingTo( new BigDecimal( "50.00" ) );
        assertThat( moneyRequest.getStatus() ).isEqualTo( MoneyRequestStatus.APPROVED );
    }

    @Test
    void execute_approve_createsTwoTransactions() {
        // Verifies that approving creates both a SEND and RECEIVED transaction.
        UserCredentials payerUser = buildUser( PAYER_EMAIL );
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );

        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser, new BigDecimal( "100.00" ) );
        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser, new BigDecimal( "0.00" ) );

        MoneyRequest moneyRequest = buildMoneyRequest( requesterWallet, payerWallet, new BigDecimal( "50.00" ), MoneyRequestStatus.PENDING );

        when( moneyRequestRepository.findById( MONEY_REQUEST_ID ) )
                .thenReturn( Optional.of( moneyRequest ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( PAYER_EMAIL ) )
                .thenReturn( Optional.of( payerWallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( inv -> inv.getArgument( 0 ) );

        respondToMoneyRequestService.execute(
                buildResponse( MONEY_REQUEST_ID, MoneyRequestStatus.APPROVED )
        );

        verify( createTransactionService, times( 2 ) ).execute( any( Transaction.class ) );
    }

    @Test
    void execute_approve_savesBothWallets() {
        // Verifies that both wallets are saved exactly once on approval.
        UserCredentials payerUser = buildUser( PAYER_EMAIL );
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );

        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser, new BigDecimal( "100.00" ) );
        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser, new BigDecimal( "0.00" ) );

        MoneyRequest moneyRequest = buildMoneyRequest( requesterWallet, payerWallet, new BigDecimal( "50.00" ), MoneyRequestStatus.PENDING );

        when( moneyRequestRepository.findById( MONEY_REQUEST_ID ) )
                .thenReturn( Optional.of( moneyRequest ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( PAYER_EMAIL ) )
                .thenReturn( Optional.of( payerWallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( inv -> inv.getArgument( 0 ) );

        respondToMoneyRequestService.execute(
                buildResponse( MONEY_REQUEST_ID, MoneyRequestStatus.APPROVED )
        );

        verify( transferaWalletRepository, times( 2 ) ).save( any( TransferaWallet.class ) );
    }

    @Test
    void execute_decline_updatesStatusAndCreatesNoTransaction() {
        // Verifies that declining updates status and creates no transaction (no money moved).
        UserCredentials payerUser = buildUser( PAYER_EMAIL );
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );

        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser, new BigDecimal( "100.00" ) );
        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser, new BigDecimal( "0.00" ) );

        MoneyRequest moneyRequest = buildMoneyRequest( requesterWallet, payerWallet, new BigDecimal( "50.00" ), MoneyRequestStatus.PENDING );

        when( moneyRequestRepository.findById( MONEY_REQUEST_ID ) )
                .thenReturn( Optional.of( moneyRequest ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( PAYER_EMAIL ) )
                .thenReturn( Optional.of( payerWallet ) );

        respondToMoneyRequestService.execute(
                buildResponse( MONEY_REQUEST_ID, MoneyRequestStatus.DECLINED )
        );

        assertThat( moneyRequest.getStatus() ).isEqualTo( MoneyRequestStatus.DECLINED );
        verify( createTransactionService, never() ).execute( any( Transaction.class ) );
        verify( transferaWalletRepository, never() ).save( any( TransferaWallet.class ) );
    }

    @Test
    void execute_decline_doesNotMoveMoney() {
        // Verifies that declining a request does not change any wallet balances.
        UserCredentials payerUser = buildUser( PAYER_EMAIL );
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );

        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser, new BigDecimal( "100.00" ) );
        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser, new BigDecimal( "0.00" ) );

        MoneyRequest moneyRequest = buildMoneyRequest( requesterWallet, payerWallet, new BigDecimal( "50.00" ), MoneyRequestStatus.PENDING );

        when( moneyRequestRepository.findById( MONEY_REQUEST_ID ) )
                .thenReturn( Optional.of( moneyRequest ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( PAYER_EMAIL ) )
                .thenReturn( Optional.of( payerWallet ) );

        respondToMoneyRequestService.execute(
                buildResponse( MONEY_REQUEST_ID, MoneyRequestStatus.DECLINED )
        );

        assertThat( payerWallet.getBalance() ).isEqualByComparingTo( new BigDecimal( "100.00" ) );
        assertThat( requesterWallet.getBalance() ).isEqualByComparingTo( new BigDecimal( "0.00" ) );
    }

    @Test
    void execute_requestNotFound_throwsIllegalArgumentException() {
        // Verifies that a non-existent request ID throws IllegalArgumentException.
        when( moneyRequestRepository.findById( MONEY_REQUEST_ID ) )
                .thenReturn( Optional.empty() );

        assertThatThrownBy( () -> respondToMoneyRequestService.execute(
                buildResponse( MONEY_REQUEST_ID, MoneyRequestStatus.APPROVED )
        ) ).isInstanceOf( IllegalArgumentException.class )
                .hasMessage( "Money request not found." );

        verify( transferaWalletRepository, never() ).save( any() );
        verify( createTransactionService, never() ).execute( any() );
    }

    @Test
    void execute_requestAlreadyResponded_throwsIllegalArgumentException() {
        // Verifies that responding to an already-resolved request throws IllegalArgumentException.
        UserCredentials payerUser = buildUser( PAYER_EMAIL );
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );

        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser, new BigDecimal( "100.00" ) );
        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser, new BigDecimal( "0.00" ) );

        MoneyRequest moneyRequest = buildMoneyRequest( requesterWallet, payerWallet, new BigDecimal( "50.00" ), MoneyRequestStatus.APPROVED );

        when( moneyRequestRepository.findById( MONEY_REQUEST_ID ) )
                .thenReturn( Optional.of( moneyRequest ) );

        assertThatThrownBy( () -> respondToMoneyRequestService.execute(
                buildResponse( MONEY_REQUEST_ID, MoneyRequestStatus.APPROVED )
        ) ).isInstanceOf( IllegalArgumentException.class )
                .hasMessage( "This request has already been responded to." );

        verify( transferaWalletRepository, never() ).save( any() );
        verify( createTransactionService, never() ).execute( any() );
    }

    @Test
    void execute_unauthorizedPayer_throwsIllegalArgumentException() {
        // Verifies that a user who is not the payer cannot respond to the request.
        UserCredentials payerUser = buildUser( PAYER_EMAIL );
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );
        UserCredentials otherUser = buildUser( "other@example.com" );

        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser, new BigDecimal( "100.00" ) );
        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser, new BigDecimal( "0.00" ) );
        TransferaWallet otherWallet = buildWallet( UUID.randomUUID(), otherUser, new BigDecimal( "100.00" ) );

        MoneyRequest moneyRequest = buildMoneyRequest( requesterWallet, payerWallet, new BigDecimal( "50.00" ), MoneyRequestStatus.PENDING );

        when( moneyRequestRepository.findById( MONEY_REQUEST_ID ) )
                .thenReturn( Optional.of( moneyRequest ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( PAYER_EMAIL ) )
                .thenReturn( Optional.of( otherWallet ) );

        assertThatThrownBy( () -> respondToMoneyRequestService.execute(
                buildResponse( MONEY_REQUEST_ID, MoneyRequestStatus.APPROVED )
        ) ).isInstanceOf( IllegalArgumentException.class )
                .hasMessage( "You are not authorized to respond to this request." );

        verify( transferaWalletRepository, never() ).save( any() );
        verify( createTransactionService, never() ).execute( any() );
    }

    @Test
    void execute_approve_insufficientBalance_throwsInsufficientBalanceException() {
        // Verifies that approving with insufficient balance throws InsufficientBalanceException.
        UserCredentials payerUser = buildUser( PAYER_EMAIL );
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );

        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser, new BigDecimal( "10.00" ) );
        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser, new BigDecimal( "0.00" ) );

        MoneyRequest moneyRequest = buildMoneyRequest( requesterWallet, payerWallet, new BigDecimal( "50.00" ), MoneyRequestStatus.PENDING );

        when( moneyRequestRepository.findById( MONEY_REQUEST_ID ) )
                .thenReturn( Optional.of( moneyRequest ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( PAYER_EMAIL ) )
                .thenReturn( Optional.of( payerWallet ) );

        assertThatThrownBy( () -> respondToMoneyRequestService.execute(
                buildResponse( MONEY_REQUEST_ID, MoneyRequestStatus.APPROVED )
        ) ).isInstanceOf( InsufficientBalanceTransferaWalletException.class );

        verify( transferaWalletRepository, never() ).save( any() );
        verify( createTransactionService, never() ).execute( any() );
    }

    @Test
    void execute_payerWalletNotFound_throwsTransferaWalletNotFoundException() {
        // Verifies that missing payer wallet throws TransferaWalletNotFoundException.
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );
        UserCredentials payerUser = buildUser( PAYER_EMAIL );

        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser, new BigDecimal( "0.00" ) );
        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser, new BigDecimal( "100.00" ) );

        MoneyRequest moneyRequest = buildMoneyRequest( requesterWallet, payerWallet, new BigDecimal( "50.00" ), MoneyRequestStatus.PENDING );

        when( moneyRequestRepository.findById( MONEY_REQUEST_ID ) )
                .thenReturn( Optional.of( moneyRequest ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( PAYER_EMAIL ) )
                .thenReturn( Optional.empty() );

        assertThatThrownBy( () -> respondToMoneyRequestService.execute(
                buildResponse( MONEY_REQUEST_ID, MoneyRequestStatus.APPROVED )
        ) ).isInstanceOf( TransferaWalletNotFoundException.class );

        verify( transferaWalletRepository, never() ).save( any() );
        verify( createTransactionService, never() ).execute( any() );
    }

    // ─── Concurrency Tests ────────────────────────────────────────────────────

    @Test
    void execute_concurrentApprovals_onlyOneSucceeds() throws InterruptedException {
        // Verifies that concurrent attempts to approve the same request only succeed once.
        UserCredentials payerUser = buildUser( PAYER_EMAIL );
        UserCredentials requesterUser = buildUser( REQUESTER_EMAIL );

        TransferaWallet payerWallet = buildWallet( PAYER_WALLET_ID, payerUser, new BigDecimal( "100.00" ) );
        TransferaWallet requesterWallet = buildWallet( REQUESTER_WALLET_ID, requesterUser, new BigDecimal( "0.00" ) );

        when( transferaWalletRepository.findByUserCredentialsEmail( PAYER_EMAIL ) )
                .thenReturn( Optional.of( payerWallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( inv -> inv.getArgument( 0 ) );

        MoneyRequest pendingRequest = buildMoneyRequest( requesterWallet, payerWallet, new BigDecimal( "50.00" ), MoneyRequestStatus.PENDING );
        MoneyRequest approvedRequest = buildMoneyRequest( requesterWallet, payerWallet, new BigDecimal( "50.00" ), MoneyRequestStatus.APPROVED );

        when( moneyRequestRepository.findById( MONEY_REQUEST_ID ) )
                .thenReturn( Optional.of( pendingRequest ) )
                .thenReturn( Optional.of( approvedRequest ) );

        int threadCount = 2;
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool( threadCount );
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch( threadCount );
        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger( 0 );
        java.util.concurrent.atomic.AtomicInteger failureCount = new java.util.concurrent.atomic.AtomicInteger( 0 );

        for ( int i = 0; i < threadCount; i++ ) {
            executor.submit( () -> {
                try {
                    var context = new org.springframework.security.core.context.SecurityContextImpl();
                    context.setAuthentication(
                            new UsernamePasswordAuthenticationToken( PAYER_EMAIL, null, null )
                    );
                    SecurityContextHolder.setContext( context );

                    respondToMoneyRequestService.execute(
                            buildResponse( MONEY_REQUEST_ID, MoneyRequestStatus.APPROVED )
                    );
                    successCount.incrementAndGet();
                } catch ( IllegalArgumentException e ) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                    SecurityContextHolder.clearContext();
                }
            } );
        }

        latch.await( 10, java.util.concurrent.TimeUnit.SECONDS );
        executor.shutdown();

        assertThat( successCount.get() ).isEqualTo( 1 );
        assertThat( failureCount.get() ).isEqualTo( 1 );
    }
}