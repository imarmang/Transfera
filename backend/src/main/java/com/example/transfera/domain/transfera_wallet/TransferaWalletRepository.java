package com.example.transfera.domain.transfera_wallet;

import com.example.transfera.domain.linked_bank_account.LinkedBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferaWalletRepository extends JpaRepository<TransferaWallet, UUID> {
    Optional<TransferaWallet> findByUserCredentialsEmail( String email );
}
