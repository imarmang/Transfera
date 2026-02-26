package com.example.transfera.service.user;

import com.example.transfera.Query;
import com.example.transfera.domain.user.User;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.dto.UserDTO.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUsersCredentialsService implements Query<Void, List<UserDTO>> {

    private final UserCredentialsRepository userCredentialsRepository;

    public GetUsersCredentialsService( UserCredentialsRepository userCredentialsRepository ) {
        this.userCredentialsRepository = userCredentialsRepository;
    }

    @Override
    public ResponseEntity<List<UserDTO>> execute( Void input ) {
        List<User>  users = userCredentialsRepository.findAll();
        List<UserDTO> userDTOS = users.stream().map( UserDTO:: new ).toList();

        return ResponseEntity.status( HttpStatus.OK ).body( userDTOS );
    }
}
