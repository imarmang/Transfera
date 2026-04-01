package com.example.transfera.service.userCredential;

import com.example.transfera.Command;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.dto.UserDTO.CreateUserRequestDTO;
import com.example.transfera.dto.UserDTO.UserCredentialsResponseDTO;
import com.example.transfera.service.transferaWallet.CreateTransferaWalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserCredentialsService implements Command<CreateUserRequestDTO, UserCredentialsResponseDTO> {

    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder           passwordEncoder;
    private final CreateTransferaWalletService createTransferaWalletService;


    public CreateUserCredentialsService( UserCredentialsRepository userCredentialsRepository, PasswordEncoder passwordEncoder, CreateTransferaWalletService createTransferaWalletService) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
        this.createTransferaWalletService = createTransferaWalletService;
    }

    @Override
    public ResponseEntity<UserCredentialsResponseDTO> execute( CreateUserRequestDTO request ) {

        if ( request.getPassword() == null || request.getPassword().isBlank() ) {
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).build();
        }

        // Check if the email already exists
        if ( userCredentialsRepository.existsByEmail( request.getEmail() ) ) {
            return ResponseEntity.status( HttpStatus.CONFLICT ).build();
        }

        // Hash the password before saving
        UserCredentials savedUserCredentials = userCredentialsRepository.save(
                new UserCredentials( request.getEmail(), passwordEncoder.encode( request.getPassword() ) )
        );

        createTransferaWalletService.execute( savedUserCredentials );

        return ResponseEntity.status( HttpStatus.CREATED ).body( new UserCredentialsResponseDTO( savedUserCredentials ));
    }
}
