package com.example.transfera.service.transaction;

import com.example.transfera.domain.linked_bank_account.LinkedBankAccount;
import com.example.transfera.domain.transaction.Transaction;
import com.example.transfera.domain.transaction.TransactionRepository;
import com.example.transfera.domain.transaction.TransactionStatus;
import com.example.transfera.domain.transaction.TransactionType;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.dto.TransactionDTO.TransactionDTO;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith( MockitoExtension.class )
class GetTransactionHistoryServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransferaWalletRepository transferaWalletRepository;

    @InjectMocks
    private GetTransactionsHistoryService getTransactionsHistoryService;

    private static final String EMAIL = "tatevik@example.com";
    private static final UUID WALLET_ID = UUID.randomUUID();

    // ─── Setup ────────────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken( EMAIL, null, null )
        );
    }

    // ─── Helper builders ──────────────────────────────────────────────────────

    private TransferaWallet buildWallet() {
        TransferaWallet wallet = new TransferaWallet();
        wallet.setId( WALLET_ID );
        wallet.setWalletNumber( "1234567890" );
        wallet.setBalance( new BigDecimal( "100.00" ) );
        wallet.setUserCredentials( new UserCredentials( EMAIL, "password" ) );
        return wallet;
    }

    private LinkedBankAccount buildLinkedBankAccount() {
        UserCredentials user = new UserCredentials( EMAIL, "password" );
        return new LinkedBankAccount(
                user,
                "Bank of America",
                "Tatevik Test",
                "123456789",
                "021000021",
                "CHECKING"
        );
    }

    private Transaction buildAddMoneyTransaction( TransferaWallet wallet ) {
        return Transaction.builder()
                .transferaWallet( wallet )
                .amount( new BigDecimal( "50.00" ) )
                .type( TransactionType.ADD_MONEY )
                .status( TransactionStatus.PENDING )
                .linkedBankAccount( buildLinkedBankAccount() )
                .build();
    }

    private Transaction buildSendTransaction( TransferaWallet wallet ) {
        return Transaction.builder()
                .transferaWallet( wallet )
                .amount( new BigDecimal( "25.00" ) )
                .type( TransactionType.SEND )
                .status( TransactionStatus.PENDING )
                .peerName( "Joe Smith" )
                .build();
    }

    // ─── Unit Tests ───────────────────────────────────────────────────────────

    @Test
    void execute_happyPath_returnsTransactionList() {
        // Verifies that a user with two transactions gets both returned correctly.
        TransferaWallet wallet = buildWallet();
        List<Transaction> transactions = List.of(
                buildAddMoneyTransaction( wallet ),
                buildSendTransaction( wallet )
        );

        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transactionRepository.findAllByTransferaWallet_IdOrderByCreatedAtDesc( WALLET_ID ) )
                .thenReturn( transactions );

        ResponseEntity<List<TransactionDTO>> response = getTransactionsHistoryService.execute( null );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).hasSize( 2 );
    }

    @Test
    void execute_emptyHistory_returnsEmptyList() {
        // Verifies that a user with no transactions gets an empty list, not an error.
        TransferaWallet wallet = buildWallet();

        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transactionRepository.findAllByTransferaWallet_IdOrderByCreatedAtDesc( WALLET_ID ) )
                .thenReturn( List.of() );

        ResponseEntity<List<TransactionDTO>> response = getTransactionsHistoryService.execute( null );

        assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).isEmpty();
    }

    @Test
    void execute_walletNotFound_throwsTransferaWalletNotFoundException() {
        // Verifies that a missing wallet throws the correct exception before touching the transaction repository.
        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.empty() );

        assertThatThrownBy( () -> getTransactionsHistoryService.execute( null ) )
                .isInstanceOf( TransferaWalletNotFoundException.class );

        verify( transactionRepository, never() )
                .findAllByTransferaWallet_IdOrderByCreatedAtDesc( any() );
    }

    @Test
    void execute_mapsAddMoneyTransactionCorrectly() {
        // Verifies that an ADD_MONEY transaction is mapped with correct type, amount, and bank details.
        TransferaWallet wallet = buildWallet();
        Transaction transaction = buildAddMoneyTransaction( wallet );

        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transactionRepository.findAllByTransferaWallet_IdOrderByCreatedAtDesc( WALLET_ID ) )
                .thenReturn( List.of( transaction ) );

        ResponseEntity<List<TransactionDTO>> response = getTransactionsHistoryService.execute( null );

        TransactionDTO dto = response.getBody().get( 0 );
        assertThat( dto.getTransactionType() ).isEqualTo( TransactionType.ADD_MONEY );
        assertThat( dto.getAmount() ).isEqualByComparingTo( new BigDecimal( "50.00" ) );
        assertThat( dto.getBankName() ).isEqualTo( "Bank of America" );
        assertThat( dto.getLastFourDigits() ).isEqualTo( "6789" );
        assertThat( dto.getPeerName() ).isNull();
    }

    @Test
    void execute_mapsSendTransactionCorrectly() {
        // Verifies that a SEND transaction is mapped with correct peer name and no bank details.
        TransferaWallet wallet = buildWallet();
        Transaction transaction = buildSendTransaction( wallet );

        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transactionRepository.findAllByTransferaWallet_IdOrderByCreatedAtDesc( WALLET_ID ) )
                .thenReturn( List.of( transaction ) );

        ResponseEntity<List<TransactionDTO>> response = getTransactionsHistoryService.execute( null );

        TransactionDTO dto = response.getBody().get( 0 );
        assertThat( dto.getTransactionType() ).isEqualTo( TransactionType.SEND );
        assertThat( dto.getPeerName() ).isEqualTo( "Joe Smith" );
        assertThat( dto.getBankName() ).isNull();
        assertThat( dto.getLastFourDigits() ).isNull();
    }

    @Test
    void execute_usesCorrectWalletIdForQuery() {
        // Verifies the repository is queried with the wallet ID derived from the JWT, not a hardcoded value.
        TransferaWallet wallet = buildWallet();

        when( transferaWalletRepository.findByUserCredentialsEmail( EMAIL ) )
                .thenReturn( Optional.of( wallet ) );
        when( transactionRepository.findAllByTransferaWallet_IdOrderByCreatedAtDesc( WALLET_ID ) )
                .thenReturn( List.of() );

        getTransactionsHistoryService.execute( null );

        verify( transactionRepository ).findAllByTransferaWallet_IdOrderByCreatedAtDesc( WALLET_ID );
    }
}