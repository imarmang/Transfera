package com.example.transfera.service.transaction;

import com.example.transfera.domain.transaction.Transaction;
import com.example.transfera.domain.transaction.TransactionRepository;
import com.example.transfera.domain.transaction.TransactionStatus;
import com.example.transfera.domain.transaction.TransactionType;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith( MockitoExtension.class )
class CreateTransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CreateTransactionService createTransactionService;

    // ─── Helper builders ──────────────────────────────────────────────────────

    private TransferaWallet buildWallet() {
        TransferaWallet wallet = new TransferaWallet();
        wallet.setWalletNumber( "1234567890" );
        wallet.setBalance( new BigDecimal( "100.00" ) );
        return wallet;
    }

    // ─── Unit Tests ───────────────────────────────────────────────────────────

    @Test
    void execute_happyPath_savesTransaction() {
        // Verifies that execute() calls repository.save() exactly once with the provided transaction.
        Transaction transaction = Transaction.builder()
                .transferaWallet( buildWallet() )
                .amount( new BigDecimal( "50.00" ) )
                .type( TransactionType.ADD_MONEY )
                .status( TransactionStatus.PENDING )
                .build();

        createTransactionService.execute( transaction );

        verify( transactionRepository, times( 1 ) ).save( transaction );
    }

    @Test
    void execute_savesExactTransactionPassed() {
        // Verifies that the exact transaction object passed in is what gets saved — no mutation.
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass( Transaction.class );

        Transaction transaction = Transaction.builder()
                .transferaWallet( buildWallet() )
                .amount( new BigDecimal( "25.00" ) )
                .type( TransactionType.CASH_OUT )
                .status( TransactionStatus.PENDING )
                .build();

        createTransactionService.execute( transaction );

        verify( transactionRepository ).save( captor.capture() );
        assertThat( captor.getValue().getAmount() ).isEqualByComparingTo( new BigDecimal( "25.00" ) );
        assertThat( captor.getValue().getType() ).isEqualTo( TransactionType.CASH_OUT );
        assertThat( captor.getValue().getStatus() ).isEqualTo( TransactionStatus.PENDING );
    }

    @Test
    void execute_neverCallsSaveMoreThanOnce() {
        // Verifies that save() is called exactly once — no accidental double saves.
        Transaction transaction = Transaction.builder()
                .transferaWallet( buildWallet() )
                .amount( new BigDecimal( "10.00" ) )
                .type( TransactionType.ADD_MONEY )
                .status( TransactionStatus.PENDING )
                .build();

        createTransactionService.execute( transaction );

        verify( transactionRepository, times( 1 ) ).save( any( Transaction.class ) );
    }

    @Test
    void execute_addMoneyTransaction_savesCorrectType() {
        // Verifies ADD_MONEY type is preserved correctly.
        Transaction transaction = Transaction.builder()
                .transferaWallet( buildWallet() )
                .amount( new BigDecimal( "50.00" ) )
                .type( TransactionType.ADD_MONEY )
                .status( TransactionStatus.PENDING )
                .build();

        createTransactionService.execute( transaction );

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass( Transaction.class );
        verify( transactionRepository ).save( captor.capture() );
        assertThat( captor.getValue().getType() ).isEqualTo( TransactionType.ADD_MONEY );
    }

    @Test
    void execute_sendTransaction_savesCorrectPeerName() {
        // Verifies peer name is preserved correctly for SEND transactions.
        Transaction transaction = Transaction.builder()
                .transferaWallet( buildWallet() )
                .amount( new BigDecimal( "25.00" ) )
                .type( TransactionType.SEND )
                .status( TransactionStatus.PENDING )
                .peerName( "Joe Smith" )
                .build();

        createTransactionService.execute( transaction );

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass( Transaction.class );
        verify( transactionRepository ).save( captor.capture() );
        assertThat( captor.getValue().getPeerName() ).isEqualTo( "Joe Smith" );
    }
}