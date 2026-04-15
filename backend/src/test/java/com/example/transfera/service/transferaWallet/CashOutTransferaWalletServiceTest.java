package com.example.transfera.service.transferaWallet;

import com.example.transfera.domain.linked_bank_account.LinkedBankAccount;
import com.example.transfera.domain.linked_bank_account.LinkedBankAccountRepository;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.dto.TransferaWalletDTO.CashOutRequestDTO;
import com.example.transfera.dto.TransferaWalletDTO.TransferaWalletDTO;
import com.example.transfera.exceptions.customExceptions.InsufficientBalanceTransferaWalletException;
import com.example.transfera.exceptions.customExceptions.LinkedBankAccountNotFoundException;
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
class CashOutTransferaWalletServiceTest {

    @Mock
    private TransferaWalletRepository transferaWalletRepository;

    @Mock
    private LinkedBankAccountRepository linkedBankAccountRepository;

    @InjectMocks
    private CashOutMoneyTransferaWalletService cashOutMoneyTransferaWalletService;

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

    private CashOutRequestDTO buildRequest( UUID linkedBankAccountId, BigDecimal amount ) {
        CashOutRequestDTO dto = new CashOutRequestDTO();
        try {
            var idField = CashOutRequestDTO.class.getDeclaredField( "linkedBankAccountId" );
            idField.setAccessible( true );
            idField.set( dto, linkedBankAccountId );

            var amountField = CashOutRequestDTO.class.getDeclaredField( "amount" );
            amountField.setAccessible( true );
            amountField.set( dto, amount );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        return dto;
    }

    // ─── Unit Tests ───────────────────────────────────────────────────────────

    @Test
    void execute_happyPath_subtractsAmountFromBalance() {
        // Verifies that cashing out $50 from a $100 wallet results in a $50 balance.
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        TransferaWallet wallet = buildWallet( user, new BigDecimal( "100.00" ) );
        CashOutRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "50.00" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        ResponseEntity<TransferaWalletDTO> response = cashOutMoneyTransferaWalletService.execute( request );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).isNotNull();
        assertThat( response.getBody().getBalance() ).isEqualByComparingTo( new BigDecimal( "50.00" ) );
        verify( transferaWalletRepository ).save( wallet );
    }

    // Verifies that the service throws LinkedBankAccountNotFoundException and never touches the wallet when the bank account does not belong to the user.
    @Test
    void execute_linkedBankAccountNotFound_throwsLinkedBankAccountNotFoundException() {
        CashOutRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "50.00" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.empty() );

        assertThatThrownBy( () -> cashOutMoneyTransferaWalletService.execute( request ) )
                .isInstanceOf( LinkedBankAccountNotFoundException.class );

        verify( transferaWalletRepository, never() ).findByUserCredentialsEmail( any() );
        verify( transferaWalletRepository, never() ).save( any() );
    }

    @Test
    void execute_walletNotFound_throwsTransferaWalletNotFoundException() {
        // Verifies that the service throws TransferaWalletNotFoundException and never calls save when the wallet does not exist.
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        CashOutRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "50.00" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.empty() );

        assertThatThrownBy( () -> cashOutMoneyTransferaWalletService.execute( request ) )
                .isInstanceOf( TransferaWalletNotFoundException.class );

        verify( transferaWalletRepository, never() ).save( any() );
    }

    @Test
    void execute_amountExceedsBalance_throwsInsufficientBalanceException() {
        // Verifies that attempting to cash out more than the available balance throws InsufficientBalanceException and never saves.
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        TransferaWallet wallet = buildWallet( user, new BigDecimal( "30.00" ) );
        CashOutRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "50.00" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );

        assertThatThrownBy( () -> cashOutMoneyTransferaWalletService.execute( request ) )
                .isInstanceOf( InsufficientBalanceTransferaWalletException.class );

        verify( transferaWalletRepository, never() ).save( any() );
    }

    @Test
    void execute_exactBalance_cashesOutSuccessfully() {
        // Verifies that cashing out the exact full balance results in a zero balance — an important financial edge case.
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        TransferaWallet wallet = buildWallet( user, new BigDecimal( "100.00" ) );
        CashOutRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "100.00" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        ResponseEntity<TransferaWalletDTO> response = cashOutMoneyTransferaWalletService.execute( request );

        assertThat( response.getBody().getBalance() ).isEqualByComparingTo( BigDecimal.ZERO );
    }

    @Test
    void execute_savesWalletExactlyOnce() {
        // Verifies that the wallet repository save() is called exactly once per cash out request — no double saves.
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        TransferaWallet wallet = buildWallet( user, new BigDecimal( "200.00" ) );
        CashOutRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "100.00" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        cashOutMoneyTransferaWalletService.execute( request );

        verify( transferaWalletRepository, times( 1 ) ).save( wallet );
    }

    @Test
    void execute_decimalAmount_subtractsCorrectly() {
        // Verifies cent-level precision — subtracting $0.99 from $10.00 results in exactly $9.01.
        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );
        TransferaWallet wallet = buildWallet( user, new BigDecimal( "10.00" ) );
        CashOutRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "0.99" ) );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( ACCOUNT_ID, EMAIL ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transferaWalletRepository.save( any( TransferaWallet.class ) ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );

        ResponseEntity<TransferaWalletDTO> response = cashOutMoneyTransferaWalletService.execute( request );

        assertThat( response.getBody().getBalance() ).isEqualByComparingTo( new BigDecimal( "9.01" ) );
    }

    // ─── Concurrency Tests ────────────────────────────────────────────────────

    @Test
    void execute_concurrentCashOuts_allSucceed() throws InterruptedException {
        // Verifies that 10 simultaneous cash out requests from the same user all succeed with correct balances.
        int threadCount = 10;
        BigDecimal cashOutAmount = new BigDecimal( "10.00" );

        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( any(), any() ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( any() ) )
                .thenAnswer( inv -> Optional.of( buildWallet( user, new BigDecimal( "100.00" ) ) ) );
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

                    CashOutRequestDTO request = buildRequest( ACCOUNT_ID, cashOutAmount );
                    ResponseEntity<TransferaWalletDTO> response =
                            cashOutMoneyTransferaWalletService.execute( request );

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

        assertThat( successCount.get() ).isEqualTo( threadCount );
        assertThat( failureCount.get() ).isZero();

        results.forEach( balance ->
                assertThat( balance ).isEqualByComparingTo( new BigDecimal( "90.00" ) )
        );
    }

    @Test
    void execute_concurrentCashOutsDifferentUsers_isolatedCorrectly() throws InterruptedException {
        // Verifies that 5 concurrent users cashing out simultaneously receive correct isolated balances with no cross-contamination.
        int userCount = 5;
        BigDecimal cashOutAmount = new BigDecimal( "25.00" );
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

                    CashOutRequestDTO request = buildRequest( accountId, cashOutAmount );
                    ResponseEntity<TransferaWalletDTO> response =
                            cashOutMoneyTransferaWalletService.execute( request );

                    results.add( response.getBody().getBalance() );
                    successCount.incrementAndGet();
                } catch ( Exception e ) {
                    // silent
                } finally {
                    latch.countDown();
                    SecurityContextHolder.clearContext();
                }
            } );
        }

        latch.await( 10, TimeUnit.SECONDS );
        executor.shutdown();

        assertThat( successCount.get() ).isEqualTo( userCount );

        results.forEach( balance ->
                assertThat( balance ).isEqualByComparingTo( new BigDecimal( "75.00" ) )
        );
    }

    @Test
    void execute_concurrentCashOuts_repositoryCalledCorrectNumberOfTimes() throws InterruptedException {
        // Verifies that save() is called exactly once per thread under concurrency — no batching or skipping.
        int threadCount = 8;

        UserCredentials user = buildUser();
        LinkedBankAccount account = buildLinkedBankAccount( user );

        when( linkedBankAccountRepository.findByIdAndUserCredentialsEmail( any(), any() ) )
                .thenReturn( Optional.of( account ) );
        when( transferaWalletRepository.findByUserCredentialsEmail( any() ) )
                .thenAnswer( inv -> Optional.of( buildWallet( user, new BigDecimal( "100.00" ) ) ) );
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

                    CashOutRequestDTO request = buildRequest( ACCOUNT_ID, new BigDecimal( "10.00" ) );
                    cashOutMoneyTransferaWalletService.execute( request );
                } finally {
                    latch.countDown();
                    SecurityContextHolder.clearContext();
                }
            } );
        }

        latch.await( 10, TimeUnit.SECONDS );
        executor.shutdown();

        verify( transferaWalletRepository, times( threadCount ) ).save( any( TransferaWallet.class ) );
    }
}