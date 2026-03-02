package com.example.transfera.service.userCredential;

import com.example.transfera.Command;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.exceptions.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeleteUserCredentialsService implements Command<UUID, Void> {

    private final UserCredentialsRepository userCredentialsRepository;

    public DeleteUserCredentialsService( UserCredentialsRepository userCredentialsRepository ) {
        this.userCredentialsRepository = userCredentialsRepository;
    }

    @Override
    public ResponseEntity<Void> execute( UUID input ) {
        Optional<UserCredentials> user = userCredentialsRepository.findById( input );

        if ( user.isPresent() ) {
            userCredentialsRepository.deleteById( input );
            return ResponseEntity.status( HttpStatus.NO_CONTENT ).build();

        }
        throw new UserNotFound();
    }
}
