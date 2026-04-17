package com.example.transfera.service.transferaWallet;

import com.example.transfera.domain.linked_bank_account.LinkedBankAccount;
import com.example.transfera.domain.linked_bank_account.LinkedBankAccountRepository;
import com.example.transfera.domain.transaction.Transaction;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.dto.TransferaWalletDTO.AddMoneyRequestDTO;
import com.example.transfera.dto.TransferaWalletDTO.TransferaWalletDTO;
import com.example.transfera.exceptions.customExceptions.LinkedBankAccountNotFoundException;
import com.example.transfera.exceptions.customExceptions.TransferaWalletNotFoundException;
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
class AddMoneyTransferaWalletServiceTest {

    @Mock
    private TransferaWalletRepository transferaWalletRepository;

    @Mock
    private LinkedBankAccountRepository linkedBankAccountRepository;

    @Mock
    private CreateTransactionService createTransactionService;

    @InjectMocks
    private AddMoneyTransferaWalletService addMoneyTransferaWalletService;

    private static final String EMAIL = "tatevik@example.com";
    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final UUID WALLET_ID = UUID.randomUUID();

    // ─── Setup ────────────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken( EMAIL, null, null );
        SecurityContextHolder.getContext().setAuthentication( auth );
    }

    // ─── Helper builders ──────────────────────────────────────────────────────

    private UserCredentials buildUser() {
        return new UserCredentials( EMAIL, "password" );
    }

    private LinkedBankAccount buildLinkedBankAccount( UserCredentials user ) {
        return new LinkedBankAccount(
                user,
                "Bank of America",
                "Tatevik Test",
                "123456789",
                "021000021",
                "CHECKING"
        );
    }

    private TransferaWallet buildWallet( UserCredentials user, BigDecimal balance ) {
        TransferaWallet wallet = new TransferaWallet();
        wallet.setId( WALLET_ID );
        wallet.setWalletNumber( "1234567890" );
        wallet.setBalance( balance );
        wallet.setUserCredentials( user );
        return wallet;
    }

    private AddMoneyRequestDTO buildRequest( UUID linkedBankAccountId, BigDecimal amount ) {
        AddMoneyRequestDTO dto = new AddMoneyRequestDTO();
        try {
            var idField = AddMoneyRequestDTO.class.getDeclaredField( "linkedBankAccountId" );
            idField.setAccessible( true );
            idField.set( dto, linkedBankAccountId );

            var amountField = AddMoneyRequestDTO.class.getDeclaredField( "amount" );
            amountField.setAccessible( true );
            amountField.set( dto, amount );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        return dto;
    }

    // ─── Unit Tests ───────────────────────────────────────────────────────────

    @Test
    void execute_happyPath_addsAmountToBalance() {
        // Arrange
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        TransferaWallet wallet = buildWallet( user, new BigDecimal( "100.00" ) );
        AddMoneyRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "50.00" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        // Act
        ResponseEntity<TransferaWalletDTO> response = addMoneyTransferaWalletService.execute( request );

        // Assert
        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).isNotNull();
        assertThat( response.getBody().getBalance() ).isEqualByComparingTo( new BigDecimal( "150.00" ) );
        verify( transferaWalletRepository ).save( wallet );
        verify( createTransactionService, times( 1 ) ).execute( any( Transaction.class ) );
    }

    @Test
    void execute_linkedBankAccountNotFound_throwsLinkedBankAccountNotFoundException() {
        // Arrange
        AddMoneyRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "50.00" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.empty() );

        // Act & Assert
        assertThatThrownBy( () -> addMoneyTransferaWalletService.execute( request ) )
                .isInstanceOf( LinkedBankAccountNotFoundException.class );

        verify( transferaWalletRepository, never() ).findByUserCredentialsEmail( any() );
        verify( transferaWalletRepository, never() ).save( any() );
    }

    @Test
    void execute_linkedBankAccountNotFound_neverCreatesTransaction() {
        AddMoneyRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "50.00" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.empty() );

        assertThatThrownBy( () -> addMoneyTransferaWalletService.execute( request ) )
                .isInstanceOf( LinkedBankAccountNotFoundException.class );

        verify( createTransactionService, never() ).execute( any( Transaction.class ) );
    }

    @Test
    void execute_walletNotFound_throwsTransferaWalletNotFoundException() {
        // Arrange
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        AddMoneyRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "50.00" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.empty() );

        // Act & Assert
        assertThatThrownBy( () -> addMoneyTransferaWalletService.execute( request ) )
                .isInstanceOf( TransferaWalletNotFoundException.class );

        verify( transferaWalletRepository, never() ).save( any() );
    }

    @Test
    void execute_walletNotFound_neverCreatesTransaction() {
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        AddMoneyRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "50.00" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.empty() );

        assertThatThrownBy( () -> addMoneyTransferaWalletService.execute( request ) )
                .isInstanceOf( TransferaWalletNotFoundException.class );

        verify( createTransactionService, never() ).execute( any( Transaction.class ) );
    }

    @Test
    void execute_zeroStartingBalance_addsCorrectly() {
        // Arrange
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        TransferaWallet wallet = buildWallet( user, new BigDecimal( "0.00" ) );
        AddMoneyRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "25.50" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        // Act
        ResponseEntity<TransferaWalletDTO> response = addMoneyTransferaWalletService.execute( request );

        // Assert
        assertThat( response.getBody().getBalance() ).isEqualByComparingTo( new BigDecimal( "25.50" ) );
        verify( createTransactionService, times( 1 ) ).execute( any( Transaction.class ) );

    }

    @Test
    void execute_savesWalletExactlyOnce() {
        // Arrange
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        TransferaWallet wallet = buildWallet( user, new BigDecimal( "200.00" ) );
        AddMoneyRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "100.00" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        // Act
        addMoneyTransferaWalletService.execute( request );

        // Assert
        verify( transferaWalletRepository, times( 1 ) ).save( wallet );
        verify( createTransactionService, times( 1 ) ).execute( any( Transaction.class ) );

    }

    @Test
    void execute_largeAmount_addsCorrectly() {
        // Arrange
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        TransferaWallet wallet = buildWallet( user, new BigDecimal( "999999.99" ) );
        AddMoneyRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "999999.99" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        // Act
        ResponseEntity<TransferaWalletDTO> response = addMoneyTransferaWalletService.execute( request );

        // Assert
        assertThat( response.getBody().getBalance() ).isEqualByComparingTo( new BigDecimal( "1999999.98" ) );
        verify( createTransactionService, times( 1 ) ).execute( any( Transaction.class ) );
    }

    @Test
    void execute_decimalAmount_addsCorrectly() {
        // Arrange
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        TransferaWallet wallet = buildWallet( user, new BigDecimal( "10.00" ) );
        AddMoneyRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "0.99" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        // Act
        ResponseEntity<TransferaWalletDTO> response = addMoneyTransferaWalletService.execute( request );

        // Assert
        assertThat( response.getBody().getBalance() ).isEqualByComparingTo( new BigDecimal( "10.99" ) );
        verify( createTransactionService, times( 1 ) ).execute( any( Transaction.class ) );

    }

    // ─── Concurrency Tests ────────────────────────────────────────────────────

    @Test
    void execute_concurrentCallsSameUser_allSucceed() throws InterruptedException {
        // 10 concurrent add-money requests from the same user
        // Each thread gets its own SecurityContext as in a real server
        int threadCount = 10;
        BigDecimal addAmount = new BigDecimal( "10.00" );

        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );

        // All mocks set up on the main thread before executor starts
        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( any(), any() ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( any() ) )
                .thenAnswer( inv -> Optional.of( buildWallet( user, new BigDecimal( "0.00" ) ) ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        ExecutorService executor = Executors.newFixedThreadPool( threadCount );
        CountDownLatch latch = new CountDownLatch( threadCount );
        AtomicInteger successCount = new AtomicInteger( 0 );
        AtomicInteger failureCount = new AtomicInteger( 0 );
        List<BigDecimal> results = new CopyOnWriteArrayList<>();

        for ( int i = 0; i < threadCount; i++ ) {
            executor.submit( () -> {
                try {
                    SecurityContext context = new SecurityContextImpl();
                    context.setAuthentication(
                            new UsernamePasswordAuthenticationToken( EMAIL, null, null )
                    );
                    SecurityContextHolder.setContext( context );

                    AddMoneyRequestDTO request = buildRequest( ACCOUNT_ID, addAmount );
                    ResponseEntity<TransferaWalletDTO> response =
                            addMoneyTransferaWalletService.execute( request );

                    results.add( response.getBody().getBalance() );
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

        // All 10 calls must succeed
        assertThat( successCount.get() ).isEqualTo( threadCount );
        assertThat( failureCount.get() ).isZero();

        // Each call adds $10 to a fresh $0 wallet = $10 per call
        results.forEach( balance ->
                assertThat( balance ).isEqualByComparingTo( new BigDecimal( "10.00" ) )
        );
    }

    @Test
    void execute_concurrentCallsDifferentUsers_isolatedCorrectly() throws InterruptedException {
        // 5 different users adding money simultaneously
        // All mocks set up on the main thread before executor starts
        // to avoid Mockito thread-safety issues
        int userCount = 5;
        BigDecimal addAmount = new BigDecimal( "50.00" );
        BigDecimal startingBalance = new BigDecimal( "100.00" );

        List<String> emails = new ArrayList<>();
        List<UUID> accountIds = new ArrayList<>();

        for ( int i = 0; i < userCount; i++ ) {
            String userEmail = "user" + i + "@example.com";
            UUID accountId = UUID.randomUUID();
            emails.add( userEmail );
            accountIds.add( accountId );

            UserCredentials user = new UserCredentials( userEmail, "password" );
            LinkedBankAccount account = buildLinkedBankAccount( user );
            TransferaWallet wallet = buildWallet( user, startingBalance );

            when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( accountId, userEmail ) )
                    .thenReturn( Optional.of( account ) );
            when( transferaWalletRepository.findByUserCredentialsEmail( userEmail ) )
                    .thenReturn( Optional.of( wallet ) );
        }

        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( inv -> inv.getArgument( 0 ) );

        ExecutorService executor = Executors.newFixedThreadPool( userCount );
        CountDownLatch latch = new CountDownLatch( userCount );
        AtomicInteger successCount = new AtomicInteger( 0 );
        List<BigDecimal> results = new CopyOnWriteArrayList<>();

        for ( int i = 0; i < userCount; i++ ) {
            final String userEmail = emails.get( i );
            final UUID accountId = accountIds.get( i );

            executor.submit( () -> {
                try {
                    SecurityContext context = new SecurityContextImpl();
                    context.setAuthentication(
                            new UsernamePasswordAuthenticationToken( userEmail, null, null )
                    );
                    SecurityContextHolder.setContext( context );

                    AddMoneyRequestDTO request = buildRequest( accountId, addAmount );
                    ResponseEntity<TransferaWalletDTO> response =
                            addMoneyTransferaWalletService.execute( request );

                    results.add( response.getBody().getBalance() );
                    successCount.incrementAndGet();
                } catch ( Exception e ) {
                    // count silently
                } finally {
                    latch.countDown();
                    SecurityContextHolder.clearContext();
                }
            } );
        }

        latch.await( 10, TimeUnit.SECONDS );
        executor.shutdown();

        // All 5 users must succeed — no cross-contamination
        assertThat( successCount.get() ).isEqualTo( userCount );

        // Every user: $100 + $50 = $150
        results.forEach( balance ->
                assertThat( balance ).isEqualByComparingTo( new BigDecimal( "150.00" ) )
        );
    }

    @Test
    void execute_concurrentCalls_repositoryCalledCorrectNumberOfTimes() throws InterruptedException {
        // Verifies save() is called exactly once per request under concurrency
        int threadCount = 8;

        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( any(), any() ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( any() ) )
                .thenAnswer( inv -> Optional.of( buildWallet( user, new BigDecimal( "0.00" ) ) ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        ExecutorService executor = Executors.newFixedThreadPool( threadCount );
        CountDownLatch latch = new CountDownLatch( threadCount );

        for ( int i = 0; i < threadCount; i++ ) {
            executor.submit( () -> {
                try {
                    SecurityContext context = new SecurityContextImpl();
                    context.setAuthentication(
                            new UsernamePasswordAuthenticationToken( EMAIL, null, null )
                    );
                    SecurityContextHolder.setContext( context );

                    AddMoneyRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "10.00" ) );
                    addMoneyTransferaWalletService.execute( request );
                } finally {
                    latch.countDown();
                    SecurityContextHolder.clearContext();
                }
            } );
        }

        latch.await( 10, TimeUnit.SECONDS );
        executor.shutdown();

        // save() must be called exactly once per thread — no batching, no skipping
        verify( transferaWalletRepository, times( threadCount ) ).save( any( TransferaWallet.class ) );
    }
}