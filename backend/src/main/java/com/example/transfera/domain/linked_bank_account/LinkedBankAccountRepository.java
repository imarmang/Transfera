package com.example.transfera.domain.linked_bank_account;

import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LinkedBankAccountRepository extends JpaRepository<LinkedBankAccount, UUID> {

    Optional<List<LinkedBankAccount>> findByUserCredentialsEmail( String email );
    Optional<LinkedBankAccount> findByIdAndUserCredentialsEmail(UUID id, String email ) ;

    boolean existsByAccountNumberAndUserCredentialsEmail(String accountNumber, String email );
}
