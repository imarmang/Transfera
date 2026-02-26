package com.example.transfera.service.user;

import com.example.transfera.Command;
import com.example.transfera.domain.user.User;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.dto.UserDTO.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CreateUserCredentialsService implements Command<User, UserDTO> {

    private final UserCredentialsRepository userCredentialsRepository;

    public CreateUserCredentialsService( UserCredentialsRepository userCredentialsRepository ) {
        this.userCredentialsRepository = userCredentialsRepository;
    }

    @Override
    public ResponseEntity<UserDTO> execute( User user ) {
        User savedUser = userCredentialsRepository.save( user );
        return ResponseEntity.status( HttpStatus.CREATED ).body( new UserDTO( savedUser ));
    }
}
