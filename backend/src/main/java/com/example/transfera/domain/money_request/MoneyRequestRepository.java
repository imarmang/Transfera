package com.example.transfera.domain.money_request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, UUID> {

    List<MoneyRequest> findAllByRequesterWallet_IdOrPayerWallet_Id( UUID requesterWalletId, UUID payerWalletId );

}
