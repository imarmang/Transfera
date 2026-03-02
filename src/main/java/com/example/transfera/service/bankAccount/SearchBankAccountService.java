/*
might be an overkill class, will check later
 */

//package com.example.transfera.service.accounts;
//
//import com.example.transfera.Query;
//import com.example.transfera.domain.account.BankAccountRepository;
//import com.example.transfera.dto.bankaccount.BankAccountDTO;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//// returns a specific account that matches a name, might be an overkill class
//@Service
//public class SearchBankAccountService implements Query<String, BankAccountDTO> {
//
//    private final BankAccountRepository bankAccountRepository;
//
//    public SearchBankAccountService( BankAccountRepository bankAccountRepository ) {
//        this.bankAccountRepository = bankAccountRepository;
//    }
//
//    @Override
//    public ResponseEntity<List<BankAccountDTO>> execute( String input ) {
//        return ResponseEntity.ok( bankAccountRepository.findByNameOrDescriptionContaining( name )
//                .stream()
//                .map( BankAccountDTO::new )
//                .toList() );
//    }
//
//}
