package com.example.transfera.service.user;

import com.example.transfera.Command;
import com.example.transfera.domain.user.User;
import com.example.transfera.domain.user.UserRepository;
import com.example.transfera.exceptions.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeleteUserService implements Command<UUID, Void> {

    private final UserRepository userRepository;

    public DeleteUserService( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<Void> execute( UUID input ) {
        Optional<User> user = userRepository.findById( input );

        if ( user.isPresent() ) {
            userRepository.deleteById( input );
            return ResponseEntity.status( HttpStatus.NO_CONTENT ).build();

        }
        throw new UserNotFound();
    }
}
