package com.example.transfera.service.userCredentials;

import com.example.transfera.Query;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.dto.UserDTO.UserCredentialsResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


/// RETURNS ALL THE USERNAMES AND THEIR ID NUMBERS FROM THE DB, DOESN'T RETURN ANY PASSWORD
@Service
public class GetUsersCredentialsService implements Query<Void, List<UserCredentialsResponseDTO>> {

    private final UserCredentialsRepository userCredentialsRepository;

    public GetUsersCredentialsService( UserCredentialsRepository userCredentialsRepository ) {
        this.userCredentialsRepository = userCredentialsRepository;
    }

    @Override
    public ResponseEntity<List<UserCredentialsResponseDTO>> execute( Void input ) {
        List<UserCredentials> userCredentials = userCredentialsRepository.findAll();
        List<UserCredentialsResponseDTO> userCredentialsResponseDTOS = userCredentials.stream().map( UserCredentialsResponseDTO:: new ).toList();

        return ResponseEntity.status( HttpStatus.OK ).body( userCredentialsResponseDTOS );
    }
}
