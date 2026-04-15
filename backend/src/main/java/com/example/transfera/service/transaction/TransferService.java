//package com.example.transfera.service.transfer;
//
//import com.example.transfera.Command;
//import com.example.transfera.domain.account.TransferaWallet;
//import com.example.transfera.domain.account.TransferaWalletRepository;
//import com.example.transfera.dto.TransferDTO;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Service
//@Transactional
//public class TransferService implements Command<TransferDTO, String> {
//
//    private final TransferaWalletRepository bankAccountRepository;
//
//    public TransferService( TransferaWalletRepository bankAccountRepository ) {
//        this.bankAccountRepository = bankAccountRepository;
//    }
//
//
//    @Override
//    public ResponseEntity<String> execute( TransferDTO transfer ) {
//
//        Optional<TransferaWallet> fromAccount = bankAccountRepository.findById( transfer.getFromUser() );
//        Optional<TransferaWallet> toAccount = bankAccountRepository.findById( transfer.getToUser() );
//
//        if ( fromAccount.isEmpty() || toAccount.isEmpty() ) {
//            throw new RuntimeException( "User is not empty" );
//        }
//        TransferaWallet from = fromAccount.get();
//        TransferaWallet to = toAccount.get();
//
//        // add & subtract
//        add( to, transfer.getAmount() );
//        // at this point -> have added new money but not checked if enough to transfer
//        System.out.println( "After adding, before deducting" );
//        System.out.println( bankAccountRepository.findById( to.getName() ) );  // this would be better as a logging statement
//
//        deduct( from, transfer.getAmount() );
//
//        return ResponseEntity.ok().build();
//
//    }
//
//    private void deduct( TransferaWallet bankAccount, double amount ) {
//        if ( bankAccount.getBalance() < amount ) {
//            throw new RuntimeException( "Not Enough Money" );
//        }
//
//        bankAccount.setBalance( bankAccount.getBalance() - amount );
//    }
//
//    private void add( TransferaWallet bankAccount, double amount ) {
//        bankAccount.setBalance( bankAccount.getBalance() + amount );
//    }
//}
