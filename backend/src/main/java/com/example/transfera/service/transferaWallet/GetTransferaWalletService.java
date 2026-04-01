package com.example.transfera.service.transferaWallet;

import com.example.transfera.Query;
import com.example.transfera.domain.transfera_wallet.TransferaWallet;
import com.example.transfera.domain.transfera_wallet.TransferaWalletRepository;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.dto.TransferaWalletDTO.TransferaWalletDTO;
import com.example.transfera.exceptions.customExceptions.TransferaWalletNotFoundException;
import com.example.transfera.exceptions.customExceptions.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class GetTransferaWalletService implements Query<Void, TransferaWalletDTO> {

    private final TransferaWalletRepository transferaWalletRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final CreateTransferaWalletService createTransferaWalletService;

    public GetTransferaWalletService( TransferaWalletRepository transferaWalletRepository,
                                      UserCredentialsRepository userCredentialsRepository,
                                      CreateTransferaWalletService createTransferaWalletService ) {
        this.transferaWalletRepository = transferaWalletRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.createTransferaWalletService = createTransferaWalletService;
    }

    @Override
    public ResponseEntity<TransferaWalletDTO> execute( Void input ) {
        String email = ( String ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<TransferaWallet> transferaWalletOptional = transferaWalletRepository.findByUserCredentialsEmail( email );

        if ( transferaWalletOptional.isPresent() ) {
            return ResponseEntity.ok( new TransferaWalletDTO( transferaWalletOptional.get() ) );
        }

        // Wallet missing — create it automatically as a fallback
        UserCredentials userCredentials = userCredentialsRepository
                .findByEmail( email )
                .orElseThrow( UserNotFound::new );

        TransferaWallet wallet = createTransferaWalletService.execute( userCredentials );
        return ResponseEntity.status( HttpStatus.CREATED ).body( new TransferaWalletDTO( wallet ) );
    }
}
