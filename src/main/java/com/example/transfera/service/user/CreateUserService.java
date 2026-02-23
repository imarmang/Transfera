package com.example.transfera.service.user;

import com.example.transfera.Command;
import com.example.transfera.domain.user.User;
import com.example.transfera.domain.user.UserRepository;
import com.example.transfera.dto.UserDTO.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CreateUserService implements Command<User, UserDTO> {

    private final UserRepository userRepository;

    public CreateUserService( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<UserDTO> execute( User user ) {
        User savedUser = userRepository.save( user );
        return ResponseEntity.status( HttpStatus.CREATED ).body( new UserDTO( savedUser ));
    }
}
